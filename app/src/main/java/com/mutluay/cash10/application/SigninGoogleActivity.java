package com.mutluay.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mutluay.cash10.R;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.api.GoogleSignClient;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.helper.SingletonUser;
import com.mutluay.cash10.model.UserModel;
import com.mutluay.cash10.util.Utils;
import com.onesignal.OneSignal;

public class SigninGoogleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SigninGoogleActivity";
    private static final int RC_SIGN_IN = 100;
    private SignInButton btn_sign_in;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageView img_cash10;
    private ConstraintLayout linear_item_one, linear_item_two, linear_item_three;
    private Animation small_to_big2;
    private TextView txt_title;
    private FirebaseUser user;
    private SharedPref pref;
    private boolean is_exist;
    private ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        is_exist = false;
        init();
        initClick();
        initAnim();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignClient.getClient(getApplicationContext());


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initClick() {
        btn_sign_in.setOnClickListener(this);
    }

    private void initAnim() {
        btn_sign_in.setTranslationY(400);
        linear_item_one.setTranslationX(800);
        linear_item_two.setTranslationX(900);
        linear_item_three.setTranslationX(1000);
        btn_sign_in.setAlpha(0);
        txt_title.setAlpha(0);
        linear_item_one.setAlpha(0);
        linear_item_two.setAlpha(0);
        linear_item_three.setAlpha(0);
        btn_sign_in.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        txt_title.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        linear_item_one.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        linear_item_two.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        linear_item_three.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(600).start();
        img_cash10.startAnimation(small_to_big2);
    }

    private void init() {
        prog = findViewById(R.id.prog);
        pref = new SharedPref(getApplicationContext());
        img_cash10 = findViewById(R.id.img_cash10);
        linear_item_one = findViewById(R.id.linear_item_one);
        linear_item_two = findViewById(R.id.linear_item_two);
        linear_item_three = findViewById(R.id.linear_item_three);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        txt_title = findViewById(R.id.txt_title);
        small_to_big2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;

        }
    }

    private void signIn() {

        btn_sign_in.setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d(TAG, "onActivityResult: ");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Tostcu.error(SigninGoogleActivity.this, "Hata " + e.getMessage());
                Log.w(TAG, "Google sign in failed", e);
                btn_sign_in.setEnabled(true);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        prog.setVisibility(View.VISIBLE);
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Tostcu.error(SigninGoogleActivity.this, "Hata " + e.getMessage());
            }
        }).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.d(TAG, "signInWithCredential:success");
                    user = mAuth.getCurrentUser();

                    isExist(user.getEmail());


                } else {
                    btn_sign_in.setEnabled(true);
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    prog.setVisibility(View.VISIBLE);
                    Tostcu.error(SigninGoogleActivity.this, "Hata ile karşılaşıldı, Tekrar" +
                            " Deneyiniz");
                    Tostcu.error(SigninGoogleActivity.this, task.getException().getMessage());
                }

            }
        });
    }

    private void isExist(final String email) {

        Database.getUserNewRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel.getEmail().equals(email)) {
                                //User exist
                                is_exist = true;
                                pref.setUserPass(userModel.getPassword());
                                pref.setUserMail(userModel.getEmail());
                                SingletonUser.getInstance().setUserModel(userModel);

                            }
                            if (is_exist) {
                                break;
                            }
                        }
                        if (is_exist) {
                            //User  exist
                            Log.d(TAG, "This user exist");
//                            Intent i = new Intent(SigninGoogleActivity.this, LoginActivity.class);
//                            startActivity(i);
//                            overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                            SigninGoogleActivity.super.onBackPressed();

                        } else {

                            Log.d(TAG, "Not Exist");

                            final UserModel userModel = new UserModel();
                            userModel.setEmail(user.getEmail());
                            userModel.setName(user.getDisplayName());
                            userModel.setPhotoUrl(user.getPhotoUrl().toString());
                            userModel.setPhone("");
                            userModel.setPoint(1000);
                            userModel.setStar(0);
                            userModel.setPassword(Utils.getRandomString(5));
                            userModel.setAdmin(false);
                            userModel.setHeart(10);
                            userModel.setIslogin(false);
                            userModel.setUsed_ref(false);
                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    Log.d(TAG, "User:" + userId);
                                    userModel.setPlayerId(userId);
                                    userModel.setRef_code(userId.substring(userId.length() - 5));
                                    if (registrationId != null)
                                        Log.d(TAG, "registrationId:" + registrationId);

                                }
                            });

                            Database.getUserNewRef().add(userModel).addOnSuccessListener(
                                    new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            String docId = documentReference.getId();
                                            userModel.setDocId(docId);
                                            documentReference.update("docId", docId);

                                            SingletonUser.getInstance().setUserModel(userModel);
                                            pref.setUserPass(userModel.getPassword());
                                            pref.setUserMail(userModel.getEmail());
                                            pref.setUserId(docId);

                                            prog.setVisibility(View.GONE);
                                            SigninGoogleActivity.super.onBackPressed();
                                        }
                                    });
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
