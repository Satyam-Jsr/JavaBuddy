package com.example.javabuddy.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.ui.AnimationPalette;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonContentFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lesson_id";
    private TextView contentText;
    private ScrollView scrollView;
    private LottieAnimationView heroAnimation;
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private int lessonId;

    public static LessonContentFragment newInstance(int lessonId) {
        LessonContentFragment fragment = new LessonContentFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_content, container, false);
        
        initializeViews(view);
        loadLessonContent();
        
        return view;
    }

    private void initializeViews(View view) {
        contentText = view.findViewById(R.id.content_text);
        scrollView = view.findViewById(R.id.scroll_view);
        heroAnimation = view.findViewById(R.id.lesson_hero_animation);
        
        database = JavaBuddyDatabase.getDatabase(requireContext());
        executor = Executors.newSingleThreadExecutor();

        if (heroAnimation != null) {
            String animationFile = AnimationPalette.animationForIndex(lessonId);
            if (animationFile != null) {
                heroAnimation.setAnimation(animationFile);
                heroAnimation.setRepeatCount(LottieDrawable.INFINITE);
                heroAnimation.playAnimation();
            }
        }
    }

    private void loadLessonContent() {
        executor.execute(() -> {
            Lesson lesson = database.lessonDao().getLessonById(lessonId);
            if (getActivity() != null && lesson != null) {
                getActivity().runOnUiThread(() -> {
                    displayContent(lesson);
                });
            }
        });
    }

    private void displayContent(Lesson lesson) {
        String content = lesson.getContent();
        
        // Format content for better readability
        content = content.replace("\\n", "\n");
        
        // Display content as HTML for rich formatting
        contentText.setText(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT));
        contentText.setMovementMethod(LinkMovementMethod.getInstance()); // Makes links clickable
        contentText.setTextSize(16f);
        contentText.setLineSpacing(8f, 1.0f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (heroAnimation != null) {
            heroAnimation.cancelAnimation();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}