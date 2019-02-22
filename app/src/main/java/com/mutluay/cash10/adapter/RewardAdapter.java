package com.mutluay.cash10.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.mutluay.cash10.R;
import com.mutluay.cash10.adapter.GenericRcwAdapter.GenericAdapter;
import com.mutluay.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.mutluay.cash10.model.RewardModel;


public class RewardAdapter extends GenericAdapter<RewardModel,
        OnItemClickListener<RewardModel>,
        RewardViewHolder> {

    public RewardAdapter(Context context, OnItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewtype göre ekelriz
        switch (viewType) {
            case 1:
                return new RewardViewHolder(inflate(R.layout.item_reward, parent));

            default:
                return new RewardViewHolder(inflate(R.layout.item_reward, parent));
        }
    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }
}