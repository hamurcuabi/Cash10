package com.emrhmrc.cash10.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SharedPref {
    // Shared preferences file name
    private static final String PREF_NAME = "PrefManager";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String CODE_SEND = "CodeSended";
    private static final String SIGN_STATE = "SignState";
    private static final String TIME_WHEEL = "TimeWheel";
    private static final String WHEEL_PLAYED = "WheelPlayed";
    private static final String USER_ID = "UserID";
    private static final String USER_MAIL = "UserMail";
    private static final String USER_PASS = "UserPass";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public SharedPref(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public String getCodeSend() {
        return pref.getString(CODE_SEND, "");
    }

    public void setCodeSend(String codeSend) {
        editor.putString(CODE_SEND, codeSend);
        editor.commit();
    }

    public int getSignState() {
        return pref.getInt(SIGN_STATE, 0);
    }

    public void setSignState(int codeSend) {
        editor.putInt(SIGN_STATE, codeSend);
        editor.commit();
    }

    public String getTimeWheel() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return pref.getString(TIME_WHEEL, dateFormat.format(date));
    }

    public void setTimeWheel(String date) {
        editor.putString(TIME_WHEEL, date);
        editor.commit();
    }

    public int getWheelPlayed() {
        return pref.getInt(WHEEL_PLAYED, 0);
    }

    public void setWheelPlayed(int i) {
        editor.putInt(WHEEL_PLAYED, i);
        editor.commit();
    }

    public String getUserId() {

        return pref.getString(USER_ID, "UserID");
    }

    public void setUserId(String id) {
        editor.putString(USER_ID, id);
        editor.commit();
    }

    public String getUserMail() {
        return pref.getString(USER_MAIL, "");
    }

    public void setUserMail(String mail) {
        editor.putString(USER_MAIL, mail);
        editor.commit();
    }

    public String getUserPass() {
        return pref.getString(USER_PASS, "");
    }

    public void setUserPass(String pass) {
        editor.putString(USER_PASS, pass);
        editor.commit();
    }
}
