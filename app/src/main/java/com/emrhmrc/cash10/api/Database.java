package com.emrhmrc.cash10.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private static FirebaseFirestore db = null;
    private static CollectionReference userRef = null;
    private static DocumentReference userInfo = null;


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



    public static DocumentReference getUserInfo(String id) {

        if (userInfo == null) {
            userInfo = getDb().document("User/" + id);
        }
        return userInfo;
    }


}
