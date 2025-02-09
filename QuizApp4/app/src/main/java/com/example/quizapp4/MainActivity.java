package com.example.quizapp4;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

    // List of existing and user-added items
    ArrayList<gallerymodel> galleryModels = new ArrayList<>();

    // Predefined drawable images
    int[] dogImages = {R.drawable.eagon, R.drawable.hank, R.drawable.lia};

    Button btnPickImage;
    Button sortButton;
    Button quizButton; // Button to go to the quiz
    Dog_recyclerviewadapter adapter;
    ActivityResultLauncher<Intent> resultLauncher;

    // Boolean to track the sorting order: true = A-Z, false = Z-A
    boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("QUIZAPPMAIN", "onCreate started");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ensure that the system insets are set correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the RecyclerView and the buttons in the layout
        RecyclerView recyclerView = findViewById(R.id.rycyclerview);
        btnPickImage = findViewById(R.id.btnPickImage);
        sortButton = findViewById(R.id.Sortbutton);
        quizButton = findViewById(R.id.Quizbutton);

        // Add the predefined items

        if (getIntent().getSerializableExtra("galleryList") != null) {
            galleryModels = (ArrayList<gallerymodel>) getIntent().getSerializableExtra("galleryList");
        }
        if (galleryModels.isEmpty()) {
            setUpgalleryModels();
        }

        // Create and set up the adapter
        adapter = new Dog_recyclerviewadapter(this, galleryModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Register the ActivityResultLauncher to retrieve images
        registerResult();

        // Click listener for the button to pick an image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            btnPickImage.setOnClickListener(view -> pickImage());
        }

        // Click listener for the sort button: toggles between A-Z and Z-A
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortGalleryModels();
            }
        });

        // Click listener for the quiz button: starts QuizActivity and passes the full list
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quizIntent = new Intent(MainActivity.this, QuizActivity.class);
                // The gallerymodel class must implement Serializable!
                quizIntent.putExtra("galleryList", galleryModels);
                startActivity(quizIntent);
            }
        });
    }

    // Sorts the list based on the current sorting order and then toggles the boolean
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
        // Toggle the sorting order for the next time
        sortAscending = !sortAscending;
        adapter.notifyDataSetChanged();
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // Adds the predefined items to the list
    private void setUpgalleryModels() {
        String[] dogNames = getResources().getStringArray(R.array.Dog_full_names);
        for (int i = 0; i < dogNames.length; i++) {
            galleryModels.add(new gallerymodel(dogNames[i], dogImages[i]));
        }
    }

    // Registers the ActivityResultLauncher to receive the result of the image selection
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
                            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    // Shows a dialog where the user can enter a name for the image
    private void showNameDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter a name for the image");

        final EditText input = new EditText(MainActivity.this);
        input.setHint("Name");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    galleryModels.add(new gallerymodel(name, imageUri));
                    adapter.notifyItemInserted(galleryModels.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Name must not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
