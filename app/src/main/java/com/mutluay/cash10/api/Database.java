package com.mutluay.cash10.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private static FirebaseFirestore db = null;
    private static CollectionReference userNewRef = null;
    private static CollectionReference versionRef = null;
    private static CollectionReference userNotif = null;
    private static CollectionReference onlineUserRef = null;
    private static DocumentReference userNewInfo = null;
    private static DocumentReference onlineUserInfo = null;
    private static DocumentReference versionInfo = null;
    private static DocumentReference userNotifInfo = null;


    public static FirebaseFirestore getDb() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }


    public static CollectionReference getUserNewRef() {

        if (userNewRef == null) {
            userNewRef = getDb().collection("NewUser");
        }
        return userNewRef;

    }
    public static CollectionReference getOnlineUserRef() {

        if (onlineUserRef == null) {
            onlineUserRef = getDb().collection("Online");
        }
        return onlineUserRef;

    }

    public static CollectionReference getVersionRef() {

        if (versionRef == null) {
            versionRef = getDb().collection("Version");
        }
        return versionRef;

    }

    public static CollectionReference getUserNotif() {

        if (userNotif == null) {
            userNotif = getDb().collection("Notif");
        }
        return userNotif;

    }

    public static DocumentReference getUserNotif(String id) {

        if (userNotifInfo == null) {
            userNotifInfo = getDb().document("Notif/" + id);
        }
        return userNotifInfo;
    }
    public static DocumentReference getOnlineUserInfo(String id) {

        if (onlineUserInfo == null) {
            onlineUserInfo = getDb().document("Online/" + id);
        }
        return onlineUserInfo;
    }

    public static DocumentReference getVersionInfo(String id) {

        if (versionInfo == null) {
            versionInfo = getDb().document("Version" + id);
        }
        return versionInfo;
    }

    public static DocumentReference getUserNewInfo(String id) {


        userNewInfo = getDb().document("NewUser/" + id);

        return userNewInfo;
    }


}
