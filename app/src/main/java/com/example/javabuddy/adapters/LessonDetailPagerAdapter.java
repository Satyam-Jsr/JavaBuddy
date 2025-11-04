package com.example.javabuddy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.javabuddy.fragments.LessonCodeFragment;
import com.example.javabuddy.fragments.LessonContentFragment;
import com.example.javabuddy.fragments.LessonPracticeFragment;

public class LessonDetailPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 3;
    private int lessonId;

    public LessonDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, int lessonId) {
        super(fragmentActivity);
        this.lessonId = lessonId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return LessonContentFragment.newInstance(lessonId);
            case 1:
                return LessonCodeFragment.newInstance(lessonId);
            case 2:
                return LessonPracticeFragment.newInstance(lessonId);
            default:
                return LessonContentFragment.newInstance(lessonId);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}