package com.gnid.social.pincee.utils.view;

import android.os.Handler;
import android.view.View;


public abstract class OnSingleDoubleClickListener implements View.OnClickListener {
    private static View clickedView;
    private static boolean isClicked;
    private static final Handler handler = new Handler();
    private static final Runnable runnable = OnSingleDoubleClickListener::timeOut;

    protected abstract void onSingleClick(View v);
    protected abstract void onDoubleClick(View v);

    @Override
    final public void onClick(View v) {
        if(isClicked && v == clickedView){
           timeOut();
           onDoubleClick(v);
        }else {
            isClicked = true;
            clickedView = v;
            handler.postDelayed(runnable, 300);
            onSingleClick(v);
        }
    }

    private static void timeOut(){
        isClicked = false;
        clickedView = null;
        handler.removeCallbacks(runnable);
    }
}
