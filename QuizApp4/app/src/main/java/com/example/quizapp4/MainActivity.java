package com.example.quizapp4;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    // Lijst met bestaande en door de gebruiker toegevoegde items
    ArrayList<gallerymodel> galleryModels = new ArrayList<>();

    // Vooraf gedefinieerde drawable afbeeldingen
    int[] dogImages = {R.drawable.eagon, R.drawable.hank, R.drawable.lia};

    Button btnPickImage;
    Button sortButton;
    Button quizButton; // Knop om naar de quiz te gaan
    Dog_recyclerviewadapter adapter;
    ActivityResultLauncher<Intent> resultLauncher;

    // Boolean om de sorteerorde bij te houden: true = A-Z, false = Z-A
    boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Zorg dat de system insets correct worden ingesteld
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vind de RecyclerView en de knoppen in de layout
        RecyclerView recyclerView = findViewById(R.id.rycyclerview);
        btnPickImage = findViewById(R.id.btnPickImage);
        sortButton = findViewById(R.id.Sortbutton);
        quizButton = findViewById(R.id.Quizbutton);

        // Voeg de vooraf gedefinieerde items toe
        setUpgalleryModels();

        // Maak en stel de adapter in
        adapter = new Dog_recyclerviewadapter(this, galleryModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Registreer de ActivityResultLauncher voor het ophalen van afbeeldingen
        registerResult();

        // Kliklistener voor de knop om een afbeelding te kiezen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            btnPickImage.setOnClickListener(view -> pickImage());
        }

        // Kliklistener voor de sorteer-knop: togglet tussen A-Z en Z-A
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortGalleryModels();
            }
        });

        // Kliklistener voor de quiz-knop: start QuizActivity en geef de volledige lijst mee
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quizIntent = new Intent(MainActivity.this, QuizActivity.class);
                // De klasse gallerymodel moet Serializable implementeren!
                quizIntent.putExtra("galleryList", galleryModels);
                startActivity(quizIntent);
            }
        });
    }

    // Sorteert de lijst op basis van de huidige sorteerorde en togglet daarna de boolean
    private void sortGalleryModels() {
        if (sortAscending) {
            Collections.sort(galleryModels, new Comparator<gallerymodel>() {
                @Override
                public int compare(gallerymodel o1, gallerymodel o2) {
                    return o1.getNameOfDog().compareToIgnoreCase(o2.getNameOfDog());
                }
            });
        } else {
            Collections.sort(galleryModels, new Comparator<gallerymodel>() {
                @Override
                public int compare(gallerymodel o1, gallerymodel o2) {
                    return o2.getNameOfDog().compareToIgnoreCase(o1.getNameOfDog());
                }
            });
        }
        // Toggle de sorteerorde voor de volgende keer
        sortAscending = !sortAscending;
        adapter.notifyDataSetChanged();
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // Voeg de vooraf gedefinieerde items toe aan de lijst
    private void setUpgalleryModels() {
        String[] dogNames = getResources().getStringArray(R.array.Dog_full_names);
        for (int i = 0; i < dogNames.length; i++) {
            galleryModels.add(new gallerymodel(dogNames[i], dogImages[i]));
        }
    }

    // Registreer de ActivityResultLauncher om het resultaat van de afbeeldingkeuze op te vangen
    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            showNameDialog(imageUri);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Geen afbeelding geselecteerd", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    // Laat een dialoog zien waarin de gebruiker een naam kan ingeven voor de foto
    private void showNameDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Voer een naam in voor de foto");

        final EditText input = new EditText(MainActivity.this);
        input.setHint("Naam");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String naam = input.getText().toString().trim();
                if (!naam.isEmpty()) {
                    galleryModels.add(new gallerymodel(naam, imageUri));
                    adapter.notifyItemInserted(galleryModels.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Naam mag niet leeg zijn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Annuleren", null);
        builder.show();
    }
}
