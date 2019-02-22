package com.mutluay.cash10.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mutluay.cash10.R;

public class BankInfoFrag extends DialogFragment {

    private String star, point, bank;
    private TextView txt_bank, txt_star, txt_point;

    public static BankInfoFrag newInstance(String star, String bank, String point) {

        Bundle args = new Bundle();
        args.putString("star", star);
        args.putString("point", point);
        args.putString("bank", bank);
        BankInfoFrag fragment = new BankInfoFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bankinfo_fragment, container);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        star = getArguments().getString("star", "");
        point = getArguments().getString("point", "");
        bank = getArguments().getString("bank", "");
        txt_bank = view.findViewById(R.id.txt_bank);
        txt_star = view.findViewById(R.id.txt_diamond);
        txt_point = view.findViewById(R.id.txt_point);

        txt_point.setText(point);
        txt_bank.setText(bank);
        txt_star.setText(star);


    }
}
