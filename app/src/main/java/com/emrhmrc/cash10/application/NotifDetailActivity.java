package com.emrhmrc.cash10.application;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.emrehmrc.tostcu.Tostcu;
import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.api.Database;
import com.emrhmrc.cash10.api.OneSignalTask;
import com.emrhmrc.cash10.helper.Functions;
import com.emrhmrc.cash10.helper.SingletonNotif;
import com.emrhmrc.cash10.helper.SingletonUser;
import com.emrhmrc.cash10.model.NotifModel;
import com.google.android.gms.tasks.OnSuccessListener;


public class NotifDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText edt_name, edt_mail, edt_methode, edt_phone, edt_message, edt_admin;
    private Button btn_ok, btn_cancel;
    private boolean is_login;
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_detail);
        init();
        setTexts(SingletonNotif.getInstance().getNotifModel());
    }

    private void setTexts(NotifModel model) {
        edt_name.setText(model.getName());
        edt_admin.setText(model.getAdmin_message());
        edt_mail.setText(model.getEmail() + " " + model.getPhone());
        edt_message.setText(model.getMessage());
        edt_methode.setText(model.getMethode());
        edt_admin.setText(model.getAdmin_message());

    }

    private void init() {

        edt_name = findViewById(R.id.edt_name);
        edt_mail = findViewById(R.id.edt_mail);
        edt_methode = findViewById(R.id.edt_methode);
        edt_message = findViewById(R.id.edt_message);
        edt_admin = findViewById(R.id.edt_admin);
        btn_ok = findViewById(R.id.btn_accept);
        btn_cancel = findViewById(R.id.btn_iptal);
        if (!SingletonUser.getInstance().getUserModel().isAdmin()) {
            btn_cancel.setVisibility(View.GONE);
            btn_ok.setVisibility(View.GONE);
            edt_admin.setKeyListener(null);
            edt_name.setEnabled(false);
            edt_mail.setEnabled(false);
            edt_methode.setEnabled(false);

        }
        else {
            edt_name.setEnabled(false);
            edt_mail.setEnabled(false);
            edt_message.setKeyListener(null);
        }
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_accept:
                accept();
                break;
            case R.id.btn_iptal:
                cancel();
                break;


        }
    }

    private void cancel() {
        final NotifModel model = SingletonNotif.getInstance().getNotifModel();
        model.setDate(Functions.DateToText());
        model.setIsdone(false);
        model.setAdmin_message(edt_admin.getText().toString());
        Database.getUserNotif(model.getNotifDocId()).update("date", model.getDate(), "isdone", true,
                "admin_message", model.getAdmin_message()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendMsj("Ödemeniz Onaylanmadı");
            }
        });

    }

    private void accept() {
        final NotifModel model = SingletonNotif.getInstance().getNotifModel();
        model.setDate(Functions.DateToText());
        model.setIsdone(true);
        model.setAdmin_message(edt_admin.getText().toString());
        Database.getUserNotif(model.getNotifDocId()).update("date", model.getDate(), "isdone", true,
                "admin_message", model.getAdmin_message()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendMsj("Ödemeniz Onaylandı");
            }
        });


    }


    private void sendMsj(String msj) {
        Tostcu.succes(getApplicationContext(), "İşlem Yapıldı!");
        OneSignalTask oneSignalTask = new OneSignalTask();
        oneSignalTask.execute(SingletonNotif.getInstance().getNotifModel().getPlayId(), msj);
        finish();
    }
}
