package com.emrhmrc.cash10.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.emrehmrc.tostcu.Tostcu;
import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.api.OneSignalTask;
import com.emrhmrc.cash10.helper.Functions;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.model.NotifModel;
import com.emrhmrc.cash10.model.UserModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends AppCompatActivity {
    private static final String TAG = "PayActivity";

    private Button btn_send;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private SharedPref pref;
    private int index = 0;
    private TextInputEditText edt_msj;
    private String methode;
    private boolean is_login;
    private String adminId;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initAdd();
        init();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_send.setEnabled(false);
                getAdminId();
            }
        });
    }

    private void initAdd() {
        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void getAdminId() {

        Database.getUserRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel.isAdmin()) {
                                //Succes
                                is_login = true;
                                adminId = userModel.getPlayerId();
                            }
                            if (is_login) break;
                        }
                        if (!is_login) {
                            //Fail

                        } else {
                            sendPay();

                        }
                    }
                });

    }

    private void sendPay() {

        final UserModel userModel = SingletonUser.getInstance().getUserModel();
        final NotifModel notifModel = new NotifModel();
        notifModel.setDocId(userModel.getDocId());
        notifModel.setEmail(userModel.getEmail());
        notifModel.setIsdone(false);
        notifModel.setMessage(edt_msj.getText().toString());
        notifModel.setName(userModel.getName());
        notifModel.setPhone(userModel.getPhone());
        notifModel.setMethode(methode);
        notifModel.setAdmin_message("");
        notifModel.setDate(Functions.DateToText());
        notifModel.setPlayId(SingletonUser.getInstance().getUserModel().getPlayerId());

        Database.getUserNotif().add(notifModel).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String docId = documentReference.getId();
                        notifModel.setNotifDocId(docId);
                        documentReference.update("notifDocId", docId);
                        OneSignalTask oneSignalTask = new OneSignalTask();
                        oneSignalTask.execute(adminId, "Ödeme Talebi!");
                        executeTransaction(2000000);
                        Tostcu.succes(getApplicationContext(), "Talebiniz İletildi!");

                    }
                });


    }

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserInfo(pref.getUserId());
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("point") - i;
                transaction.update(point, "point", newPoint);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ." + e.getMessage());

            }
        });
        finish();
    }

    private void init() {
        pref = new SharedPref(getApplicationContext());
        btn_send = findViewById(R.id.btn_send);
        list = new ArrayList<>();
        list.add("TL");
        list.add("Dolar");
        list.add("Euro");
        list.add("Bitcoin");
        list.add("Etherum");
        list.add("Dash");
        list.add("Monero");
        list.add("Zcash");
        list.add("Bitcoin Cash");
        list.add("Litecoin");
        list.add("Stellar");
        list.add("Tron");
        list.add("Dogecoin");
        list.add("Ripple");
        list.add("Neo");
        list.add("Waves");
        list.add("Decred");
        list.add("Verge");
        list.add("Qtum");
        list.add("Peercoin");
        list.add("Digibyte");

        spinner = findViewById(R.id.spn);
        adapter = new ArrayAdapter<>(this,
                R.layout.spn_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                index = i;
                methode = list.get(i);
                Log.d(TAG, "onItemSelected: " + i + methode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edt_msj = findViewById(R.id.edt_msj);
    }
}
