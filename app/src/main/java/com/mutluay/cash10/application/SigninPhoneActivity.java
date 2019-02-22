package com.mutluay.cash10.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mutluay.cash10.R;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.util.MaskedEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SigninPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SigninPhoneActivity";
    private ImageView img_phone, img_security;
    private MaskedEditText edt_phone_input, edt_code_input;
    private TextView txt_info;
    private Button btn_phone, btn_code;
    private Animation small_to_big, small_to_big2;
    private String codeSent;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken token;
    private SharedPref pref;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            Log.d(TAG, "onVerificationCompleted: ");
            txt_info.setText(getString(R.string.sended));
            btn_code.setEnabled(true);
            edt_code_input.setEnabled(true);


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            txt_info.setText(getString(R.string.error));
            btn_phone.setEnabled(true);
            edt_phone_input.setEnabled(true);
            btn_code.setEnabled(false);
            edt_code_input.setEnabled(false);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
            token = forceResendingToken;
            pref.setCodeSend(codeSent);

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_phone);
        init();
        initClick();
        initAnim();
    }

    private void initAnim() {
        edt_phone_input.setTranslationX(600);
        btn_phone.setTranslationX(-700);
        txt_info.setTranslationX(800);
        edt_code_input.setTranslationX(900);
        btn_code.setTranslationX(-1000);

        edt_code_input.setAlpha(0);
        edt_phone_input.setAlpha(0);
        txt_info.setAlpha(0);

        edt_phone_input.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        btn_phone.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        txt_info.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        edt_code_input.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500)
                .start();
        btn_code.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(600).start();

        img_phone.setAnimation(small_to_big);
        img_security.setAnimation(small_to_big2);
    }

    private void initClick() {
        btn_code.setOnClickListener(this);
        btn_phone.setOnClickListener(this);
    }

    private void init() {
        pref = new SharedPref(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        img_phone = findViewById(R.id.img_diceroll);
        img_security = findViewById(R.id.img_security);
        edt_phone_input = findViewById(R.id.edt_phone_input);
        edt_code_input = findViewById(R.id.edt_code_input);
        txt_info = findViewById(R.id.txt_info);
        btn_phone = findViewById(R.id.btn_phone);
        btn_code = findViewById(R.id.btn_code);
        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big);
        small_to_big2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);
        btn_code.setEnabled(false);
        edt_code_input.setEnabled(false);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here you can open new activity
                            Database.getUserNewInfo(pref.getUserId()).update("phone", mAuth
                                    .getCurrentUser().getPhoneNumber());
                            pref.setSignState(2);
                            onBackPressed();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                                txt_info.setText(getString(R.string.error_code));
                                btn_code.setEnabled(true);
                                edt_code_input.setEnabled(true);
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {

        String phone = "+90" + edt_phone_input.getRawText();
        if (phone.length() == 13) {
            btn_phone.setEnabled(false);
            edt_phone_input.setEnabled(false);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            txt_info.setText(getString(R.string.sending));
        } else txt_info.setText(getString(R.string.error));

    }

    private void verifySignInCode() {
        String code = edt_code_input.getRawText();

        if (code != null && code.length() == 6) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
            btn_code.setEnabled(false);
            edt_code_input.setEnabled(false);
        } else txt_info.setText(getString(R.string.error_code));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                verifySignInCode();
                break;
            case R.id.btn_phone:
                sendVerificationCode();
                break;

        }
    }
}
