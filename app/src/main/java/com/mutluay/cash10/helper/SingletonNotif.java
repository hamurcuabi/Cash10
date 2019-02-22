package com.mutluay.cash10.helper;

import com.mutluay.cash10.model.NotifModel;

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
