package com.mutluay.cash10.adapter.GenericRcwAdapter;

public interface OnItemClickListener<T> extends BaseRecyclerListener {

    void onItemClicked(T item);
}