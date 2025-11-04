package com.example.javabuddy.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.example.javabuddy.R;
import com.example.javabuddy.ai.GroqApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class AIHelpActivity extends AppCompatActivity {

    private TextInputEditText questionInput;
    private MaterialButton askButton;
    private ProgressBar loadingIndicator;
    private MaterialCardView responseCard;
    private TextView responseText;

    private GroqApiService groqApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_help);

        initializeViews();
        setupToolbar();
        setupListeners();

        groqApiService = new GroqApiService();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionInput = findViewById(R.id.question_input);
        askButton = findViewById(R.id.ask_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        responseCard = findViewById(R.id.response_card);
        responseText = findViewById(R.id.response_text);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.ai_help_title);
        }
    }

    private void setupListeners() {
        askButton.setOnClickListener(v -> submitQuestion());
    }

    private void submitQuestion() {
        String question = questionInput != null ? questionInput.getText().toString().trim() : "";
        if (TextUtils.isEmpty(question)) {
            Toast.makeText(this, R.string.ai_help_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);
        groqApiService.askGeneralQuestion(question)
            .thenAccept(response -> runOnUiThread(() -> showResponse(response)))
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    responseCard.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.ai_help_error, Toast.LENGTH_SHORT).show();
                });
                return null;
            });
    }

    private void showResponse(String response) {
        setLoadingState(false);
        if (responseCard.getVisibility() != View.VISIBLE) {
            responseCard.setVisibility(View.VISIBLE);
        }
        responseText.setText(formatResponseText(response));
    }

    private void setLoadingState(boolean loading) {
        loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        askButton.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private CharSequence formatResponseText(String rawResponse) {
        if (TextUtils.isEmpty(rawResponse)) {
            return getString(R.string.ai_help_error);
        }

        String[] lines = rawResponse.trim().split("\\r?\\n");
        StringBuilder html = new StringBuilder();
        boolean inList = false;

        for (String originalLine : lines) {
            String line = originalLine.trim();

            if (line.isEmpty()) {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<br/>");
                continue;
            }

            if (line.startsWith("- ") || line.startsWith("* ") || line.startsWith("• ")) {
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                String item = line.substring(1).trim();
                html.append("<li>").append(TextUtils.htmlEncode(item)).append("</li>");
            } else {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<p>")
                    .append(TextUtils.htmlEncode(originalLine.trim()))
                    .append("</p>");
            }
        }

        if (inList) {
            html.append("</ul>");
        }

        return HtmlCompat.fromHtml(html.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY);
    }
}
