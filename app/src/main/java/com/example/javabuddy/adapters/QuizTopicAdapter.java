package com.example.javabuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.javabuddy.R;
import com.example.javabuddy.activities.QuizActivity;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.ui.AnimationPalette;

import java.util.ArrayList;
import java.util.List;

public class QuizTopicAdapter extends RecyclerView.Adapter<QuizTopicAdapter.QuizTopicViewHolder> {

    private final Context context;
    private List<QuizTopicItem> topicItems = new ArrayList<>();
    private final SparseArray<String> assignedAnimations = new SparseArray<>();
    private SharedPreferences settingsPreferences;

    public static class QuizTopicItem {
        public final Lesson lesson;
        public final int quizCount;

        public QuizTopicItem(Lesson lesson, int quizCount) {
            this.lesson = lesson;
            this.quizCount = quizCount;
        }
    }

    public QuizTopicAdapter(Context context) {
        this.context = context;
        this.settingsPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTopicItems(List<QuizTopicItem> items) {
        this.topicItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_topic, parent, false);
        return new QuizTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizTopicViewHolder holder, int position) {
        QuizTopicItem item = topicItems.get(position);
        Lesson lesson = item.lesson;
        int quizCount = item.quizCount;

        holder.topicTitle.setText(lesson.getTitle());
        
        // Set quiz count text
        if (quizCount > 0) {
            holder.quizCountText.setText(quizCount + " problem" + (quizCount > 1 ? "s" : "") + " available");
            holder.quizCountText.setTextColor(context.getResources().getColor(R.color.success_color));
        } else {
            holder.quizCountText.setText("No problems available");
            holder.quizCountText.setTextColor(context.getResources().getColor(R.color.warning_color));
        }
        
        // Set summary
        if (lesson.getSummary() != null && !lesson.getSummary().trim().isEmpty()) {
            holder.topicSummary.setText(lesson.getSummary());
            holder.topicSummary.setVisibility(View.VISIBLE);
        } else {
            holder.topicSummary.setVisibility(View.GONE);
        }

        // Check animation preference
        boolean animationsEnabled = settingsPreferences.getBoolean("pref_enable_card_animations", true);
        
        if (!animationsEnabled) {
            holder.quizAnimation.cancelAnimation();
            holder.quizAnimation.setVisibility(View.GONE);
        } else {
            holder.quizAnimation.setVisibility(View.VISIBLE);
            
            // Assign random animation if not already assigned
            String animation = assignedAnimations.get(lesson.getId());
            if (animation == null) {
                animation = AnimationPalette.randomQuizAnimation();
                assignedAnimations.put(lesson.getId(), animation);
            }
            
            holder.quizAnimation.setAnimation(animation);
            holder.quizAnimation.setSpeed(1.0f);
            holder.quizAnimation.loop(true);
            holder.quizAnimation.playAnimation();
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (quizCount > 0) {
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("lesson_id", lesson.getId());
                intent.putExtra("lesson_title", lesson.getTitle());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No problems available for this lesson yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicItems.size();
    }

    static class QuizTopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicTitle;
        TextView quizCountText;
        TextView topicSummary;
        LottieAnimationView quizAnimation;

        public QuizTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id.topic_title);
            quizCountText = itemView.findViewById(R.id.quiz_count_text);
            topicSummary = itemView.findViewById(R.id.topic_summary);
            quizAnimation = itemView.findViewById(R.id.quiz_animation);
        }
    }
}
