package com.gnid.social.pincee.ui.actionbar;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gnid.social.pinceeui.R;

public class MessageChatActionMode implements ActionMode.Callback {
    private final String TAG = MessageChatActionMode.class.getSimpleName();
    private final Implementor implementor;

    public MessageChatActionMode(Implementor implementor) {
        this.implementor = implementor;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        implementor.getMenuInflater().inflate(R.menu.chat_message_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.i(TAG, "onPrepareActionMode: ");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.i(TAG, "onActionItemClicked: "+item.getTitle());
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    public interface Implementor{
        MenuInflater getMenuInflater();
    }
}
