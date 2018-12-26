package com.emrhmrc.cash10.application;

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

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.WheelGame.LuckyWheelView;
import com.emrhmrc.cash10.WheelGame.model.LuckyItem;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.util.TextFont;
import com.emrhmrc.cash10.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class WheelActivity extends AppCompatActivity implements View.OnClickListener, LuckyWheelView.LuckyRoundItemSelectedListener {

    private static final String TAG = "WheelActivity";
    private boolean isTurning = false;
    private LuckyWheelView luckyWheelView;
    private Button btn_play;
    private SharedPref pref;
    private TextView txt_point, txt_bank, txt_diamond;
    private List<LuckyItem> model;
    private Animation small_to_big, zoom_in;
    private DocumentReference pointRef;
    private ImageView img_point, img_bank, img_diamond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        model = whellItems();
        init();
        initClick();
        //  canPlay();
        setTextCount(Utils.MAX_WHEEL_COUNT - pref.getWheelPlayed());
        Log.d(TAG, "onCreate: "+pref.getUserId());

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
        btn_play = findViewById(R.id.play);
        luckyWheelView = findViewById(R.id.luckyWheel);
        luckyWheelView.setData(model);
        luckyWheelView.setRound(5);
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
        small_to_big = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.small_to_big);
        zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        txt_point.startAnimation(small_to_big);
        img_point.startAnimation(zoom_in);
        txt_bank.startAnimation(small_to_big);
        txt_diamond.startAnimation(small_to_big);
        pointRef = Database.getUserInfo(pref.getUserId());
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
        luckyItem1.text = "2";
        luckyItem1.point = 2;
        luckyItem1.icon = R.drawable.diamond;
        luckyItem1.with_icon = true;
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "50";
        luckyItem2.point = 50;
        luckyItem2.icon = R.drawable.avatar;
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "500";
        luckyItem3.point = 500;
        luckyItem3.icon = R.drawable.phone;
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = "40";
        luckyItem4.point = 40;
        luckyItem4.icon = R.drawable.ads;
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = "12";
        luckyItem5.point = 12;
        luckyItem5.icon = R.drawable.avatar;
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = "350";
        luckyItem6.point = 350;
        luckyItem6.icon = R.drawable.coins;
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = "8";
        luckyItem7.point = 8;
        luckyItem7.icon = R.drawable.diamond;
        luckyItem7.with_icon = true;
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = "125";
        luckyItem8.point = 125;
        luckyItem8.icon = R.drawable.setting;
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.text = "250";
        luckyItem9.point = 250;
        luckyItem9.icon = R.drawable.tick;
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.text = "100";
        luckyItem10.point = 100;
        luckyItem10.icon = R.drawable.message;
        luckyItem10.color = 0xffFFF3E0;
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.text = "6";
        luckyItem11.point = 6;
        luckyItem11.icon = R.drawable.diamond;
        luckyItem11.with_icon = true;
        luckyItem11.color = 0xffFFE0B2;
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.text = "150";
        luckyItem12.point = 150;
        luckyItem12.icon = R.drawable.diamond;
        luckyItem12.color = 0xffFFCC80;
        data.add(luckyItem12);

        LuckyItem luckyItem13 = new LuckyItem();
        luckyItem13.text = "15";
        luckyItem13.point = 15;
        luckyItem13.icon = R.drawable.diamond;
        luckyItem13.color = 0xffFFF3E0;


        LuckyItem luckyItem14 = new LuckyItem();
        luckyItem14.text = "90";
        luckyItem14.point = 90;
        luckyItem14.icon = R.drawable.diamond;
        luckyItem14.color = 0xffFFE0B2;
        data.add(luckyItem14);

        LuckyItem luckyItem15 = new LuckyItem();
        luckyItem15.text = "4";
        luckyItem15.point = 4;
        luckyItem15.icon = R.drawable.diamond;
        luckyItem15.with_icon = true;
        luckyItem15.color = 0xffFFCC80;
        data.add(luckyItem15);

        LuckyItem luckyItem16 = new LuckyItem();
        luckyItem16.text = "100";
        luckyItem16.point = 100;
        luckyItem16.icon = R.drawable.diamond;
        luckyItem16.color = 0xffFFF3E0;
        data.add(luckyItem16);

        LuckyItem luckyItem17 = new LuckyItem();
        luckyItem17.text = "20";
        luckyItem17.point = 20;
        luckyItem17.icon = R.drawable.diamond;
        luckyItem17.color = 0xffFFE0B2;
        data.add(luckyItem17);

        LuckyItem luckyItem18 = new LuckyItem();
        luckyItem18.text = "120";
        luckyItem18.point = 120;
        luckyItem18.icon = R.drawable.diamond;
        luckyItem18.color = 0xffFFCC80;
        data.add(luckyItem18);

        LuckyItem luckyItem19 = new LuckyItem();
        luckyItem19.text = "75";
        luckyItem19.point = 75;
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
        }
    }

    @Override
    public void LuckyRoundItemSelected(int index) {
        btn_play.setText(getString(R.string.play));
        btn_play.setEnabled(true);
        isTurning = false;
        executeTransaction(model.get(index - 1).point);
        if ((index) == 1 || (index) == 7 || (index) == 11 || (index) == 14)
            executeTransactionStar(1);
        Log.d(TAG, "LuckyRoundItemSelected: " + index);


//        Toast.makeText(getApplicationContext(),""+ model.get(index).point, Toast.LENGTH_SHORT)
//                .show();
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
                Log.d(TAG, "onFailure: ."+e.getMessage());
            }
        });
    }

    private void executeTransactionStar(final int i) {
        Database.getDb().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference point = Database.getUserInfo(pref.getUserId());
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

    @Override
    protected void onStop() {
        super.onStop();

    }
}
