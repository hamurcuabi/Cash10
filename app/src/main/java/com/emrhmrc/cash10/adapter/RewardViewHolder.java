package com.emrhmrc.cash10.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrhmrc.cash10.R;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.BaseViewHolder;
import com.emrhmrc.cash10.adapter.GenericRcwAdapter.OnItemClickListener;
import com.emrhmrc.cash10.model.RewardModel;


public class RewardViewHolder extends BaseViewHolder<RewardModel,
        OnItemClickListener<RewardModel>> {

    private TextView txt_howmany, txt_reward_info;
    private ImageView img_reward;


    public RewardViewHolder(View itemView) {
        super(itemView);
        txt_howmany = itemView.findViewById(R.id.txt_howmany);
        txt_reward_info = itemView.findViewById(R.id.txt_reward_info);
        img_reward = itemView.findViewById(R.id.img_reward);

    }

    @Override
    public void onBind(final RewardModel item, @Nullable final OnItemClickListener<RewardModel> listener) {
        txt_howmany.setText("" + item.getHowmany()+" Elmas");
        txt_reward_info.setText(item.getName());
        img_reward.setImageResource(item.getImage());

        if (listener != null) {
            //if min sdk >24
            // txt_test.setOnClickListener(v->listener.onItemClicked(item));
            img_reward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(item);
                }
            });


        }

    }


}

