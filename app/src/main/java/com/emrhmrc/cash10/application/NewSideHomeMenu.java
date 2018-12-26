package com.emrhmrc.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.fragment.BankInfoFrag;
import com.emrhmrc.cash10.helper.CircleTransform;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.util.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class NewSideHomeMenu extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewSideHomeMenu";
    private ImageView img_home_avatar, img_wheel, img_phone, img_slot, img_video, img_referans,
            img_setting, img_pay, img_kasa;
    private TextView txt_user_name, txt_wheel_info, txt_phone_info, txt_slot_info, txt_video_info,
            txt_ref_info, txt_setting_info, txt_pay_info, txt_bank_info;
    private Animation small_to_big, rotate_one;
    private DocumentReference pointRef;
    private SharedPref pref;
    private String point, star, money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_new_home_menu);
        init();
        initAnim(400);
        initClicks();
        initFields();
    }

    private void initClicks() {
        img_home_avatar.setOnClickListener(this);
        img_wheel.setOnClickListener(this);
        img_phone.setOnClickListener(this);
        img_slot.setOnClickListener(this);
        img_kasa.setOnClickListener(this);
    }

    private void initFields() {
        Picasso.get()
                .load(SingletonUser.getInstance().getUserModel().getPhotoUrl())
                .resize(48, 48)
                .centerCrop()
                .transform(new CircleTransform())
                .into(img_home_avatar);
        txt_user_name.setText(SingletonUser.getInstance().getUserModel().getName().toUpperCase());
    }

    private void initAnim(int time) {


        img_wheel.startAnimation(small_to_big);
        img_phone.startAnimation(small_to_big);
        img_slot.startAnimation(small_to_big);
        img_video.startAnimation(small_to_big);
        img_referans.startAnimation(small_to_big);
        img_setting.startAnimation(small_to_big);
        img_pay.startAnimation(small_to_big);


    }

    private void init() {
        pref = new SharedPref(getApplicationContext());
        pointRef = Database.getUserInfo(pref.getUserId());
        txt_user_name = findViewById(R.id.txt_username);
        txt_wheel_info = findViewById(R.id.txt_wheel_info);
        txt_phone_info = findViewById(R.id.txt_phone_info);
        txt_slot_info = findViewById(R.id.txt_slot_info);
        txt_video_info = findViewById(R.id.txt_video_info);
        txt_ref_info = findViewById(R.id.txt_ref_info);
        txt_setting_info = findViewById(R.id.txt_setting_info);
        txt_pay_info = findViewById(R.id.txt_pay_info);
        txt_bank_info = findViewById(R.id.txt_bak_info);

        img_home_avatar = findViewById(R.id.img_home_avatar);
        img_wheel = findViewById(R.id.img_wheel);
        img_phone = findViewById(R.id.img_phone);
        img_slot = findViewById(R.id.img_slot);
        img_video = findViewById(R.id.img_video);
        img_referans = findViewById(R.id.img_ref);
        img_setting = findViewById(R.id.img_setting);
        img_pay = findViewById(R.id.img_pay);
        img_kasa = findViewById(R.id.img_bank);

        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);
        rotate_one = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_one);

    }

    private void goWheel() {
        img_wheel.setEnabled(false);
        img_wheel.startAnimation(rotate_one);
        rotate_one.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(NewSideHomeMenu.this, WheelActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img_wheel.setEnabled(true);

    }

    private void goSlot() {
        img_slot.setEnabled(false);
        img_slot.startAnimation(rotate_one);
        rotate_one.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(NewSideHomeMenu.this, SlotGameActivty.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                img_slot.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void showBankInfo() {
        FragmentManager fm = getSupportFragmentManager();
        BankInfoFrag bankInfoFrag = BankInfoFrag.newInstance(star, money, point);
        bankInfoFrag.show(fm, "bankinfo");
    }

    private void goPhone() {
        img_phone.setEnabled(false);
        img_phone.startAnimation(rotate_one);
        rotate_one.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(NewSideHomeMenu.this, SigninPhoneActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                img_phone.setEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_wheel:
                goWheel();
                break;

            case R.id.img_phone:
                goPhone();
                break;
            case R.id.img_slot:
                goSlot();
                break;
            case R.id.img_bank:
                showBankInfo();
                break;

        }
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
                    Long point1 = documentSnapshot.getLong("point");
                    Long star1 = documentSnapshot.getLong("star");

                    double tl = (double) point1 / Utils.TL_POINT;
                    money = String.valueOf(tl) + " ₺";
                    point = "" + point1;
                    star = "" + star1;
                    money = String.valueOf(tl) + " ₺";

                }
            }
        });
    }
}
