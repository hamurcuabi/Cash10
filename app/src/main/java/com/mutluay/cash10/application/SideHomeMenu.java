package com.mutluay.cash10.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mutluay.cash10.R;
import com.mutluay.cash10.helper.SingletonUser;
import com.squareup.picasso.Picasso;

public class SideHomeMenu extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rel_main;
    private LinearLayout linear_side_menu, linear_home_top, linear_items_info, linear_items_two,
            linear_items_three, linear_other, linear_bottom;
    private ImageView img_home_avatar, img_side_avatar, img_wheel, img_phone, img_slot;
    private Button btn_exit, menu1, menu2, menu3, menu4;
    private Animation small_to_big, rotate_one;
    private TextView txt_username, txt_user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_home_menu);
        init();

        initClicks();
        initAnim(400);
        initFields();


    }

    private void initClicks() {
        img_home_avatar.setOnClickListener(this);
        img_wheel.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        img_phone.setOnClickListener(this);
        img_slot.setOnClickListener(this);
    }

    private void initFields() {
        Picasso.get()
                .load(SingletonUser.getInstance().getUserModel().getPhotoUrl())
                .resize(48, 48)
                .centerCrop()
                .into(img_home_avatar);
        txt_username.setText(SingletonUser.getInstance().getUserModel().getName().toUpperCase());
    }

    private void initAnim(int time) {
        //main
        linear_home_top.setTranslationX(100);
        linear_home_top.setAlpha(0);
        linear_items_info.setTranslationX(100);
        linear_items_info.setAlpha(0);
        linear_items_two.setTranslationX(100);
        linear_items_two.setAlpha(0);
        linear_items_three.setTranslationX(100);
        linear_items_three.setAlpha(0);
        linear_other.setTranslationX(100);
        linear_other.setAlpha(0);
        linear_bottom.setTranslationX(100);
        linear_bottom.setAlpha(0);

        linear_home_top.animate().translationX(0).alpha(1).setDuration(300).setStartDelay(time)
                .start();
        linear_items_info.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(time)
                .start();
        linear_items_two.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(time)
                .start();
        linear_items_three.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(time)
                .start();
        linear_other.animate().translationX(0).alpha(1).setDuration(700).setStartDelay(time)
                .start();
        linear_bottom.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(time)
                .start();
        //menu


    }

    private void init() {
        img_slot = findViewById(R.id.img_slot);
        txt_username = findViewById(R.id.txt_username);
        rel_main = findViewById(R.id.rel_main);
        linear_side_menu = findViewById(R.id.linear_side_menu);
        linear_home_top = findViewById(R.id.linear_home_top);
        linear_items_info = findViewById(R.id.linear_items_info);
        linear_items_two = findViewById(R.id.linear_items_two);
        linear_items_three = findViewById(R.id.linear_items_three);
        linear_other = findViewById(R.id.linear_other);
        linear_bottom = findViewById(R.id.linear_bottom);
        img_home_avatar = findViewById(R.id.img_home_avatar);
        img_phone = findViewById(R.id.img_diceroll);
        img_side_avatar = findViewById(R.id.img_side_avatar);
        img_wheel = findViewById(R.id.img_wheel);
        btn_exit = findViewById(R.id.btn_exit);
        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big);
        rotate_one = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_one);

        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        menu3 = findViewById(R.id.menu3);
        menu4 = findViewById(R.id.menu4);

        txt_user_info = findViewById(R.id.txt_user_name);

    }

    private void sideMenuAnim() {
        img_side_avatar.startAnimation(small_to_big);
        menu1.setTranslationY(-300);
        menu2.setTranslationY(-400);
        menu3.setTranslationY(-500);
        menu4.setTranslationY(-600);
        btn_exit.setTranslationY(-700);
        menu1.setAlpha(0);
        menu2.setAlpha(0);
        menu3.setAlpha(0);
        menu4.setAlpha(0);
        btn_exit.setAlpha(0);
        menu1.animate().translationY(0).alpha(1).setDuration(400)
                .start();
        menu2.animate().translationY(0).alpha(1).setDuration(500)
                .start();
        menu3.animate().translationY(0).alpha(1).setDuration(600)
                .start();
        menu4.animate().translationY(0).alpha(1).setDuration(700)
                .start();
        btn_exit.animate().translationY(0).alpha(1).setDuration(800)
                .start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_home_avatar:

//                linear_side_menu.animate().translationX(0).setDuration(400);
//                rel_main.animate().translationX(0).alpha((float) 0.5).setDuration(400);
//                sideMenuAnim();

                break;
            case R.id.btn_exit:
                rel_main.animate().translationX(-500).alpha(1).setDuration(400);
                linear_side_menu.animate().translationX(-500).setDuration(400);
                initAnim(100);

                break;
            case R.id.img_wheel:

                goWheel();
                break;

            case R.id.img_diceroll:
                goPhone();
                break;
            case R.id.img_slot:
                goSlot();
                break;

        }
    }

    private void goWheel() {
        img_wheel.setEnabled(false);
        img_wheel.startAnimation(rotate_one);
        Intent i = new Intent(SideHomeMenu.this, WheelActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);
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
                Intent i = new Intent(SideHomeMenu.this, SlotGameActivty.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                img_slot.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


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
                Intent i = new Intent(SideHomeMenu.this, SigninPhoneActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fleft, R.anim.fhelper);
                img_phone.clearAnimation();
                img_phone.setEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }
}
