package com.mutluay.cash10.util;

import java.util.Random;

public class Utils {

    private static final String ALLOWED_CHARACTERS = "0123456789";
    public static final int MAX_WHEEL_COUNT=2500;
    public static final double TL_POINT=100000.0;
    public static final String AppId="ca-app-pub-1202140444527551~2554318838";
    public static final String RewardVideo="ca-app-pub-1202140444527551/6564171443";
    public static final String RewardVideo2="ca-app-pub-1202140444527551~2554318838";
    public static final String RewardVideo3="ca-app-pub-1202140444527551~2554318838";
    public static final String RewardVideo4="ca-app-pub-1202140444527551~2554318838";
    public static final String BannerHome="ca-app-pub-1202140444527551/8943046480";
    public static final String BannerLogin="ca-app-pub-1202140444527551/4237514715";
    public static final String BannerWheel="ca-app-pub-1202140444527551/9461921234";
    public static final String InterWheel="ca-app-pub-1202140444527551/5159590438";
    public static final String BannerSlot="ca-app-pub-1202140444527551/8966263067";
    public static final String InterSlot="ca-app-pub-1202140444527551/6444751945";
    public static final String BannerZar="ca-app-pub-1202140444527551/7183118541";
    public static final String InterZar="ca-app-pub-1202140444527551/6785207882";
    public static final String BannerRulet="ca-app-pub-1202140444527551/4897411146";
    public static final String InterRulet="ca-app-pub-1202140444527551/8070369396";
    public static final String BannerYazi="ca-app-pub-1202140444527551/2234754638";
    public static final String InterYazi="ca-app-pub-1202140444527551/6552563151";
    public static final String BannerMarket="ca-app-pub-1202140444527551/9022813825";
    public static final String BannerOdeme="ca-app-pub-1202140444527551/9150806334";
    public static final String BannerDuyuru="ca-app-pub-1202140444527551/7263009599";

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


}
