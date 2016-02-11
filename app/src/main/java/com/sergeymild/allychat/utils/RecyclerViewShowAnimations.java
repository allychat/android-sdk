package com.sergeymild.allychat.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sergeyMild on 26/11/15.
 */
public class RecyclerViewShowAnimations {
    private final String HORIZONTAL = "horizontal";
    private final String VERTICAL = "vertical";
    private int lastAnimatedPosition = -1;
    private static final int ANIMATION_DELAY_INTERVAL = 50;
    private static final float TRANSLATION_OFFSET_Y = 500f;
    private static final float TRANSLATION_OFFSET_X = 500f;
    private static final int ANIMATION_DURATION = 400;
    private long nextAnimationStartTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isInversed;

    public RecyclerViewShowAnimations(boolean isInversed) {
        this.isInversed = isInversed;
        if (isInversed) {
            lastAnimatedPosition = Integer.MAX_VALUE;
        }
    }

    public void verticalAnimation(final RecyclerView.ViewHolder targetViewHolder, final int position) {
        //baseRecyclerAnimation(targetViewHolder, position, VERTICAL);
        baseRecyclerAnimation(targetViewHolder, position, VERTICAL);
    }

    public void horizontalAnimation(final RecyclerView.ViewHolder targetViewHolder, final int position) {
        baseRecyclerAnimation(targetViewHolder, position, HORIZONTAL);
    }

    private void baseRecyclerAnimation(final RecyclerView.ViewHolder targetViewHolder, final int position, final String animationType) {
        final float maxAlpha = 1f;
        final View targetView = targetViewHolder.itemView;

        // Don't actually run the animation right a way. This gives a nice effect
        // when adding a large batch of items.
        boolean isFromBottom = isInversed ? position < lastAnimatedPosition : position > lastAnimatedPosition;
        if (isFromBottom) {
            int delay = 0;
            long currTime = System.currentTimeMillis();
            if (currTime < nextAnimationStartTime + ANIMATION_DELAY_INTERVAL) {
                delay = (int) ((nextAnimationStartTime + ANIMATION_DELAY_INTERVAL) - currTime);
            }
            nextAnimationStartTime = currTime + delay;

            targetView.setAlpha(0);
            if (animationType.equals(HORIZONTAL)) {
                targetView.setTranslationX(TRANSLATION_OFFSET_X);
            } else if (animationType.equals(VERTICAL)) {
                targetView.setTranslationY(!isInversed ? TRANSLATION_OFFSET_Y : -TRANSLATION_OFFSET_Y);
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (animationType.equals(HORIZONTAL)) {
                        targetView.animate().alpha(maxAlpha).translationX(0).setDuration(ANIMATION_DURATION);
                    } else if (animationType.equals(VERTICAL)) {
                        targetView.animate().alpha(maxAlpha).translationY(0).setDuration(ANIMATION_DURATION);
                    }
                    targetView.animate().setInterpolator(new LinearOutSlowInInterpolator());
                    targetView.animate().start();
                }
            }, delay);
            lastAnimatedPosition = position;
        }
    }
}
