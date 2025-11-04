package com.example.javabuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.activities.LessonDetailActivity;
import com.example.javabuddy.activities.QuizActivity;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.ui.AnimationPalette;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonPracticeFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lesson_id";
    private Button takeQuizButton, nextLessonButton;
    private LottieAnimationView practiceAnimation;
    private int lessonId;
    private JavaBuddyDatabase database;
    private ExecutorService executor;

    public static LessonPracticeFragment newInstance(int lessonId) {
        LessonPracticeFragment fragment = new LessonPracticeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getInt(ARG_LESSON_ID);
        }
        executor = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_practice, container, false);
        
        initializeViews(view);
        setupClickListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        takeQuizButton = view.findViewById(R.id.take_quiz_button);
        nextLessonButton = view.findViewById(R.id.next_lesson_button);
        practiceAnimation = view.findViewById(R.id.practice_animation);
        database = JavaBuddyDatabase.getDatabase(requireContext());

        if (practiceAnimation != null) {
            String animationFile = AnimationPalette.animationForIndex(lessonId + 7);
            if (animationFile != null) {
                practiceAnimation.setAnimation(animationFile);
                practiceAnimation.setRepeatCount(LottieDrawable.INFINITE);
                practiceAnimation.playAnimation();
            }
        }
    }

    private void setupClickListeners() {
        takeQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("lesson_id", lessonId);
            startActivity(intent);
        });

        nextLessonButton.setOnClickListener(v -> {
            navigateToNextLesson();
        });
    }

    private void navigateToNextLesson() {
        if (database == null || executor == null) {
            Toast.makeText(getContext(), "Next lesson unavailable right now.", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            Lesson nextLesson = database.lessonDao().getNextLesson(lessonId);
            if (!isAdded()) {
                return;
            }

            if (nextLesson != null) {
                requireActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(getActivity(), LessonDetailActivity.class);
                    intent.putExtra("lesson_id", nextLesson.getId());
                    intent.putExtra("lesson_title", nextLesson.getTitle());
                    startActivity(intent);
                });
            } else {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "You’ve completed all available lessons!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (practiceAnimation != null) {
            practiceAnimation.cancelAnimation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}