package com.gnid.social.pincee.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LinearLayoutItemView extends LinearLayout {
    private String itemId;

    public LinearLayoutItemView(Context context) {
        super(context);
    }

    public LinearLayoutItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
        Log.i("TAG", "RecyclerItemView: ");
    }

    public LinearLayoutItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
