package com.example.javabuddy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javabuddy.R;
import com.example.javabuddy.database.entities.PracticeProblem;

import java.util.List;

public class PracticeProblemAdapter extends RecyclerView.Adapter<PracticeProblemAdapter.ProblemViewHolder> {

    private List<PracticeProblem> problems;
    private OnProblemClickListener listener;

    public interface OnProblemClickListener {
        void onProblemClick(PracticeProblem problem);
    }

    public PracticeProblemAdapter(List<PracticeProblem> problems, OnProblemClickListener listener) {
        this.problems = problems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_practice_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        PracticeProblem problem = problems.get(position);
        holder.bind(problem);
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    public class ProblemViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView titleText, descriptionText, difficultyText, categoryText, pointsText;
        private View difficultyIndicator;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.problem_card);
            titleText = itemView.findViewById(R.id.problem_title);
            descriptionText = itemView.findViewById(R.id.problem_description);
            difficultyText = itemView.findViewById(R.id.difficulty_text);
            categoryText = itemView.findViewById(R.id.category_text);
            pointsText = itemView.findViewById(R.id.points_text);
            difficultyIndicator = itemView.findViewById(R.id.difficulty_indicator);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProblemClick(problems.get(position));
                }
            });
        }

        public void bind(PracticeProblem problem) {
            titleText.setText(problem.getTitle());
            
            // Truncate description if too long
            String description = problem.getProblemStatement();
            if (description.length() > 100) {
                description = description.substring(0, 100) + "...";
            }
            descriptionText.setText(description);
            
            difficultyText.setText(problem.getDifficulty());
            categoryText.setText(problem.getCategory());
            pointsText.setText(String.format("%d pts", problem.getPoints()));

            // Set difficulty indicator color
            int color;
            switch (problem.getDifficulty().toLowerCase()) {
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