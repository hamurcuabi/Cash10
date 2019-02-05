package com.emrhmrc.cash10.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.BaseViewHolder;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.emrhmrc.cash10.model.NotifModel;


public class NotifViewHolder extends BaseViewHolder<NotifModel,
        OnItemClickListener<NotifModel>> {

    private TextView txtCount, txtDate;
    private LinearLayout lnritem;

    public NotifViewHolder(View itemView) {
        super(itemView);
        txtCount = itemView.findViewById(R.id.txtCount);
        txtDate = itemView.findViewById(R.id.txtDate);
        lnritem = itemView.findViewById(R.id.lnritem);
    }

    @Override
    public void onBind(final NotifModel item, @Nullable final OnItemClickListener<NotifModel> listener) {
        txtCount.setText("" + (getAdapterPosition() + 1));
        txtDate.setText(item.getDate());

        if (listener != null) {
            //if min sdk >24
            // txt_test.setOnClickListener(v->listener.onItemClicked(item));
            txtCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(item);
                }
            });


        }

    }


}

