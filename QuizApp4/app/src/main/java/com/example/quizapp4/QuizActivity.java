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

    // Keep track of the score
    int answerTotal = 0;
    int answerCorrect = 0;
    Random rand = new Random();
    String correctName;
    int idCorrect;
    int idCurrent;

    // UI elements
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

    // List of quiz items (passed from MainActivity)
    ArrayList<gallerymodel> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Ensure the content accounts for system insets (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the list passed from MainActivity
        images = (ArrayList<gallerymodel>) getIntent().getSerializableExtra("galleryList");
        // If the list is missing or contains fewer than 3 items, use the fallback list
        if (images == null || images.size() < 3) {
            images = new ArrayList<>();
            images.add(new gallerymodel("Aegon", R.drawable.eagon));
            images.add(new gallerymodel("Hank", R.drawable.hank));
            images.add(new gallerymodel("Lia", R.drawable.lia));
        }

        // Bind the UI elements using findViewById
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

        // Add the RadioButtons to a list (so we can later determine the correct button)
        optButtons = new ArrayList<>();
        optButtons.add(buttonOpt1);
        optButtons.add(buttonOpt2);
        optButtons.add(buttonOpt3);

        // Listen for changes in the RadioGroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                idCurrent = checkedId;
            }
        });

        // Actions for the buttons
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

        // Start the quiz
        hideBeforeAnswer();
        generateQuestion();
    }

    // Generates a new question:
    protected void generateQuestion() {
        int size = images.size();
        if (size < 3) return; // Safety check

        // 1. Randomly choose one item as the question (the photo to be shown)
        int correctIndex = rand.nextInt(size);
        gallerymodel correctItem = images.get(correctIndex);
        correctName = correctItem.getNameOfDog();

        // 2. Choose two distractors that are not the same as the correct item
        int distractor1Index, distractor2Index;
        do {
            distractor1Index = rand.nextInt(size);
        } while (distractor1Index == correctIndex);
        do {
            distractor2Index = rand.nextInt(size);
        } while (distractor2Index == correctIndex || distractor2Index == distractor1Index);

        String distractor1 = images.get(distractor1Index).getNameOfDog();
        String distractor2 = images.get(distractor2Index).getNameOfDog();

        // 3. Create a list of options: the correct name and the two distractors
        ArrayList<String> options = new ArrayList<>();
        options.add(correctName);
        options.add(distractor1);
        options.add(distractor2);

        // Shuffle the options so that the correct one is not always in the same position
        Collections.shuffle(options);

        // 4. Assign the options to the RadioButtons
        buttonOpt1.setText(options.get(0));
        buttonOpt2.setText(options.get(1));
        buttonOpt3.setText(options.get(2));

        // 5. Determine which RadioButton contains the correct option
        int correctOptionIndex = options.indexOf(correctName);
        idCorrect = optButtons.get(correctOptionIndex).getId();

        // 6. Display the photo of the correct item
        Uri uri = correctItem.getImageUri();
        if (uri != null) {
            image.setImageURI(uri);
        } else {
            image.setImageResource(correctItem.getImageResource());
        }

        // Ensure that no answer is pre-selected
        radioGroup.clearCheck();
    }

    // Checks whether the chosen answer is correct
    protected boolean wasCorrect() {
        return idCorrect == idCurrent;
    }

    // Updates the score and displays a result message
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

    // Hides elements before an answer is given
    protected void hideBeforeAnswer() {
        textResult.setVisibility(View.INVISIBLE);
        buttonEnd.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
        buttonSubmit.setVisibility(View.VISIBLE);
    }

    // Shows elements after an answer is given
    protected void showAfterAnswer() {
        textResult.setVisibility(View.VISIBLE);
        buttonEnd.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        buttonSubmit.setVisibility(View.INVISIBLE);
    }
}
