package com.emrhmrc.cash10.adapter.GenericRcwAdapter;

import com.emrhmrc.cash10.adapter.GenericRcwAdapter.BaseRecyclerListener;

public interface OnItemClickListener<T> extends BaseRecyclerListener {

    void onItemClicked(T item);
}