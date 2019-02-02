package com.emrhmrc.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        is_login = false;
        init();
        setEditexts();
        initAnim();
        initClick();


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
            Database.getUserRef().get()
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
                                }
                                if (is_login) break;
                            }
                            if (!is_login) {
                                //Fail Login
                                img_logo.clearAnimation();
                                isEnabled(true);
                            } else {
                                img_logo.clearAnimation();
                                isEnabled(true);
                                pref.setUserMail(edt_email.getText().toString());
                                pref.setUserPass(edt_password.getText().toString());
                                pref.setUserId(model.getDocId());
                                SingletonUser.getInstance().setUserModel(model);
                                Intent i = new Intent(LoginActivity.this, NewSideHomeMenu.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                                finish();

                            }
                        }
                    });
        }
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
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        txt_forget_password = findViewById(R.id.txt_forget_password);
        pass_inputlayout = findViewById(R.id.pass_inputlayout);
        mail_inputlayout = findViewById(R.id.mail_inputlayout);
        small_to_big2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_login:
                logMeIn();
                break;
            case R.id.btn_signup:
                Intent i = new Intent(LoginActivity.this, SigninGoogleActivity.class);
                startActivity(i);
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
