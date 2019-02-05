package com.emrhmrc.cash10.application;

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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.emrehmrc.tostcu.Tostcu;
import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.util.TextFont;
import com.emrhmrc.cash10.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CasinoGameActivity extends AppCompatActivity {

    private static final String TAG = "CasinoGameActivity";
    private static final String[] sectors = {"Siyah", "Kırmızı", "YEŞİL"
    };
    private static final Random RANDOM = new Random();
    private static final float HALF_SECTOR = 360f / 24f / 2f;
    private List<String> list;
    private Spinner spinner;
    private Button btn_start;
    private int degree = 0, degreeOld = 0;
    private ImageView img, cursor;
    private ArrayAdapter<String> adapter;
    private int index = 0;
    private boolean win, big_win;
    private Animation animation, small_to_big;
    private SharedPref pref;
    private TextView txt_point, txt_bank, txt_diamond;
    private DocumentReference pointRef;
    private ImageView img_point, img_bank, img_diamond, img_popup;
    private Point p;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casino_game);
        initAdd();
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
        pointRef = Database.getUserInfo(pref.getUserId());
        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        btn_start = findViewById(R.id.start);
        cursor = findViewById(R.id.cursor);
        img = findViewById(R.id.img1);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spin();
            }
        });
        spinner = findViewById(R.id.spn);
        list = Arrays.asList(sectors);
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
                if (p != null) showPopup(CasinoGameActivity.this, p);
            }
        });
    }

    private void initAdd() {
        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
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
        txt_point.setText(String.valueOf(i));
        double tl = (double) i / Utils.TL_POINT;

//        DecimalFormat df = new DecimalFormat("#,##");
//        String dx=df.format(tl);
//        tl=Double.valueOf(dx);
        txt_bank.setText(String.valueOf(tl) + " ₺");
        Log.d(TAG, "setTextCount: " + tl);
    }

    private void executeTransaction(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserInfo(pref.getUserId());
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
                    setTextCount(point);
                    txt_diamond.setText("" + star);
                    txt_bank.startAnimation(small_to_big);
                    txt_point.startAnimation(small_to_big);
                    txt_diamond.startAnimation(small_to_big);


                }
            }
        });

    }

    public void spin() {
        degreeOld = degree % 360;
        degree = RANDOM.nextInt(360) + 720;
        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(3600);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // we empty the result text view when the animation start
                // resultTv.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // we display the correct sector pointed by the triangle at the end of the rotate animation
                //  resultTv.setText(getSector(360 - (degree % 360)));
                getSector(360 - (degree % 360));
                cursor.clearAnimation();
                Log.d(TAG, "index " + index);
                if (win) {
                    executeTransaction(500);
                    Tostcu.succes(getApplicationContext(), "500!");
                } else if (big_win) {
                    executeTransaction(1000);
                    Tostcu.succes(getApplicationContext(), "1000!");
                }
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cursor.startAnimation(animation);
        // we start the animation
        img.startAnimation(rotateAnim);
        Log.d(TAG, "spin: " + degree);
    }

    private void getSector(int degrees) {
        String text = "";
        big_win = false;
        if (degrees >= (HALF_SECTOR * 1) && degrees < (HALF_SECTOR * 3)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 3) && degrees < (HALF_SECTOR * 5)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 5) && degrees < (HALF_SECTOR * 7)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 7) && degrees < (HALF_SECTOR * 9)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 9) && degrees < (HALF_SECTOR * 11)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 11) && degrees < (HALF_SECTOR * 13)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 13) && degrees < (HALF_SECTOR * 15)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 15) && degrees < (HALF_SECTOR * 17)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 17) && degrees < (HALF_SECTOR * 19)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 19) && degrees < (HALF_SECTOR * 21)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 21) && degrees < (HALF_SECTOR * 23)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 23) && degrees < (HALF_SECTOR * 25)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 25) && degrees < (HALF_SECTOR * 27)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 27) && degrees < (HALF_SECTOR * 29)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 29) && degrees < (HALF_SECTOR * 31)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 31) && degrees < (HALF_SECTOR * 33)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 33) && degrees < (HALF_SECTOR * 35)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 35) && degrees < (HALF_SECTOR * 37)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 37) && degrees < (HALF_SECTOR * 39)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 39) && degrees < (HALF_SECTOR * 41)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 41) && degrees < (HALF_SECTOR * 43)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 43) && degrees < (HALF_SECTOR * 45)) {
            text = "KIRMIZI";
            if (index == 1) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 45) && degrees < (HALF_SECTOR * 47)) {
            text = "SİYAH";
            if (index == 0) win = true;
            else win = false;
        } else if (degrees >= (HALF_SECTOR * 47) && degrees < 360 || degrees >= 0 && degrees <
                (HALF_SECTOR * 49)) {
            text = "YEŞİL";
            if (index == 2) {
                win = false;
                big_win = true;
            }

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
        info.setText(getResources().getString(R.string.info_rulet));

    }
}
