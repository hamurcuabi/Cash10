package com.emrhmrc.cash10.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.emrehmrc.tostcu.Tostcu;
import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.fragment.BankInfoFrag;
import com.emrhmrc.cash10.helper.CircleTransform;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.model.UserModel;
import com.emrhmrc.cash10.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewSideHomeMenu extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {
    private static final String TAG = "NewSideHomeMenu";
    private final String reward_id = "ca-app-pub-1202140444527551/8027554673";
    UserModel model;
    private ImageView img_home_avatar, img_wheel, img_addphone, img_slot, img_watchadd,
            img_diceroll, img_rulet, img_pay, img_kasa, img_notif, img_headcoins, img_ref;
    private TextView txt_user_name, txt_wheel_info, txt_phone_info, txt_slot_info, txt_video_info,
            txt_ref_info, txt_setting_info, txt_pay_info, txt_bank_info;
    private Animation small_to_big, rotate_one, blink;
    private DocumentReference pointRef;
    private SharedPref pref;
    private String point, star, money;
    private WebView img_gif;
    private RewardedVideoAd mRewardedVideoAd;
    private Point p;
    private boolean find;
    private boolean used;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_new_home_menu);
        MobileAds.initialize(this, "ca-app-pub-1202140444527551~5620883183");

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        init();
        initAnim();
        initClicks();
        initFields();
        initGif();

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private void initGif() {
        //   img_gif.loadUrl("file:///android_asset/gif.gif");
    }

    private void initClicks() {

        img_wheel.setOnClickListener(this);
        img_watchadd.setOnClickListener(this);
        img_slot.setOnClickListener(this);
        img_kasa.setOnClickListener(this);
        img_diceroll.setOnClickListener(this);
        img_headcoins.setOnClickListener(this);
        img_rulet.setOnClickListener(this);
        img_pay.setOnClickListener(this);
        img_notif.setOnClickListener(this);
        img_addphone.setOnClickListener(this);
        img_ref.setOnClickListener(this);
    }

    private void initFields() {
        Picasso.get()
                .load(SingletonUser.getInstance().getUserModel().getPhotoUrl())
                .resize(48, 48)
                .centerCrop()
                .transform(new CircleTransform())
                .into(img_home_avatar);
        txt_user_name.setText(SingletonUser.getInstance().getUserModel().getName().toUpperCase());
        point = "" + SingletonUser.getInstance().getUserModel().getPoint();
        star = "" + SingletonUser.getInstance().getUserModel().getStar();
        double tl = (double) SingletonUser.getInstance().getUserModel().getPoint() / Utils.TL_POINT;
        money = String.valueOf(tl) + " ₺";

    }

    private void initAnim() {

        img_wheel.startAnimation(small_to_big);
        img_slot.startAnimation(small_to_big);
        img_watchadd.startAnimation(small_to_big);
        img_diceroll.startAnimation(small_to_big);
        img_headcoins.startAnimation(small_to_big);
        img_rulet.startAnimation(small_to_big);

        img_pay.startAnimation(small_to_big);
        img_notif.startAnimation(small_to_big);
        img_addphone.startAnimation(small_to_big);


    }

    private void init() {
        img_gif = findViewById(R.id.img_gif);
        pref = new SharedPref(getApplicationContext());
        pointRef = Database.getUserInfo(pref.getUserId());
        txt_user_name = findViewById(R.id.txt_username);

        img_home_avatar = findViewById(R.id.img_home_avatar);

        img_wheel = findViewById(R.id.img_wheel);
        img_slot = findViewById(R.id.img_slot);
        img_watchadd = findViewById(R.id.img_watchads);
        img_rulet = findViewById(R.id.img_rulet);
        img_headcoins = findViewById(R.id.img_headcoins);
        img_diceroll = findViewById(R.id.img_diceroll);

        img_addphone = findViewById(R.id.img_addphone);
        img_pay = findViewById(R.id.img_pay);
        img_notif = findViewById(R.id.img_notif);
        img_kasa = findViewById(R.id.img_bank);
        img_ref = findViewById(R.id.img_ref);


        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big2);
        rotate_one = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_one);
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

    }

    private void goWheel() {
        img_wheel.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, WheelActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);

    }

    private void goSlot() {
        img_slot.setEnabled(false);
        img_slot.startAnimation(blink);
        Intent i = new Intent(NewSideHomeMenu.this, SlotGameActivty.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);

    }

    private void showBankInfo() {
        FragmentManager fm = getSupportFragmentManager();
        BankInfoFrag bankInfoFrag = BankInfoFrag.newInstance(star, money, point);
        bankInfoFrag.show(fm, "bankinfo");
    }

    private void goPhone() {
        img_addphone.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, MerketActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }

    private void goDice() {
        img_diceroll.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, DiceActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }

    private void goHeadCoins() {
        img_headcoins.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, CoinFlipActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }

    private void goRulet() {
        img_rulet.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, CasinoGameActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }

    private void goNotifList() {
        img_notif.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, NotifListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }


    private void goPay() {
        if (Double.parseDouble(money.replace("₺", "").trim()) >= 20.0) {
            img_pay.setEnabled(false);
            Intent i = new Intent(NewSideHomeMenu.this, PayActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fleft, R.anim.fhelper);
        } else {

            Tostcu.warning(getApplicationContext(), "Bakiye En az 20 Tl olmalıdır");

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_wheel:
                goWheel();
                break;

            case R.id.img_diceroll:
                goDice();
                break;
            case R.id.img_slot:
                goSlot();
                break;
            case R.id.img_headcoins:
                goHeadCoins();
                break;
            case R.id.img_rulet:
                goRulet();
                break;
            case R.id.img_bank:
                showBankInfo();
                break;
            case R.id.img_addphone:
                goPhone();
                break;
            case R.id.img_watchads:
                openAdmob();
                break;
            case R.id.img_pay:
                goPay();
                break;
            case R.id.img_notif:
                goNotifList();
                break;
            case R.id.img_ref:
                if (p != null) showPopup(NewSideHomeMenu.this, p);
                break;

        }
    }


    private void openAdmob() {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm");
        Date date = null;
        Date date2 = null;
        try {
            String datelast = pref.getLastAdTime();
            date = sdf.parse(datelast);
            Calendar now = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, 5);
            Log.d(TAG, "openAdmob: " + now.getTime().toString());
            Log.d(TAG, "openAdmob: " + cal.getTime().toString());
            if (now.after(cal)) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                } else Toast.makeText(this, "Yüklenmedi Tekrar deneyin!",
                        Toast.LENGTH_SHORT).show();
            } else {
                int second = (int) (cal.getTimeInMillis() - now.getTimeInMillis()) / 1000;
                Log.d(TAG, "openAdmob: " + second);
                int min = second / 60;

                Tostcu.warning(getApplicationContext(), min + " Dk " + (second - (min * 60)) + " Saniye sonra aktif " +
                        "olacaktır.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            } else Tostcu.info(getApplicationContext(), "Yüklenmedi Tekrar Deneyin");
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
                    boolean is_used = documentSnapshot.getBoolean("used_ref");
                    used = is_used;

                    double tl = (double) point1 / Utils.TL_POINT;
                    money = String.valueOf(tl) + " ₺";
                    point = "" + point1;
                    star = "" + star1;
                    money = String.valueOf(tl) + " ₺";

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRewardedVideoAd.resume(this);
        img_wheel.setEnabled(true);
        img_watchadd.setEnabled(true);
        img_slot.setEnabled(true);
        img_kasa.setEnabled(true);
        img_diceroll.setEnabled(true);
        img_headcoins.setEnabled(true);
        img_rulet.setEnabled(true);
        img_pay.setEnabled(true);
        img_notif.setEnabled(true);
        img_addphone.setEnabled(true);
    }

    @Override
    public void onRewarded(RewardItem reward) {

        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        //  Toast.makeText(this, "Tamamlanmadı!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //  Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        // Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //  Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        // Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

        String formatted = format1.format(cal.getTime());
        pref.setLastAdTime(formatted);
        Tostcu.succes(getApplicationContext(), "1000 Puan!!");
        executeTransaction(1000);

    }


    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserInfo(SingletonUser.getInstance()
                        .getUserModel().getDocId());
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("point") + i;
                transaction.update(point, "point", newPoint);
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

    private void executeTransactionRef(final int i, final String id) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserInfo(id);
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("point") + i;
                transaction.update(point, "point", newPoint);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                executeTransaction(500);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ." + e.getMessage());
            }
        });
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.

        //Initialize the Point with x, and y positions

            int popupWidth = 200;
            int popupHeight = 150;

            // Inflate the popup_layout.xml
            LinearLayout viewGroup = context.findViewById(R.id.popuplnr);
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.popup_ref, viewGroup, false);

            // Creating the PopupWindow
            final PopupWindow popup = new PopupWindow(context);
            popup.setContentView(layout);
        /*popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);*/
            popup.setFocusable(true);

            // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
            int OFFSET_X = 0;
            int OFFSET_Y = 0;
            // Clear the default translucent background
            popup.setBackgroundDrawable(new BitmapDrawable());
            // Displaying the popup at the specified location, + offsets.
            popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

            // Getting a reference to Close button, and close the popup when clicked.
            final TextView txt_ref = layout.findViewById(R.id.txt_ref);
            final TextInputEditText edt_message = layout.findViewById(R.id.edt_message);
            Button btn_send = layout.findViewById(R.id.btn_send);
            txt_ref.setText(SingletonUser.getInstance().getUserModel().getRef_code());
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(edt_message.getText().toString())) {
                        if (!TextUtils.equals(txt_ref.getText().toString(), edt_message.getText()
                                .toString())) {
                            if(!used) {
                                lookToRef(edt_message.getText().toString());
                                popup.dismiss();
                            }
                            else Tostcu.error(getApplicationContext(), "Referans Kodu Kullanıldı!");
                        } else {
                            Tostcu.warning(getApplicationContext(), "Kendi Kodunuzu Giremezsiniz!");
                        }
                    } else {
                        Tostcu.warning(getApplicationContext(), "Kod girilmedi!");
                    }
                }
            });

        }


    private void lookToRef(final String code) {

        Database.getUserRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    model = documentSnapshot.toObject(UserModel.class);

                    if (TextUtils.equals(model.getRef_code(), code)) {
                        find = true;
                        break;

                    } else find = false;

                }
                if (find) {
                    //executeTransaction(500);
                    executeTransactionRef(500, model.getDocId());
                    Tostcu.succes(getApplicationContext(), "500 Puan !");
                    Database.getUserInfo(SingletonUser.getInstance().getUserModel().getDocId())
                            .update("used_ref", true);

                } else {
                    Tostcu.error(getApplicationContext(), "Kod Bulunamdı!");
                }

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        img_ref.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }
}
