package com.example.javabuddy.adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.ui.AnimationPalette;
import java.util.List;
import android.util.SparseArray;
import androidx.preference.PreferenceManager;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClickListener listener;
    private final SparseArray<String> assignedAnimations = new SparseArray<>();

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
    private TextView titleText, summaryText, difficultyText, conceptGroupText;
    private View difficultyIndicator;
    private LottieAnimationView lessonAnimation;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.lesson_card);
            titleText = itemView.findViewById(R.id.lesson_title);
            summaryText = itemView.findViewById(R.id.lesson_summary);
            difficultyText = itemView.findViewById(R.id.difficulty_text);
            conceptGroupText = itemView.findViewById(R.id.concept_group_text);
            difficultyIndicator = itemView.findViewById(R.id.difficulty_indicator);
            lessonAnimation = itemView.findViewById(R.id.lesson_animation);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onLessonClick(lessons.get(position));
                }
            });
        }

        public void bind(Lesson lesson) {
            titleText.setText(lesson.getTitle());
            summaryText.setText(lesson.getSummary());
            difficultyText.setText(lesson.getDifficulty());
            conceptGroupText.setText(lesson.getConceptGroup());

            if (lessonAnimation != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                boolean animationsEnabled = prefs.getBoolean("pref_enable_card_animations", true);

                if (!animationsEnabled) {
                    lessonAnimation.cancelAnimation();
                    lessonAnimation.setVisibility(View.GONE);
                } else {
                    String animationFile = assignedAnimations.get(lesson.getId());
                    if (animationFile == null) {
                        animationFile = AnimationPalette.randomQuizAnimation();
                        assignedAnimations.put(lesson.getId(), animationFile);
                    }
                    lessonAnimation.setVisibility(View.VISIBLE);
                    lessonAnimation.setRepeatCount(LottieDrawable.INFINITE);
                    Object tag = lessonAnimation.getTag();
                    if (!(tag instanceof String) || !animationFile.equals(tag)) {
                        lessonAnimation.setAnimation(animationFile);
                        lessonAnimation.setTag(animationFile);
                    }
                    if (!lessonAnimation.isAnimating()) {
                        lessonAnimation.playAnimation();
                    }
                }
            }

            // Set difficulty indicator color
            int color;
            switch (lesson.getDifficulty().toLowerCase()) {
                case "beginner":
                    color = itemView.getContext().getResources().getColor(android.R.color.holo_green_light);
                    break;
                case "intermediate":
                    color = itemView.getContext().getResources().getColor(android.R.color.holo_orange_light);
                    break;
                case "advanced":
                    color = itemView.getContext().getResources().getColor(android.R.color.holo_red_light);
                    break;
                default:
                    color = itemView.getContext().getResources().getColor(android.R.color.darker_gray);
            }
            difficultyIndicator.setBackgroundColor(color);
        }
    }
}