package com.mutluay.cash10.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.google.firebase.firestore.Transaction;
import com.mutluay.cash10.R;
import com.mutluay.cash10.WheelGame.LuckyWheelView;
import com.mutluay.cash10.WheelGame.model.LuckyItem;
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.util.TextFont;
import com.mutluay.cash10.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class WheelActivity extends AppCompatActivity implements View.OnClickListener,
        LuckyWheelView.LuckyRoundItemSelectedListener, RewardedVideoAdListener {

    private static final String TAG = "WheelActivity";
    private boolean isTurning = false;
    private LuckyWheelView luckyWheelView;
    private Button btn_play;
    private SharedPref pref;
    private TextView txt_bank;
    private List<LuckyItem> model;
    private Animation small_to_big, zoom_in;
    private DocumentReference pointRef;
    private ImageView img_bank, img_popup;
    private Point p;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;
    private int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        model = whellItems();
        initAdd();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        init();
        initClick();
        //  canPlay();
        setTextCount(Utils.MAX_WHEEL_COUNT - pref.getWheelPlayed());
        Log.d(TAG, "onCreate: " + pref.getUserId());

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

    private void setTextCount(long i) {
        double tl = (double) i / Utils.TL_POINT;

//        DecimalFormat df = new DecimalFormat("#,##");
//        String dx=df.format(tl);
//        tl=Double.valueOf(dx);
        txt_bank.setText(String.valueOf(tl) + " ₺");
        Log.d(TAG, "setTextCount: " + tl);
    }

    private void initClick() {
        luckyWheelView.setLuckyRoundItemSelectedListener(this);
        btn_play.setOnClickListener(this);
    }

    private void canPlay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        if (pref.getWheelPlayed() == Utils.MAX_WHEEL_COUNT) {
            pref.setTimeWheel(dateFormat.format(date));

        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateInString = pref.getTimeWheel();
            Date old = null;
            try {
                old = sdf.parse(dateInString);
                if (date.after(old)) {
                    pref.setWheelPlayed(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }


    }

    private void playing() {

            if (!isTurning) {
                // int old = pref.getWheelPlayed();
                // if (old <= Utils.MAX_WHEEL_COUNT) {
                btn_play.setText(getString(R.string.whelling));
                btn_play.setEnabled(false);
                isTurning = true;
                int index = getRandomIndex();
                luckyWheelView.startLuckyWheelWithTargetIndex(index);
//                old++;
//                pref.setWheelPlayed(old);
//                setTextCount(Utils.MAX_WHEEL_COUNT - old);


        }
    }

    private void init() {
        img_popup = findViewById(R.id.popup_info);
        img_popup.setOnClickListener(this);
        btn_play = findViewById(R.id.play);
        luckyWheelView = findViewById(R.id.luckyWheel);
        luckyWheelView.setData(model);
        luckyWheelView.setRound(3);
        txt_bank = findViewById(R.id.txt_bank);
        img_bank = findViewById(R.id.img_bank);
        pref = new SharedPref(getApplicationContext());
        txt_bank.setTypeface(TextFont.logo(getApplicationContext()));
        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big);
        zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        txt_bank.startAnimation(small_to_big);
        pointRef = Database.getUserNewInfo(pref.getUserId());
    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(model.size() - 1) + 1;

    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    private List<LuckyItem> whellItems() {
        List<LuckyItem> data = new ArrayList<>();
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = "220";
        luckyItem1.point = 220;
        luckyItem1.icon = R.drawable.diamond;
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "100";
        luckyItem2.point = 100;
        luckyItem2.icon = R.drawable.diamond;
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "20";
        luckyItem3.point = 20;
        luckyItem3.icon = R.drawable.diamond;
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = "120";
        luckyItem4.point = 120;
        luckyItem4.icon = R.drawable.diamond;
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = "75";
        luckyItem5.point = 75;
        luckyItem5.icon = R.drawable.diamond;
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = "1000";
        luckyItem6.point = 1000;
        luckyItem6.icon = R.drawable.diamond;
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = "50";
        luckyItem7.point = 50;
        luckyItem7.icon = R.drawable.diamond;
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = "500";
        luckyItem8.point = 500;
        luckyItem8.icon = R.drawable.diamond;
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.text = "40";
        luckyItem9.point = 40;
        luckyItem9.icon = R.drawable.diamond;
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.text = "12";
        luckyItem10.point = 12;
        luckyItem10.icon = R.drawable.diamond;
        luckyItem10.color = 0xffFFF3E0;
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.text = "350";
        luckyItem11.point = 350;
        luckyItem11.icon = R.drawable.diamond;
        luckyItem11.color = 0xffFFE0B2;
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.text = "8";
        luckyItem12.point = 8;
        luckyItem12.icon = R.drawable.diamond;
        luckyItem12.color = 0xffFFCC80;
        data.add(luckyItem12);

        LuckyItem luckyItem13 = new LuckyItem();
        luckyItem13.text = "125";
        luckyItem13.point = 125;
        luckyItem13.icon = R.drawable.diamond;
        luckyItem13.color = 0xffFFF3E0;


        LuckyItem luckyItem14 = new LuckyItem();
        luckyItem14.text = "250";
        luckyItem14.point = 250;
        luckyItem14.icon = R.drawable.diamond;
        luckyItem14.color = 0xffFFE0B2;
        data.add(luckyItem14);

        LuckyItem luckyItem15 = new LuckyItem();
        luckyItem15.text = "100";
        luckyItem15.point = 100;
        luckyItem15.icon = R.drawable.diamond;
        luckyItem15.color = 0xffFFCC80;
        data.add(luckyItem15);

        LuckyItem luckyItem16 = new LuckyItem();
        luckyItem16.text = "13";
        luckyItem16.point = 13;
        luckyItem16.icon = R.drawable.diamond;
        luckyItem16.color = 0xffFFF3E0;
        data.add(luckyItem16);

        LuckyItem luckyItem17 = new LuckyItem();
        luckyItem17.text = "150";
        luckyItem17.point = 150;
        luckyItem17.icon = R.drawable.diamond;
        luckyItem17.color = 0xffFFE0B2;
        data.add(luckyItem17);

        LuckyItem luckyItem18 = new LuckyItem();
        luckyItem18.text = "90";
        luckyItem18.point = 90;
        luckyItem18.icon = R.drawable.diamond;
        luckyItem18.color = 0xffFFCC80;
        data.add(luckyItem18);

        LuckyItem luckyItem19 = new LuckyItem();
        luckyItem19.text = "55";
        luckyItem19.point = 55;
        luckyItem19.icon = R.drawable.diamond;
        luckyItem19.color = 0xffFFE0B2;
        data.add(luckyItem19);
        return data;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.play:
                playing();
                break;
            case R.id.popup_info:
                if (p != null) showPopup(WheelActivity.this, p);
                break;
        }
    }

    @Override
    public void LuckyRoundItemSelected(int index) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        btn_play.setText(getString(R.string.play));
        btn_play.setEnabled(true);
        isTurning = false;
        executeTransaction(model.get(index - 1).point);
        Tostcu.succes(getApplicationContext(), "" + model.get(index - 1).point);
        Log.d(TAG, "LuckyRoundItemSelected: " + index);

//        Toast.makeText(getApplicationContext(),""+ model.get(index).point, Toast.LENGTH_SHORT)
//                .show();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(Utils.RewardVideo4,
                new AdRequest.Builder().build());
    }

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(pref.getUserId());
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

    private void executeTransactionStar(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(pref.getUserId());
                DocumentSnapshot exampleNoteSnapshot = transaction.get(point);
                long newPoint = exampleNoteSnapshot.getLong("star") + i;
                transaction.update(point, "star", newPoint);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //yıldız arttı
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
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
                    Long point = documentSnapshot.getLong("point");
                    setTextCount(point);
                    txt_bank.startAnimation(small_to_big);


                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        img_popup.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
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
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup, false);

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
        TextView info = layout.findViewById(R.id.txt_info);
        info.setText(getResources().getString(R.string.info_whell));

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
        executeTransaction(100);
        Tostcu.succes(WheelActivity.this, "100!!");
    }

    private void executeTransactionHeart(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserNewInfo(pref.getUserId());
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

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
