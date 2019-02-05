package com.emrhmrc.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.emrhmrc.cash10.BuildConfig;
import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.api.OneSignalTask;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.model.VersionModel;
import com.emrhmrc.cash10.util.TextFont;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OneSignal;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private TextView txt_title, txt_subtitle, txt_start;
    private Animation small_to_big, fleft, fhelper, ftop, ftophelper;
    private ImageView img_splash;
    private SharedPref pref;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AdView mAdView;
    private VersionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // initmAuthState();
        initAdd();
        init();
        initAnim();
        txt_title.setTypeface(TextFont.logo(getApplicationContext()));
        txt_subtitle.setTypeface(TextFont.light(getApplicationContext()));
        txt_start.setTypeface(TextFont.medium(getApplicationContext()));

        txt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            txt_start.setEnabled(false);
                checkUpdate();

            }
        });

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


    }

    private void checkUpdate() {
        Database.getVersionRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            model = documentSnapshot.toObject(VersionModel.class);

                        }
                        if (model.getCode() == BuildConfig.VERSION_CODE) {
                            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                            finish();
                            txt_start.setEnabled(true);
                        } else {

                            Tostcu.warning(getApplicationContext(), "Uygulamamızı Güncellemeniz " +
                                    "Gerekiyor !");
                            txt_start.setEnabled(true);

                        }

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

    private void sendPush() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d(TAG, "User:" + userId);
                OneSignalTask oneSignalTask = new OneSignalTask();
                oneSignalTask.execute(userId, "Hey Benimm Yahu");
                if (registrationId != null)
                    Log.d(TAG, "registrationId:" + registrationId);

            }
        });
    }

    private void initmAuthState() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(HomeActivity.this, WheelActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                }

            }
        };
    }


    private void initAnim() {
        txt_title.setTranslationX(400);
        txt_subtitle.setTranslationX(500);
        txt_start.setTranslationX(600);

        txt_title.setAlpha(0);
        txt_subtitle.setAlpha(0);
        txt_start.setAlpha(0);

        txt_title.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        txt_subtitle.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        txt_start.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();
    }

    private void init() {
        pref = new SharedPref(getApplicationContext());
        small_to_big = AnimationUtils.loadAnimation(this, R.anim.small_to_big);
        fleft = AnimationUtils.loadAnimation(this, R.anim.fleft);
        ftop = AnimationUtils.loadAnimation(this, R.anim.ftop);
        fhelper = AnimationUtils.loadAnimation(this, R.anim.fhelper);
        ftophelper = AnimationUtils.loadAnimation(this, R.anim.ftophelper);

        txt_title = findViewById(R.id.txt_title);
        txt_subtitle = findViewById(R.id.txt_subtitle);
        txt_start = findViewById(R.id.txt_start);
        img_splash = findViewById(R.id.img_splash);
        img_splash.setAnimation(small_to_big);
    }
}
