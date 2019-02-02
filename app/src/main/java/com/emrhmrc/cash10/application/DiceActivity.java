package com.emrhmrc.cash10.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.helper.SharedPref;
import com.emrhmrc.cash10.util.TextFont;
import com.emrhmrc.cash10.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random();
    private static final String TAG = "DiceActivity";
    private ImageView img1, img2;
    private Button btn_start;
    private Spinner spinner, spinner2;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private int index = 0, index2 = 0, current, current2;
    private MediaPlayer mediaPlayer;
    private SharedPref pref;
    private TextView txt_point, txt_bank, txt_diamond;
    private DocumentReference pointRef;
    private ImageView img_point, img_bank, img_diamond, img_popup;
    private Point p;
    private Animation small_to_big;

    public static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
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
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        pointRef = Database.getUserInfo(pref.getUserId());

        list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        spinner = findViewById(R.id.spn);
        spinner2 = findViewById(R.id.spn2);
        adapter = new ArrayAdapter<>(this,
                R.layout.spn_item, list);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                index2 = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R
                        .anim.dice_roll);
                final Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R
                        .anim.dice_roll);
                final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.roll);
                        mediaPlayer.start();
                        btn_start.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int win_count = 0;
                        int value = randomDiceValue();
                        if (animation == anim1) {

                            switch (value) {
                                case 1:
                                    img1.setImageResource(R.drawable.d1);
                                    current = 0;
                                    break;
                                case 2:
                                    img1.setImageResource(R.drawable.d2);
                                    current = 1;
                                    break;
                                case 3:
                                    img1.setImageResource(R.drawable.d3);
                                    current = 2;
                                    break;
                                case 4:
                                    img1.setImageResource(R.drawable.d4);
                                    current = 3;
                                    break;
                                case 5:
                                    img1.setImageResource(R.drawable.d5);
                                    current = 4;
                                    break;
                                case 6:
                                    img1.setImageResource(R.drawable.d6);
                                    current = 5;
                                    break;

                            }
                            if (current == index) {
                                win_count += 500;
                                Toast.makeText(getApplicationContext(), "1.Tahminden 500!", Toast
                                        .LENGTH_SHORT).show();
                            }


                        } else if (animation == anim2) {
                            switch (value) {
                                case 1:
                                    img2.setImageResource(R.drawable.d1);
                                    current2 = 0;
                                    break;
                                case 2:
                                    img2.setImageResource(R.drawable.d2);
                                    current2 = 1;
                                    break;
                                case 3:
                                    img2.setImageResource(R.drawable.d3);
                                    current2 = 2;
                                    break;
                                case 4:
                                    img2.setImageResource(R.drawable.d4);
                                    current2 = 3;
                                    break;
                                case 5:
                                    img2.setImageResource(R.drawable.d5);
                                    current2 = 4;
                                    break;
                                case 6:
                                    img2.setImageResource(R.drawable.d6);
                                    current2 = 5;
                                    break;

                            }
                            if (current2 == index2) {
                                win_count += 500;
                                Toast.makeText(getApplicationContext(), "2.Tahminden 500!", Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                        if (win_count > 0) executeTransaction(win_count);
                        mediaPlayer.stop();
                        btn_start.setEnabled(true);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };

                anim1.setAnimationListener(animationListener);
                anim2.setAnimationListener(animationListener);

                img1.startAnimation(anim1);
                img2.startAnimation(anim2);
            }
        });
        img_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (p != null) showPopup(DiceActivity.this, p);
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
        info.setText(getResources().getString(R.string.info_dice));

    }
}