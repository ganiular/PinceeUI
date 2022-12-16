package com.gnid.social.pincee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnid.social.pinceeui.MainActivity;
import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.db.UserDB;
import com.gnid.social.pincee.db.model.MessageViewModel;
import com.gnid.social.pincee.ui.actionbar.MessageChatActionMode;
import com.gnid.social.pincee.ui.dialog.MessageDeleteDialog;
import com.gnid.social.pincee.utils.MyClipboardManager;
import com.gnid.social.pincee.utils.adapters.MessageListAdapter;
import com.gnid.social.pincee.utils.edit.ChatMessageTextWatcher;
import com.gnid.social.pincee.utils.view.ContextMenuRecyclerView;

import java.util.Arrays;
import java.util.LinkedList;

public class ChatActivity extends AppCompatActivity implements MessageListAdapter.MyCallbacks, MessageChatActionMode.Implementor {
    public static final String VIEW_NAME_PROFILE_IMAGE = "profile_image_view";
    public static final String VIEW_NAME_USERNAME = "username_view";
    private final String TAG = ChatActivity.class.getSimpleName();
    private ContextMenuRecyclerView rv;
    private MessageListAdapter adapter;

    public static final String EXTRA_RECEIVER_ID = "com.gnid.pincee.RECEIVER";
    private boolean isEmptyVisible = false;

    private LinkedList<MessageViewModel> selectedItems;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.chat_message_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    item.setEnabled(false);
                    new MessageDeleteDialog(ChatActivity.this, selectedItems.size()) {
                        @Override
                        protected void onCancel(DialogInterface dialog) {
                            item.setEnabled(true);
                        }
                        @Override
                        public void onItemClick(AlertDialog dialog, int which) {
                            switch (which) {
                                case DELETE_FOR_ME:
                                    Toast.makeText(ChatActivity.this, "delete for me", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    mode.finish();
                                    break;
                                case DELETE_FOR_ALL:
                                    Toast.makeText(ChatActivity.this, "delete for all", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    mode.finish();
                                    break;
                                case CANCELED:
                                    dialog.cancel();
                            }
                        }
                    };
                    return true;
                case R.id.action_reply:
                    item.setEnabled(false);
                    openTag(selectedItems.get(0));
                    mode.finish();
                    return true;
                case R.id.action_copy:
                    item.setEnabled(false);
                    MessageViewModel[] sortedItems = selectedItems.toArray(new MessageViewModel[0]);
                    Arrays.sort(sortedItems, (o1, o2) -> Long.compare(o1.rowId, o2.rowId));

                    StringBuilder copyText = new StringBuilder();

                    // if items are more than one, separate them with new line
                    MessageViewModel message = sortedItems[sortedItems.length-1];
                    copyText.append(message.text);
                    for(int i=sortedItems.length-2; i>=0; i--){
                        copyText.append('\n').append(sortedItems[i].text);
                    }
                    MyClipboardManager.copyTextToClipboard(ChatActivity.this, copyText);
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.isSelectionMode = false;
            for(MessageViewModel item : selectedItems){
                item.isChecked = false;
            }
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            mode.finish();
            selectedItems = null;
            actionMode = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // TODO: Add custom back nav icon just like that of whatsapp
        //  see guild at stackoverflow  q: android toolbar back arrow with icon like whatsapp
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.findViewById(R.id.nav_back).setOnClickListener(v->backNavigation());
        toolbar.findViewById(R.id.nav_profile).setOnClickListener(v->gotoProfile());
        setSupportActionBar(toolbar);

        ImageView profileImage = toolbar.findViewById(R.id.profile_image);
        TextView username = toolbar.findViewById(R.id.username);
        profileImage.setTransitionName(VIEW_NAME_PROFILE_IMAGE);
        username.setTransitionName(VIEW_NAME_USERNAME);

        adapter = new MessageListAdapter(this, this);
        UserDB.queryMessages(adapter);
        rv = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
//        registerForContextMenu(rv);

        EditText edtMessage = findViewById(R.id.message_input_bar).findViewById(R.id.edit_message);
        edtMessage.addTextChangedListener(new ChatMessageTextWatcher(this));
    }

    public void onEmptyList(boolean v) {
        if(v && !isEmptyVisible) {
            isEmptyVisible = true;
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }
        else if(!v && isEmptyVisible){
            isEmptyVisible = false;
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemDoubleClicked(View view, MessageViewModel message) {
        openTag(message);
        makeVibration();
    }

    private void makeVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK));
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                vibrator.vibrate(30);
            }
        }
    }

    @Override
    public boolean onItemLongClick(View v, int layoutPosition) {
        if(adapter.isSelectionMode) {
            adapter.markItem(v, layoutPosition);
            return true;
        }

        if(actionModeCallback == null){
            actionModeCallback = new MessageChatActionMode(this);
        }
        actionMode = startActionMode(actionModeCallback);
        adapter.isSelectionMode = true;
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.markItem(v, layoutPosition);
        return true;
    }

    @Override
    public void onItemSelected(int position, MessageViewModel item) {
        if(item.isChecked){
            if(selectedItems == null) selectedItems = new LinkedList<>();
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }

        int count = selectedItems.size();
        if(count == 0){
            actionModeCallback.onDestroyActionMode(actionMode);
            return;
        } else if(count == 1){
            actionMode.getMenu().findItem(R.id.action_reply).setVisible(true);
        }
        else if(count == 2){
            actionMode.getMenu().findItem(R.id.action_reply).setVisible(false);
        }
        actionMode.setTitle(String.valueOf(selectedItems.size()));
    }

    @Override
    public int getDp(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private void openTag(MessageViewModel message){
        View btnCloseTag = findViewById(R.id.button_close);
        btnCloseTag.setVisibility(View.VISIBLE);
        btnCloseTag.setOnClickListener(this::closeTag);

        View msgInputBar = findViewById(R.id.message_input_bar);
        msgInputBar.getBackground().setLevel(1);

        ViewGroup contentTagContainer = msgInputBar.findViewById(R.id.frame_layout);
        contentTagContainer.setVisibility(View.VISIBLE);

        TextView txtAuthorName = contentTagContainer.findViewById(R.id.text_view_3);
        int highLightColor;
        if (message.senderId.equals("me")) {
            highLightColor = getResources().getColor(R.color.my_message_color);
        } else {
            highLightColor = getResources().getColor(R.color.his_message_color);
        }
        txtAuthorName.setTextColor(highLightColor);
        ((GradientDrawable) contentTagContainer.getBackground()).setColor(highLightColor);
    }

    private void closeTag(View v) {
        View msgInputBar = findViewById(R.id.message_input_bar);
        msgInputBar.getBackground().setLevel(0);

        ViewGroup contentTagContainer = msgInputBar.findViewById(R.id.frame_layout);
        contentTagContainer.setVisibility(View.GONE);

        v.setOnClickListener(null);
        v.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_activity_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: "+item.getItemId());
        if (item.getItemId() == android.R.id.home){
            backNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backNavigation(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void gotoProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.chat_message_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }
}