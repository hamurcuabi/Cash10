package com.mutluay.cash10.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
import com.mutluay.cash10.R;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.fragment.BankInfoFrag;
import com.mutluay.cash10.helper.CircleTransform;
import com.mutluay.cash10.helper.MyServices;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.helper.SingletonUser;
import com.mutluay.cash10.model.UserModel;
import com.mutluay.cash10.model.VersionModel;
import com.mutluay.cash10.util.Utils;
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
    private ImageView img_home_avatar, img_wheel, img_watchadd,
            img_pay, img_notif;
    private TextView txt_user_name, txt_wheel_info, txt_phone_info, txt_slot_info, txt_video_info,
            txt_ref_info, txt_setting_info, txt_pay_info, txt_bank_info, txt_online;
    private Animation small_to_big, rotate_one, blink;
    private DocumentReference pointRef, pointRef2;
    private SharedPref pref;
    private String point, star, money;
    private WebView img_gif;
    private RewardedVideoAd mRewardedVideoAd;
    private Point p;
    private boolean find;
    private boolean used;
    private ImageView facebook, whatsapp, insta, youtube;
    private String online_code;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_new_home_menu);
        MobileAds.initialize(this, Utils.AppId);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        init();
        initAnim();
        initClicks();
        initFields();
        initGif();
        startService(new Intent(getBaseContext(), MyServices.class));
        executeTransactionOnline(1);
        initAdd();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(Utils.RewardVideo,
                new AdRequest.Builder().build());
    }

    private void initAdd() {
        MobileAds.initialize(this,
                Utils.AppId);
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

    private void initGif() {
        //   img_gif.loadUrl("file:///android_asset/gif.gif");
    }

    private void initClicks() {

        img_wheel.setOnClickListener(this);
        img_watchadd.setOnClickListener(this);
        img_pay.setOnClickListener(this);
        img_notif.setOnClickListener(this);

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
        img_watchadd.startAnimation(small_to_big);
        img_pay.startAnimation(small_to_big);
        img_notif.startAnimation(small_to_big);


    }

    private void init() {
        pref = new SharedPref(getApplicationContext());
        pointRef = Database.getUserNewInfo(pref.getUserId());
        pointRef2 = Database.getOnlineUserInfo("QU1u0eEKd3C2pptSSkxK");
        txt_user_name = findViewById(R.id.txt_username);
        img_home_avatar = findViewById(R.id.img_home_avatar);
        img_wheel = findViewById(R.id.img_wheel);
        img_watchadd = findViewById(R.id.img_watchads);
        img_pay = findViewById(R.id.img_pay);
        img_notif = findViewById(R.id.img_notif);
        txt_online = findViewById(R.id.txt_online);

        facebook = findViewById(R.id.facebook);
        whatsapp = findViewById(R.id.whatsapp);
        insta = findViewById(R.id.instagram);
        youtube = findViewById(R.id.youtube);
        facebook.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        insta.setOnClickListener(this);
        youtube.setOnClickListener(this);


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


    private void showBankInfo() {
        FragmentManager fm = getSupportFragmentManager();
        BankInfoFrag bankInfoFrag = BankInfoFrag.newInstance(star, money, point);
        bankInfoFrag.show(fm, "bankinfo");
    }


    private void goNotifList() {
        img_notif.setEnabled(false);
        Intent i = new Intent(NewSideHomeMenu.this, NotifListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fleft, R.anim.fhelper);


    }

    private void goPay() {
        if (Integer.parseInt(point) >= 5000000) {

            img_pay.setEnabled(false);
            Intent i = new Intent(NewSideHomeMenu.this, PayActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fleft, R.anim.fhelper);
        } else {

            Tostcu.warning(getApplicationContext(), "Bakiye En az 50 Tl olmalıdır");

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_wheel:
                goWheel();
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
            case R.id.facebook:
                openBrowser("https://www.facebook.com/mutlu.ay.10297");
                break;
            case R.id.whatsapp:
                openBrowser("https://chat.whatsapp.com/Brn3iTD6Ipp3qorWr0NxRC");
                break;
            case R.id.instagram:
                openBrowser("https://www.instagram.com/infiniteyazilim/");
                break;
            case R.id.youtube:
                openBrowser("https://www.youtube.com/channel/UC2P13-0J81DQhhbabkSXmrg");
                break;

        }
    }

    private void openBrowser(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link)));
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


                    double tl = (double) point1 / Utils.TL_POINT;
                    money = String.valueOf(tl) + " ₺";
                    point = "" + point1;


                }
            }
        });
        pointRef2.addSnapshotListener(this, new EventListener<DocumentSnapshot>
                () {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    Long online = documentSnapshot.getLong("code");

                    txt_online.setText("" + online + " Online");
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
        img_pay.setEnabled(true);
        img_notif.setEnabled(true);
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
        Tostcu.succes(getApplicationContext(), "500 Puan!!");
        executeTransaction(500);

    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: NewSideHome");
        mRewardedVideoAd.destroy(this);
        executeTransactionOnline(-1);
        super.onDestroy();
    }

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(SingletonUser.getInstance()
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

    private void getOnlineCode() {
        Database.getOnlineUserRef().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VersionModel model = documentSnapshot.toObject(VersionModel.class);
                            txt_online.setText("" + model.getCode() + " Online");
                            online_code = "" + model.getCode();
                        }


                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Tostcu.error(getApplicationContext(), "Hata Tekrar Deneyiniz!");
            }
        });


    }

    private void executeTransactionOnline(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getOnlineUserInfo("QU1u0eEKd3C2pptSSkxK");
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("code") + i;
                transaction.update(point, "code", newPoint);
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
                DocumentReference point = Database.getUserNewInfo(id);
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
        if (p == null) {
            int[] location = new int[2];

            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            txt_user_name.getLocationOnScreen(location);
            p.x = location[0];
            p.y = location[1];
        }
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
                        if (!used) {
                            lookToRef(edt_message.getText().toString());
                            popup.dismiss();
                        } else Tostcu.error(getApplicationContext(), "Referans Kodu Kullanıldı!");
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

        Database.getUserNewRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    executeTransactionRef(500, model.getDocId());
                    Tostcu.succes(getApplicationContext(), "500 Puan !");
                    Database.getUserNewInfo(SingletonUser.getInstance().getUserModel().getDocId())
                            .update("used_ref", true);

                } else {
                    Tostcu.error(getApplicationContext(), "Kod Bulunamdı!");
                }

            }
        });

    }


}
