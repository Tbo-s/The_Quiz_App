package com.example.thequizapp;

import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    int answerTotal = 0;
    int answerCorrect = 0;

    Random rand = new Random();
    String correctName;
    int idCorrect;
    int idCurrent;

    TextView textScore;
    TextView textResult;
    Button buttonSubmit;
    Button buttonEnd;
    Button buttonNext;

    Button buttonOpt1;
    Button buttonOpt2;
    Button buttonOpt3;
    List<Button> optButtons;
    RadioGroup radioGroup;
    ImageView image;

    List<Pair<String, Integer>> images;

    public QuizActivity() {
        this.images = new ArrayList<>();
        images.add(new Pair<>("Aegon", R.drawable.aegon));
        images.add(new Pair<>("Hank", R.drawable.hank));
        images.add(new Pair<>("Huan", R.drawable.huan));
        images.add(new Pair<>("Lia", R.drawable.lia));
    }

    protected void generateQuestion() {
        List<Pair<String, Integer>> options = new ArrayList<>();
        int size = images.size();
        int index1 = rand.nextInt(size);
        buttonOpt1.setText(images.get(index1).first);
        options.add(images.get(index1));
        Log.d("QUIZAPP", String.format("image 1: %d %s", images.get(index1).second, images.get(index1).first));
        int index2 = rand.nextInt(size);
        while (index2 == index1) {
            index2 = rand.nextInt(size);
        }
        buttonOpt2.setText(images.get(index2).first);
        options.add(images.get(index2));
        Log.d("QUIZAPP", String.format("image 2: %d %s", images.get(index2).second, images.get(index2).first));
        int index3 = rand.nextInt(size);
        while (index3 == index1 || index3 == index2) {
            index3 = rand.nextInt(size);
        }
        buttonOpt3.setText(images.get(index3).first);
        options.add(images.get(index3));
        Log.d("QUIZAPP", String.format("image 3: %d %s", images.get(index3).second, images.get(index3).first));

        int answer = rand.nextInt(3);
        correctName = options.get(answer).first;
        idCorrect = optButtons.get(answer).getId(); // the correct button
        Log.d("QUIZAPP", String.format("The correct answer: %d %s", idCorrect, correctName));
        image.setImageResource(options.get(answer).second);
        radioGroup.clearCheck();
    }

    protected boolean wasCorrect() {
        Log.d("QUIZAPP", String.format("id correct = %d, id current = %d", idCorrect, idCurrent));
        return idCorrect == idCurrent;
    }

    protected void updateScore() {
        answerTotal += 1;
        if (wasCorrect()) {
            Log.d("QUIZAPP", "correct answer");
            textResult.setText(R.string.correct);
            answerCorrect += 1;
        } else {
            Log.d("QUIZAPP", "incorrect answer");
            textResult.setText(getString(R.string.incorrect, correctName));
        }

        textScore.setText(getString(R.string.quiz_score, answerCorrect, answerTotal, String.format("%.1f %%", answerCorrect * 100.0 / answerTotal)));
    }

    protected void hideBeforeAnswer() {
        textResult.setVisibility(View.INVISIBLE);
        buttonEnd.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
        buttonSubmit.setVisibility(View.VISIBLE);
    }

    protected void showAfterAnswer() {
        textResult.setVisibility(View.VISIBLE);
        buttonEnd.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        buttonSubmit.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("QUIZAPP", "super on quiz create called");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        Log.d("QUIZAPP", "quiz set layout");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textScore = findViewById(R.id.textQuizScore);
        textScore.setText(getString(R.string.quiz_score, answerCorrect, answerTotal, "answer to get percentage"));
        textResult = findViewById(R.id.textQuizResult);
        Log.d("QUIZAPP", "quiz set text score");

        buttonEnd = findViewById(R.id.buttonQuizEnd);
        buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.d("QUIZAPP", "quiz set end button");

        buttonNext = findViewById(R.id.buttonQuizNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQuestion();
                hideBeforeAnswer();
            }
        });
        Log.d("QUIZAPP", "quiz set next button");

        buttonSubmit = findViewById(R.id.buttonQuizSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAfterAnswer();
                updateScore();
            }
        });
        Log.d("QUIZAPP", "quiz set submit button");

        buttonOpt1 = findViewById(R.id.buttonQuizOption1);
        buttonOpt2 = findViewById(R.id.buttonQuizOption2);
        buttonOpt3 = findViewById(R.id.buttonQuizOption3);

        optButtons = new ArrayList<>();
        optButtons.add(buttonOpt1);
        optButtons.add(buttonOpt2);
        optButtons.add(buttonOpt3);

        radioGroup = findViewById(R.id.radioQuizAnsweres);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                idCurrent = checkedId;
                Log.d("QUIZAPP", String.format("id changed to %d", idCurrent));
            }
        });
        Log.d("QUIZAPP", "quiz set radio buttons");


        image = findViewById(R.id.imageView);

        hideBeforeAnswer();
        Log.d("QUIZAPP", "quiz set visibility");

        generateQuestion();
        Log.d("QUIZAPP", "quiz set first question");
    }
}