package com.emrhmrc.cash10.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private static FirebaseFirestore db = null;
    private static CollectionReference userRef = null;
    private static CollectionReference versionRef = null;
    private static CollectionReference userNotif= null;
    private static DocumentReference userInfo = null;
    private static DocumentReference versionInfo = null;
    private static DocumentReference userNotifInfo = null;


    public static FirebaseFirestore getDb() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static CollectionReference getUserRef() {

        if (userRef == null) {
            userRef = getDb().collection("User");
        }
        return userRef;

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
    public static DocumentReference getVersionInfo(String id) {

        if (versionInfo == null) {
            versionInfo = getDb().document("Version" + id);
        }
        return versionInfo;
    }

    public static DocumentReference getUserInfo(String id) {


            userInfo = getDb().document("User/" + id);

        return userInfo;
    }


}
