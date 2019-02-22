package com.mutluay.cash10.adapter.GenericRcwAdapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Base ViewHolder to be used with the generic adapter.
 * {@link GenericAdapter}
 *
 * @param <T> type of objects, which will be used in the adapter's data set
 * @param <L> click listener {@link BaseRecyclerListener}
 */
public abstract class BaseViewHolder<T, L extends BaseRecyclerListener> extends RecyclerView.ViewHolder {

    private L listener;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T item, @Nullable L listener);

    /**
     * Bind data to the item.
     * Override this method for using the payloads in order to achieve the full power of DiffUtil
     * {@link android.support.v7.util.DiffUtil.Callback}
     *
     * @param item object, associated with the item.
     */
    public void onBind(T item, List<Object> payloads) {
        onBind(item, listener);
    }

    protected L getListener() {
        return listener;
    }

}