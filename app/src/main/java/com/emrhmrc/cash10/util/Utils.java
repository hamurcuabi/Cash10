package com.emrhmrc.cash10.util;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.WheelGame.model.LuckyItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private static final String ALLOWED_CHARACTERS = "0123456789";
    public static final int MAX_WHEEL_COUNT=2500;
    public static final double TL_POINT=100000.0;

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


}
