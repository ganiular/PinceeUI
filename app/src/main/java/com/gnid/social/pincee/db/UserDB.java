package com.gnid.social.pincee.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SortedList;

import com.gnid.social.pincee.db.model.ChatViewModel;
import com.gnid.social.pincee.db.model.MessageViewModel;
import com.gnid.social.pincee.utils.adapters.MessageListAdapter;

import java.util.LinkedList;

public class UserDB extends BaseDB{

    public UserDB(@Nullable Context context) {
        super(context);
    }

    public static void queryChats(ChatViewModel.Callback listener) {
        for (int i = 0; i < 15; i++) {
            new ChatViewModel(
                    "userid"+i,"Username"+i,"Message"+i,null, i,i*90,i, 30, listener
            );
        }
    }

    public static void queryMessages(MessageViewModel.Callback listener) {
        int k=0;
        for (int i = 0; i < 15; i++) {
            boolean j = i%2 == 0;
            new MessageViewModel(
                    i, j? "me":"other", j? "Hello my neighbor.":"How are you doing?",300,
                    j? (k++)%4:0, listener, i, 1L, "Hello my neighbor."
            );
        }
    }

    @Override
    void onCreateTable(SQLiteDatabase db) {

    }

    @Override
    void onDropTable(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
