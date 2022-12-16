package com.gnid.social.pincee.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.gnid.social.pinceeui.R;

public class FrameLayoutItemView extends FrameLayout {
    private long itemId;

    public FrameLayoutItemView(Context context) {
        super(context);
    }

    public FrameLayoutItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
        Log.i("TAG", "RecyclerItemView: ");
    }

    public FrameLayoutItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FrameLayoutItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }
}
