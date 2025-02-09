package com.example.quizapp4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    // Score bijhouden
    int answerTotal = 0;
    int answerCorrect = 0;
    Random rand = new Random();
    String correctName;
    int idCorrect;
    int idCurrent;

    // UI-elementen
    TextView textScore;
    TextView textResult;
    Button buttonSubmit;
    Button buttonEnd;
    Button buttonNext;
    RadioButton buttonOpt1;
    RadioButton buttonOpt2;
    RadioButton buttonOpt3;
    ArrayList<RadioButton> optButtons;
    RadioGroup radioGroup;
    ImageView image;

    // Lijst met quiz-items (door MainActivity meegegeven)
    ArrayList<gallerymodel> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Zorg dat de content rekening houdt met de system insets (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Haal de lijst op die vanuit MainActivity is meegegeven
        images = (ArrayList<gallerymodel>) getIntent().getSerializableExtra("galleryList");
        // Indien de lijst ontbreekt of minder dan 3 items bevat, gebruik dan de fallback-lijst
        if (images == null || images.size() < 3) {
            images = new ArrayList<>();
            images.add(new gallerymodel("Aegon", R.drawable.eagon));
            images.add(new gallerymodel("Hank", R.drawable.hank));
            images.add(new gallerymodel("Lia", R.drawable.lia));
        }

        // Koppel de UI-elementen via findViewById
        textScore = findViewById(R.id.textQuizScore);
        textResult = findViewById(R.id.textQuizResult);
        buttonEnd = findViewById(R.id.buttonQuizEnd);
        buttonNext = findViewById(R.id.buttonQuizNext);
        buttonSubmit = findViewById(R.id.buttonQuizSubmit);
        buttonOpt1 = findViewById(R.id.buttonQuizOption1);
        buttonOpt2 = findViewById(R.id.buttonQuizOption2);
        buttonOpt3 = findViewById(R.id.buttonQuizOption3);
        image = findViewById(R.id.imageView);
        radioGroup = findViewById(R.id.radioQuizAnsweres);

        // Voeg de RadioButtons toe aan een lijst (zodat we later de correcte knop kunnen bepalen)
        optButtons = new ArrayList<>();
        optButtons.add(buttonOpt1);
        optButtons.add(buttonOpt2);
        optButtons.add(buttonOpt3);

        // Luister naar veranderingen in de RadioGroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                idCurrent = checkedId;
            }
        });

        // Acties voor de knoppen
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAfterAnswer();
                updateScore();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQuestion();
                hideBeforeAnswer();
            }
        });

        buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Start de quiz
        hideBeforeAnswer();
        generateQuestion();
    }

    // Genereert een nieuwe vraag:
    protected void generateQuestion() {
        int size = images.size();
        if (size < 3) return; // Veiligheidscheck

        // 1. Kies willekeurig één item als de vraag (de foto die getoond wordt)
        int correctIndex = rand.nextInt(size);
        gallerymodel correctItem = images.get(correctIndex);
        correctName = correctItem.getNameOfDog();

        // 2. Kies twee afleiders (distractoren) die niet hetzelfde zijn als het correcte item
        int distractor1Index, distractor2Index;
        do {
            distractor1Index = rand.nextInt(size);
        } while (distractor1Index == correctIndex);
        do {
            distractor2Index = rand.nextInt(size);
        } while (distractor2Index == correctIndex || distractor2Index == distractor1Index);

        String distractor1 = images.get(distractor1Index).getNameOfDog();
        String distractor2 = images.get(distractor2Index).getNameOfDog();

        // 3. Maak een lijst met opties: de correcte naam en de twee distractoren
        ArrayList<String> options = new ArrayList<>();
        options.add(correctName);
        options.add(distractor1);
        options.add(distractor2);

        // Schud de opties zodat de correcte niet altijd op dezelfde plek staat
        Collections.shuffle(options);

        // 4. Wijs de opties toe aan de RadioButtons
        buttonOpt1.setText(options.get(0));
        buttonOpt2.setText(options.get(1));
        buttonOpt3.setText(options.get(2));

        // 5. Bepaal welke RadioButton de correcte optie bevat
        int correctOptionIndex = options.indexOf(correctName);
        idCorrect = optButtons.get(correctOptionIndex).getId();

        // 6. Toon de foto van het correcte item
        Uri uri = correctItem.getImageUri();
        if (uri != null) {
            image.setImageURI(uri);
        } else {
            image.setImageResource(correctItem.getImageResource());
        }

        // Zorg dat er geen vooraf geselecteerd antwoord is
        radioGroup.clearCheck();
    }

    // Controleert of het gekozen antwoord correct is
    protected boolean wasCorrect() {
        return idCorrect == idCurrent;
    }

    // Werkt de score bij en toont een resultaatbericht
    protected void updateScore() {
        answerTotal++;
        if (wasCorrect()) {
            textResult.setText(R.string.correct);
            answerCorrect++;
        } else {
            textResult.setText(getString(R.string.incorrect, correctName));
        }
        String percentage = String.format("%.1f %%", answerCorrect * 100.0 / answerTotal);
        textScore.setText(getString(R.string.quiz_score, answerCorrect, answerTotal, percentage));
    }

    // Verbergt elementen vóór het beantwoorden
    protected void hideBeforeAnswer() {
        textResult.setVisibility(View.INVISIBLE);
        buttonEnd.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
        buttonSubmit.setVisibility(View.VISIBLE);
    }

    // Toont elementen nadat een antwoord is gegeven
    protected void showAfterAnswer() {
        textResult.setVisibility(View.VISIBLE);
        buttonEnd.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        buttonSubmit.setVisibility(View.INVISIBLE);
    }
}
