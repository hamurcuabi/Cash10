package com.mutluay.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.mutluay.cash10.BuildConfig;
import com.mutluay.cash10.R;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.helper.SingletonUser;
import com.mutluay.cash10.model.UserModel;
import com.mutluay.cash10.model.VersionModel;
import com.mutluay.cash10.util.Utils;
import com.onesignal.OneSignal;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private ImageView img_logo;
    private Button btn_login, btn_signup;
    private TextView txt_forget_password;
    private TextInputEditText edt_email, edt_password;
    private TextInputLayout mail_inputlayout, pass_inputlayout;
    private Animation small_to_big2, rotate;
    private boolean is_login;
    private UserModel model;
    private SharedPref pref;
    private AdView mAdView;
    private boolean otheraccount = false;
    private String playerId;
    private String ref_code;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initAdd();
        is_login = false;
        init();
        setEditexts();
        initAnim();
        initClick();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d(TAG, "User:" + userId);
                playerId = userId;
                ref_code = userId.substring(userId.length() - 5);
                if (registrationId != null)
                    Log.d(TAG, "registrationId:" + registrationId);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void setEditexts() {
        edt_email.setText(pref.getUserMail());
        edt_password.setText(pref.getUserPass());
    }

    private void isEnabled(boolean b) {

        btn_login.setEnabled(b);
        btn_signup.setEnabled(b);
        edt_password.setEnabled(b);
        edt_email.setEnabled(b);
        txt_forget_password.setEnabled(b);


    }

    private void initClick() {
        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        txt_forget_password.setOnClickListener(this);
    }

    private void checkUpdate() {
        Database.getVersionRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VersionModel model = documentSnapshot.toObject(VersionModel.class);
                            if (model.getCode() == BuildConfig.VERSION_CODE) {
                                logMeIn();
                            } else {
                                Tostcu.warning(getApplicationContext(), "Uygulamamızı Güncellemeniz " +
                                        "Gerekiyor !");

                            }
                        }


                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Tostcu.error(getApplicationContext(), "Şifremi Unuttum alanına" +
                        " tıklayıp Google ile Giriş Yapınız.");
            }
        });


    }

    private void logMeIn() {
        final String email = edt_email.getText().toString();
        final String pass = edt_password.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mail_inputlayout.setError(getString(R.string.mail_error));

        } else if (TextUtils.isEmpty(pass)) {
            pass_inputlayout.setError(getString(R.string.pass_error));
        } else {
            isEnabled(false);
            img_logo.startAnimation(rotate);
            Database.getUserNewRef().get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                if (userModel.getEmail().equals(email) && userModel.getPassword()
                                        .equals(pass)) {
                                    //Succes Login
                                    is_login = true;
                                    model = userModel;
                                    SingletonUser.getInstance().setUserModel(userModel);
                                    Log.d(TAG, "onSuccess: "+userModel.getDocId());
                                 /*   if (!userModel.isIslogin()) {
                                        is_login = true;
                                        model = userModel;
                                        SingletonUser.getInstance().setUserModel(userModel);
                                    } else {
                                        is_login = false;
                                        otheraccount = true;
                                    }*/
                                }
                                if (is_login) break;
                            }
                            if (!is_login) {
                                //Fail Login
                                Tostcu.error(getApplicationContext(), "Şifremi Unuttum alanına" +
                                        " tıklayıp Google ile Giriş Yapınız.");
                                img_logo.clearAnimation();
                                isEnabled(true);

                            } else {

                                SingletonUser.getInstance().setUserModel(model);
                                Database.getUserNewInfo(model.getDocId()).update("islogin",
                                        true, "ref_code", ref_code, "playerId", playerId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        img_logo.clearAnimation();
                                        isEnabled(true);
                                        pref.setUserMail(edt_email.getText().toString());
                                        pref.setUserPass(edt_password.getText().toString());
                                        pref.setUserId(model.getDocId());
                                        Tostcu.succes(getApplicationContext(), getResources().getString(R
                                                .string.succes));
                                        Log.d(TAG, "onSuccess: " + model.getDocId());
                                        Intent i = new Intent(LoginActivity.this, NewSideHomeMenu.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                                        finish();
                                    }
                                });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Fail Login
                    img_logo.clearAnimation();
                    isEnabled(true);
                    Tostcu.error(getApplicationContext(), "Şifremi Unuttum alanına" +
                            " tıklayıp Google ile Giriş Yapınız.");
                }
            });
        }
    }

    private void logMeInName() {

        isEnabled(false);
        img_logo.startAnimation(rotate);
        Database.getUserNewRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            executeTransactionHeart(30, userModel.getDocId());

                        }
                    }
                });
    }

    private void executeTransactionHeart(final int i, final String user) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(user);
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("heart") + i;
                transaction.update(point, "heart", newPoint);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //ok
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ." + e.getMessage());
            }
        });
    }

    private void initAnim() {

        img_logo.startAnimation(small_to_big2);

        mail_inputlayout.setTranslationX(500);
        pass_inputlayout.setTranslationX(600);
        btn_login.setTranslationX(800);
        btn_signup.setTranslationX(900);
        txt_forget_password.setTranslationX(1000);

        mail_inputlayout.setAlpha(0);
        pass_inputlayout.setAlpha(0);
        btn_login.setAlpha(0);
        btn_signup.setAlpha(0);
        txt_forget_password.setAlpha(0);

        mail_inputlayout.animate().alpha(1).translationX(0).setDuration(500).setStartDelay(200)
                .start();
        pass_inputlayout.animate().alpha(1).translationX(0).setDuration(500).setStartDelay(300)
                .start();
        btn_login.animate().alpha(1).translationX(0).setDuration(500).setStartDelay(400).start();
        btn_signup.animate().alpha(1).translationX(0).setDuration(500).setStartDelay(500).start();
        txt_forget_password.animate().alpha(1).translationX(0).setDuration(500).setStartDelay
                (600).start();


    }

    private void init() {

        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        pref = new SharedPref(getApplicationContext());
        img_logo = findViewById(R.id.img_logo);
        btn_login = findViewById(R.id.btn_accept);
        btn_signup = findViewById(R.id.btn_iptal);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        txt_forget_password = findViewById(R.id.txt_forget_password);
        pass_inputlayout = findViewById(R.id.pass_inputlayout);
        mail_inputlayout = findViewById(R.id.mail_inputlayout);
        small_to_big2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);
    }

    private void initAdd() {
        MobileAds.initialize(this,
                Utils.AppId);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Utils.InterWheel);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_accept:
                checkUpdate();
                //logMeInName();
                break;
            case R.id.btn_iptal:
                Intent i = new Intent(LoginActivity.this, SigninGoogleActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                break;
            case R.id.txt_forget_password:
                Intent i2 = new Intent(LoginActivity.this, SigninGoogleActivity.class);
                startActivity(i2);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEditexts();
    }


}
