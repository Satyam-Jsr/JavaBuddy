package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.ui.AnimationPalette;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class TimedTestActivity extends AppCompatActivity {

    public static final String EXTRA_TIMED_MODE = "timed_mode";
    public static final String EXTRA_TIME_LIMIT_SECONDS = "time_limit_seconds";
    public static final String EXTRA_TIMED_QUESTION_COUNT = "timed_question_count";

    private SeekBar questionsSeekBar;
    private TextView questionsLabel;
    private ChipGroup timeChipGroup;
    private Button startButton;
    private TextView statusText;
    private LottieAnimationView headerAnimation;

    private int questionCount = 10;
    private int timeLimitSeconds = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_test);

        initializeViews();
        setupToolbar();
        setupAnimation();
        setupSeekBar();
        setupChipGroup();
        setupButton();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionsSeekBar = findViewById(R.id.questions_seekbar);
    questionsLabel = findViewById(R.id.questions_label);
        timeChipGroup = findViewById(R.id.time_chip_group);
        startButton = findViewById(R.id.start_timed_test_button);
        statusText = findViewById(R.id.status_text);
        headerAnimation = findViewById(R.id.timed_test_animation);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Timed Test");
        }
    }

    private void setupAnimation() {
        if (headerAnimation != null) {
            String animationFile = AnimationPalette.animationForIndex(4);
            if (animationFile != null) {
                headerAnimation.setAnimation(animationFile);
            }
            headerAnimation.setRepeatCount(LottieDrawable.INFINITE);
            headerAnimation.playAnimation();
        }
    }

    private void setupSeekBar() {
        questionsSeekBar.setMax(25);
        questionsSeekBar.setProgress(questionCount);
        updateQuestionLabel();

        questionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                questionCount = Math.max(5, progress);
                updateQuestionLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setupChipGroup() {
        Chip defaultChip = findViewById(R.id.chip_time_120);
        if (defaultChip != null) {
            defaultChip.setChecked(true);
        }

        timeChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int chipId = checkedIds.get(0);
            if (chipId == R.id.chip_time_60) {
                timeLimitSeconds = 60;
            } else if (chipId == R.id.chip_time_120) {
                timeLimitSeconds = 120;
            } else if (chipId == R.id.chip_time_180) {
                timeLimitSeconds = 180;
            } else if (chipId == R.id.chip_time_300) {
                timeLimitSeconds = 300;
            }
            statusText.setText(String.format("%d questions • %d second limit", questionCount, timeLimitSeconds));
        });
    }

    private void setupButton() {
        startButton.setOnClickListener(v -> startTimedQuiz());
    }

    private void startTimedQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(EXTRA_TIMED_MODE, true);
        intent.putExtra(EXTRA_TIME_LIMIT_SECONDS, timeLimitSeconds);
        intent.putExtra(EXTRA_TIMED_QUESTION_COUNT, questionCount);
        intent.putExtra("lesson_id", -1);
        startActivity(intent);
        finish();
    }

    private void updateQuestionLabel() {
        questionsLabel.setText(String.format("Number of Questions: %d", questionCount));
        statusText.setText(String.format("%d questions • %d second limit", questionCount, timeLimitSeconds));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
