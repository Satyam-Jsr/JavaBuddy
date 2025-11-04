package com.example.javabuddy.ui;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Central place to keep track of Lottie animation asset names so we can
 * reuse the same pool across activities and fragments without duplicating
 * string arrays everywhere.
 */
public final class AnimationPalette {

    private static final String[] QUIZ_ANIMATIONS = {
        "lott1.lottie",
        "lott2.lottie",
        "lott3.lottie",
        "lott4.lottie",
        "lott5.lottie",
        "lott6.lottie",
        "lott7.lottie",
        "lott8.lottie",
        "lott9.lottie",
        "lott10.lottie",
        "lott11.lottie",
        "lott12.lottie",
        "lott13.lottie",
        "lott14.lottie",
        "lott15.lottie",
        "lott16.lottie",
        "lott17.lottie",
        "lott18.lottie",
        "lott19.lottie",
        "lott20.lottie"
    };

    private AnimationPalette() {
        // Utility class
    }

    public static String randomQuizAnimation() {
        if (QUIZ_ANIMATIONS.length == 0) {
            return null;
        }
        return QUIZ_ANIMATIONS[ThreadLocalRandom.current().nextInt(QUIZ_ANIMATIONS.length)];
    }

    public static String animationForIndex(int index) {
        if (QUIZ_ANIMATIONS.length == 0) {
            return null;
        }
        int safeIndex = Math.abs(index) % QUIZ_ANIMATIONS.length;
        return QUIZ_ANIMATIONS[safeIndex];
    }

    public static String[] allAnimations() {
        return QUIZ_ANIMATIONS.clone();
    }
}
