package com.emrhmrc.cash10.helper;

import com.emrhmrc.cash10.model.NotifModel;
import com.emrhmrc.cash10.model.UserModel;

public class SingletonNotif {


    private static SingletonNotif single_instance = null;

    private NotifModel notifModel;

    private SingletonNotif() {

    }

    public static SingletonNotif getInstance() {
        if (single_instance == null)
            single_instance = new SingletonNotif();

        return single_instance;
    }

    public NotifModel getNotifModel() {
        return notifModel;
    }

    public void setNotifModel(NotifModel notifModel) {
        this.notifModel = notifModel;
    }

}
