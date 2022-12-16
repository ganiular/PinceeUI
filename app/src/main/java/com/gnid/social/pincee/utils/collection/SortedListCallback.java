package com.gnid.social.pincee.utils.collection;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.gnid.social.pincee.db.model.ChatViewModel;

public class SortedListCallback extends SortedList.Callback<ChatViewModel> {
    final private String TAG = SortedListCallback.class.getSimpleName();
    private final RecyclerView.Adapter mAdapter;

    public SortedListCallback(RecyclerView.Adapter adapter){
        mAdapter = adapter;
    }
    @Override
    public int compare(ChatViewModel o1, ChatViewModel o2) {
        return Long.compare(o1.sortKey, o2.sortKey);
    }

    @Override
    public void onChanged(int position, int count) {
        mAdapter.notifyItemChanged(position);
        Log.i(TAG, "onChanged: position:"+position+" count:"+count);
    }

    // this as called after areItemTheSame is true for further validation
    @Override
    public boolean areContentsTheSame(ChatViewModel oldItem, ChatViewModel newItem) {
        return oldItem.rowId == newItem.rowId;
    }

    // if this method returns true, areContentsTheSame() will be call for further
    // validation
    @Override
    public boolean areItemsTheSame(ChatViewModel item1, ChatViewModel item2) {
        return item1.rowId == item2.rowId;
    }

    @Override
    public void onInserted(int position, int count) {
        mAdapter.notifyItemInserted(position);
        Log.i(TAG, "onInserted: position:"+position+" count:"+count);
    }

    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRemoved(position);
        Log.i(TAG, "onRemoved: position:"+position+" count:"+count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        Log.i(TAG, "onMoved: from:"+fromPosition+" to:"+toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }
}
