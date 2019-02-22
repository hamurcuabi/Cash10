package com.mutluay.cash10.helper;

import com.mutluay.cash10.model.UserModel;

public class SingletonUser {


    private static SingletonUser single_instance = null;

    private UserModel userModel;

    private SingletonUser() {

    }

    public static SingletonUser getInstance() {
        if (single_instance == null)
            single_instance = new SingletonUser();

        return single_instance;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

}
