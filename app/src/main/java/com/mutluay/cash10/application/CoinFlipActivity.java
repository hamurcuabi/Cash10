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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.mutluay.cash10.api.Database;
import com.mutluay.cash10.helper.SharedPref;
import com.mutluay.cash10.util.TextFont;
import com.mutluay.cash10.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity implements Animation.AnimationListener,
        RewardedVideoAdListener {

    private static final String TAG = "CoinFlipActivity";
    private ImageView img;
    private Animation flip, flip_helper;
    private boolean isStop = true, isback = true;
    private Button btn_start;
    private int i = 0;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private int index = 0;
    private int current;
    private SharedPref pref;
    private TextView txt_point, txt_bank, txt_diamond;
    private DocumentReference pointRef;
    private ImageView img_point, img_bank, img_diamond, img_popup;
    private Point p;
    private Animation small_to_big;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private long heart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coin_flip);
        initAdd();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        img_popup = findViewById(R.id.popup_info);
        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big);
        txt_point = findViewById(R.id.txt_point);
        txt_bank = findViewById(R.id.txt_bank);
        txt_diamond = findViewById(R.id.txt_diamond);
        img_point = findViewById(R.id.img_star);
        img_bank = findViewById(R.id.img_bank);
        img_diamond = findViewById(R.id.img_diamond);
        pref = new SharedPref(getApplicationContext());
        txt_point.setTypeface(TextFont.logo(getApplicationContext()));
        txt_bank.setTypeface(TextFont.logo(getApplicationContext()));
        txt_diamond.setTypeface(TextFont.logo(getApplicationContext()));
        btn_start = findViewById(R.id.start);
        img = findViewById(R.id.img1);
        flip = AnimationUtils.loadAnimation(this, R.anim.flip);
        flip_helper = AnimationUtils.loadAnimation(this, R.anim.flip_helper);
        flip_helper.setAnimationListener(this);
        flip.setAnimationListener(this);
        flip_helper.setRepeatCount(3);
        flip.setRepeatCount(3);
        pointRef = Database.getUserNewInfo(pref.getUserId());

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heart <= -30) {
                    Tostcu.info(getApplicationContext(), "Hakkınız bitti Reklamı İzleyin 30 Hak " +
                            "Kazanın!");
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    } else Tostcu.warning(getApplicationContext(), "Yüklenmedi Tekrar deneyin!");
                } else {
                    Tostcu.info(getApplicationContext(), "" + (heart+30) + " Hakkınız Kaldı");
                    executeTransactionHeart(-1);
                    img.clearAnimation();
                    img.setAnimation(flip);
                    img.startAnimation(flip);
                }




                 /*else {

                    int number = new Random().nextInt(100) + 1;
                    if (number % 2 == 0) {
                        img.setImageResource(R.drawable.tos);
                    } else {
                        img.setImageResource(R.drawable.head);
                    }
                    img.clearAnimation();

                }
                isStop = !isStop;*/
            }
        });
        list = new ArrayList<>();
        list.add("3 Yaprak Tarafı");
        list.add("Dolar Tarafı");

        spinner = findViewById(R.id.spn);
        adapter = new ArrayAdapter<>(this,
                R.layout.spn_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        img_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (p != null) showPopup(CoinFlipActivity.this, p);
            }
        });
    }

    private void setTextCount(long i) {
        txt_point.setText(String.valueOf(i));
        double tl = (double) i / Utils.TL_POINT;

//        DecimalFormat df = new DecimalFormat("#,##");
//        String dx=df.format(tl);
//        tl=Double.valueOf(dx);
        txt_bank.setText(String.valueOf(tl) + " ₺");
        Log.d(TAG, "setTextCount: " + tl);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(Utils.RewardVideo3,
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

    private void initAdd() {
        MobileAds.initialize(this,
                Utils.AppId);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
                    Long star = documentSnapshot.getLong("star");
                    Long heart2 = documentSnapshot.getLong("heart");
                    heart = heart2;
                    setTextCount(point);
                    txt_diamond.setText("" + star);
                    txt_bank.startAnimation(small_to_big);
                    txt_point.startAnimation(small_to_big);
                    txt_diamond.startAnimation(small_to_big);

                }
            }
        });

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {


    }

    @Override
    public void onAnimationRepeat(Animation animation) {

        i++;
        if (animation == flip) {

            img.clearAnimation();
            img.setAnimation(flip_helper);
            img.startAnimation(flip_helper);

            if (isback) {
                img.setImageResource(R.drawable.tos);

            } else {
                img.setImageResource(R.drawable.head);

            }
            isback = !isback;

        } else {
            img.clearAnimation();
            img.setAnimation(flip);
            img.startAnimation(flip);

            if (isback) {
                img.setImageResource(R.drawable.tos);

            } else {
                img.setImageResource(R.drawable.head);

            }
            isback = !isback;
        }
        if (i % 13 == 0) {
            int number = new Random().nextInt(100) + 1;
            if (number % 2 == 0) {
                img.setImageResource(R.drawable.tos);
                current = 1;
            } else {
                img.setImageResource(R.drawable.head);
                current = 0;
            }
            img.clearAnimation();
            if (index == current) {
                executeTransaction(30);
                Tostcu.succes(getApplicationContext(), "30!");
            } else
                Tostcu.error(getApplicationContext(), "Tekrar Dene!");
            isStop = !isStop;


        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        img_diamond.getLocationOnScreen(location);

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
        info.setText(getResources().getString(R.string.info_coin));

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

        Tostcu.succes(getApplicationContext(), "30 Hak Eklendi!!");
        executeTransactionHeart(30);

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
                // Log.d(TAG, "onFailure: ." + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
