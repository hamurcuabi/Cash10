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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

public class SlotGameActivty extends AppCompatActivity {
    private static final String TAGA = "SlotGameActivty";
    private final int[] RESOURCE = {R.drawable.ic_android, R.drawable.ic_cake,
            R.drawable.ic_check, R.drawable.ic_cutter,
            R.drawable.ic_flag, R.drawable.ic_hand,
            R.drawable.ic_box};
    private final String[] TAG = {"android", "cake", "check", "cutter", "flag", "hand", "pencil"};
    private Button start, stop;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private Thread firstThread;
    private Thread secondThread;
    private Thread thirdThread;
    private Animation animation;
    private SharedPref pref;
    private DocumentReference pointRef;
    private TextView txt_point, txt_bank, txt_diamond;
    private Animation small_to_big, zoom_in;
    private ImageView img_point, img_bank, img_diamond, img_popup;
    private Point p;

    private RandomizerRunnable[] randomizerRunnables;
    private int currentRunnable;

    private boolean isResuming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_game_activty);

        isResuming = false;
        img_popup = findViewById(R.id.popup_info);
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
        randomizerRunnables = new RandomizerRunnable[3];

        image1 = findViewById(R.id.img1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        start.setEnabled(true);
        stop.animate().alpha(0).translationX(300).setDuration(400).start();
        stop.setEnabled(false);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.stb);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRoll();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRoll();
            }
        });
        img_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (p != null) showPopup(SlotGameActivty.this, p);
            }
        });
    }

    private void startRoll() {
        currentRunnable = 0;
        randomizerRunnables[0] = new RandomizerRunnable(image1);
        randomizerRunnables[1] = new RandomizerRunnable(image2);
        randomizerRunnables[2] = new RandomizerRunnable(image3);

        int image1Pos, image2Pos, image3Pos;

        if (isResuming) {
            image1Pos = getIndexOnTag(image1.getTag().toString());
            image2Pos = getIndexOnTag(image2.getTag().toString());
            image3Pos = getIndexOnTag(image3.getTag().toString());
        } else {
            image1Pos = (int) (Math.random() * RESOURCE.length);
            image2Pos = (int) (Math.random() * RESOURCE.length);
            image3Pos = (int) (Math.random() * RESOURCE.length);
        }

        randomizerRunnables[0].setCurrentIndex(image1Pos);
        randomizerRunnables[1].setCurrentIndex(image2Pos);
        randomizerRunnables[2].setCurrentIndex(image3Pos);

        image1.setTag(TAG[image1Pos]);
        image1.setTag(TAG[image2Pos]);
        image1.setTag(TAG[image3Pos]);

        firstThread = new Thread(randomizerRunnables[0]);
        secondThread = new Thread(randomizerRunnables[1]);
        thirdThread = new Thread(randomizerRunnables[2]);

        randomizerRunnables[0].setRunning(true);
        randomizerRunnables[1].setRunning(true);
        randomizerRunnables[2].setRunning(true);

        firstThread.start();
        secondThread.start();
        thirdThread.start();

       start.animate().alpha(0).translationX(300).setDuration(400).start();
        start.setEnabled(false);
        stop.animate().alpha(1).translationX(0).setDuration(400).start();
        stop.setEnabled(true);
    }

    private int getIndexOnTag(String tag) {
        int result = -1;
        for (int i = 0; i < TAG.length; i++) {
            if (TAG[i].equalsIgnoreCase(tag)) result = i;
        }
        return result;
    }

    private void stopRoll() {
        if (currentRunnable < randomizerRunnables.length - 1) {
            randomizerRunnables[currentRunnable].setRunning(false);
            currentRunnable++;
        } else {
            isResuming = true;

            randomizerRunnables[currentRunnable].setRunning(false);

            String image1Tag = image1.getTag().toString();
            String image2Tag = image2.getTag().toString();
            String image3Tag = image3.getTag().toString();

            if (image1Tag.equalsIgnoreCase(image2Tag) && image2Tag.equalsIgnoreCase(image3Tag)) {
                executeTransaction(500);
                Toast.makeText(this, "500!!", Toast.LENGTH_SHORT).show();
            } else if (image1Tag.equalsIgnoreCase(image2Tag) || image2Tag.equalsIgnoreCase
                    (image3Tag) || image1Tag.equalsIgnoreCase(image3Tag)) {

                executeTransaction(200);
                Toast.makeText(this, "200!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tekrar Dene!!", Toast.LENGTH_SHORT).show();
            }
            start.animate().alpha(1).translationX(0).setDuration(400).start();
            start.setEnabled(true);
            stop.animate().alpha(0).translationX(300).setDuration(400).start();
            stop.setEnabled(false);
        }
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
                Log.d(TAGA, "onFailure: ." + e.getMessage());
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
                Log.d(TAGA, "onFailure: " + e.getMessage());
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

                    Log.d(TAGA, e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    Long point = documentSnapshot.getLong("point");
                    Long star = documentSnapshot.getLong("star");
                    txt_diamond.setText("" + star);
                    txt_point.setText("" + point);
                    double tl = (double) point / Utils.TL_POINT;
                    txt_bank.setText(String.valueOf(tl) + " ₺");
                    txt_bank.startAnimation(small_to_big);
                    txt_point.startAnimation(small_to_big);
                    txt_diamond.startAnimation(small_to_big);

                }
            }
        });

    }

    private class RandomizerRunnable implements Runnable {

        private int currentIndex = 0;
        private boolean running;
        private ImageView imageView;

        public RandomizerRunnable(ImageView imageView) {
            this.imageView = imageView;
            this.currentIndex = 0;
            this.running = false;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    SlotGameActivty.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(RESOURCE[currentIndex]);
                            imageView.setTag(TAG[currentIndex]);
                            imageView.startAnimation(animation);
                        }
                    });
                    Thread.sleep(400);
                    currentIndex++;
                    if (currentIndex >= RESOURCE.length) currentIndex = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        info.setText(getResources().getString(R.string.info_slot));

    }
}
