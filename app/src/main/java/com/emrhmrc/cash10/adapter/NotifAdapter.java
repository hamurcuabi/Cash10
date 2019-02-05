package com.emrhmrc.cash10.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.GenericAdapter;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.emrhmrc.cash10.model.NotifModel;


public class NotifAdapter extends GenericAdapter<NotifModel,
        OnItemClickListener<NotifModel>,
        NotifViewHolder> {

    public NotifAdapter(Context context,OnItemClickListener listener) {
        super(context,listener);
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewtype göre ekelriz
        switch (viewType) {
            case 1:
                return new NotifViewHolder(inflate(R.layout.item_done_layout, parent));

            default:
                return new NotifViewHolder(inflate(R.layout.item_waiting_layout, parent));
        }
    }

    @Override
    public int getItemViewType(int position) {
        final NotifModel item = getItem(position);
        if (item.isIsdone()) return 1;
        else return 0;
    }
}