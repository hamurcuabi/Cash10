package com.emrhmrc.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.emrhmrc.cash10.adapter.NotifAdapter;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.helper.SingletonNotif;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.model.NotifModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotifListActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView rcw;
    private List<NotifModel> model;
    private NotifAdapter adapter;
    private ProgressBar progress;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_list);
        initAdd();
        init();
        if (SingletonUser.getInstance().getUserModel().isAdmin()) {
            getAdminList();
        } else getList();
    }
    private void initAdd() {
        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private void init() {
        rcw = findViewById(R.id.rcv);
        adapter = new NotifAdapter(getApplicationContext(), this);
        rcw.setLayoutManager(new LinearLayoutManager(this));
        rcw.setAdapter(adapter);
        model = new ArrayList<>();
        progress = findViewById(R.id.progress);
    }

    private void getList() {


        Database.getUserNotif().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                model.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    NotifModel notifModel = documentSnapshot.toObject(NotifModel.class);
                    if (SingletonUser.getInstance().getUserModel().getDocId().equals(notifModel.getDocId
                            ())) {
                        model.add(notifModel);
                    }
                }
                adapter.setItems(model);
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void getAdminList() {

        Database.getUserNotif().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                model.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    NotifModel notifModel = documentSnapshot.toObject(NotifModel.class);

                    model.add(notifModel);

                }
                adapter.setItems(model);
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClicked(Object item) {
        SingletonNotif.getInstance().setNotifModel((NotifModel) item);
        Intent i = new Intent(NotifListActivity.this, NotifDetailActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.setVisibility(View.VISIBLE);
        if (SingletonUser.getInstance().getUserModel().isAdmin()) {
            getAdminList();
        } else getList();
    }
}
