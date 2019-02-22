package com.mutluay.cash10.util;

import android.content.Context;
import android.graphics.Typeface;

public class TextFont {

    public static Typeface logo(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Fredoka.ttf");
    }

    public static Typeface light(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/MontserratLight.ttf");
    }

    public static Typeface medium(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/MontserratMedium.ttf");
    }

    public static Typeface regular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/MontserratRegular.ttf");
    }
}
