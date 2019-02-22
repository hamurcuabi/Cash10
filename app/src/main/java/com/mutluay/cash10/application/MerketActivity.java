package com.mutluay.cash10.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.mutluay.cash10.R;
import com.mutluay.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.mutluay.cash10.adapter.RewardAdapter;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.api.OneSignalTask;
import com.mutluay.cash10.helper.Functions;
import com.mutluay.cash10.helper.SingletonUser;
import com.mutluay.cash10.model.NotifModel;
import com.mutluay.cash10.model.RewardModel;
import com.mutluay.cash10.model.UserModel;
import com.mutluay.cash10.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MerketActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MerketActivity";

    private RecyclerView rcw;
    private List<RewardModel> model;
    private RewardAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView txt_selected, txt_count;
    private Button btn_accept, btn_iptal;
    private int money = 0;
    private DocumentReference pointRef;
    private int have;
    private TextInputEditText edt_message;
    private String adminId;
    private boolean is_login;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merket);
        initAdd();
        init();
        prepareList();
    }
    private void initAdd() {
        MobileAds.initialize(this,
                Utils.AppId);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private void init() {

        rcw = findViewById(R.id.rcv);
        adapter = new RewardAdapter(getApplicationContext(), this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcw.setLayoutManager(layoutManager);
        rcw.setAdapter(adapter);
        model = new ArrayList<>();
        txt_count = findViewById(R.id.txt_count);
        txt_selected = findViewById(R.id.txt_selected);
        btn_accept = findViewById(R.id.btn_accept);
        btn_iptal = findViewById(R.id.btn_iptal);
        btn_accept.setOnClickListener(this);
        btn_iptal.setOnClickListener(this);
        edt_message = findViewById(R.id.edt_message);
        pointRef = Database.getUserNewInfo(SingletonUser.getInstance().getUserModel().getDocId());
    }

    private void prepareList() {

        List<RewardModel> list = new ArrayList<>();

        RewardModel model = new RewardModel();
        model.setHowmany("2.000.000");
        model.setMoney(2000000);
        model.setName("Samsung Telefon");
        model.setImage(R.drawable.smartphone);
        list.add(model);

        RewardModel model2 = new RewardModel();
        model2.setHowmany("5.000.000");
        model2.setMoney(5000000);
        model2.setName("Monster Bilgisayar");
        model2.setImage(R.drawable.laptop);
        list.add(model2);

        RewardModel model3 = new RewardModel();
        model3.setHowmany("500.000");
        model3.setMoney(500000);
        model3.setName("100 TL Hediye Paketi");
        model3.setImage(R.drawable.rewardmoney);
        list.add(model3);

        RewardModel model4 = new RewardModel();
        model4.setHowmany("30.000.000");
        model4.setMoney(30000000);
        model4.setName("Kawasaki Z750");
        model4.setImage(R.drawable.motor);
        list.add(model4);

        RewardModel model5 = new RewardModel();
        model5.setHowmany("600.000");
        model5.setMoney(600000);
        model5.setName("200 TL Hediye Paketi");
        model5.setImage(R.drawable.rewardmoney);
        list.add(model5);

        RewardModel model6 = new RewardModel();
        model6.setHowmany("7.000.000");
        model6.setMoney(7000000);
        model6.setName("Iphone Xs Max");
        model6.setImage(R.drawable.smartphone2);
        list.add(model6);

        RewardModel model7 = new RewardModel();
        model7.setHowmany("40.000.000");
        model7.setMoney(40000000);
        model7.setName("Toyota C-HR Hybrid");
        model7.setImage(R.drawable.car);
        list.add(model7);

        RewardModel model8 = new RewardModel();
        model8.setHowmany("800.000");
        model8.setMoney(800000);
        model8.setName("400 TL Hediye Paketi");
        model8.setImage(R.drawable.rewardmoney);
        list.add(model8);

        RewardModel model9 = new RewardModel();
        model9.setHowmany("50.000.000");
        model9.setMoney(50000000);
        model9.setName("Türkiye Sınırlarında 3+1 Ev");
        model9.setImage(R.drawable.house);
        list.add(model9);

        RewardModel mode20 = new RewardModel();
        mode20.setHowmany("400.000");
        mode20.setMoney(400000);
        mode20.setName("Kozmetik");
        mode20.setImage(R.drawable.cosmetic);
        list.add(mode20);

        RewardModel mode21 = new RewardModel();
        mode21.setHowmany("300.000");
        mode21.setMoney(300000);
        mode21.setName("Kırtasiye");
        mode21.setImage(R.drawable.lib);
        list.add(mode21);

        RewardModel mode22 = new RewardModel();
        mode22.setHowmany("200.000");
        mode22.setMoney(200000);
        mode22.setName("Mobil(30 Tl)");
        mode22.setImage(R.drawable.smartphone);
        list.add(mode22);

        adapter.setItems(list);


    }

    @Override
    public void onItemClicked(Object item) {
        final RewardModel model = (RewardModel) item;
        txt_selected.setText(model.getName());
        money = ((RewardModel) item).getMoney();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_accept:
                accept();
                break;
            case R.id.btn_iptal:
                cancel();
                break;


        }
    }

    private void cancel() {
        super.onBackPressed();
    }

    private void accept() {

        if (money != 0 && !TextUtils.isEmpty(edt_message.getText().toString())) {

            if (have >= money) {
                getAdminId();

            } else Tostcu.info(getApplicationContext(), "Elmasların Eksik!");
        } else {
            Tostcu.warning(getApplicationContext(), "Ürün Seç ve Bilgilerini Gir");
        }

    }

    private void sendPay() {

        final UserModel userModel = SingletonUser.getInstance().getUserModel();
        final NotifModel notifModel = new NotifModel();
        notifModel.setDocId(userModel.getDocId());
        notifModel.setEmail(userModel.getEmail());
        notifModel.setIsdone(false);
        notifModel.setMessage(edt_message.getText().toString());
        notifModel.setName(userModel.getName());
        notifModel.setPhone(userModel.getPhone());
        notifModel.setMethode(txt_selected.getText().toString());
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
                        oneSignalTask.execute(adminId, "Elmas Marketi Ödeme Talebi!");
                        executeTransaction(money);
                        Tostcu.succes(getApplicationContext(), "Talebiniz İletildi!");
                        finish();

                    }
                });


    }

    private void getAdminId() {

        Database.getUserNewRef().get()
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

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(SingletonUser.getInstance()
                        .getUserModel().getDocId());
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("star") - i;
                transaction.update(point, "star", newPoint);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        pointRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>
                () {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    Long star = documentSnapshot.getLong("star");
                    txt_count.setText("" + star);
                    have = star.intValue();
                }
            }
        });
    }
}
