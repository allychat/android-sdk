package com.sergeymild.allychatdemo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by sergeyMild on 08/10/15.
 */
public class DrawableUtils {

    public static Drawable getTintedDrawable(@NonNull Context context, @DrawableRes int drawableResId, @ColorRes int colorResId) {
        Drawable normalDrawable = ContextCompat.getDrawable(context, drawableResId);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable.mutate(), ContextCompat.getColor(context, colorResId));
        return wrapDrawable;
    }
}
