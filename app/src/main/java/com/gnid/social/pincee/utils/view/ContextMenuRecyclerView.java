package com.gnid.social.pincee.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ContextMenuRecyclerView extends RecyclerView {
    private RecyclerViewContextMenuInfo mContextMenuInfo;

    public ContextMenuRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ContextMenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContextMenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
//        return mContextMenuInfo;
//    }

//    @Override
//    public boolean showContextMenuForChild(View originalView) {
//        final int longPressPosition = getChildAdapterPosition(originalView);
//        if (longPressPosition >= 0) {
//            final long longPressId = ((RecyclerItemView) originalView).getItemId();
//            mContextMenuInfo = new RecyclerViewContextMenuInfo(longPressPosition, longPressId);
//            return super.showContextMenuForChild(originalView);
//        }
//        return false;
//    }

    private static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public final long itemId;
        public final int position;

        public RecyclerViewContextMenuInfo(int position, long id){
            this.position  = position;
            this.itemId = id;
        }
    }
}
