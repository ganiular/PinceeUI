package com.gnid.social.pincee.db.model;

import android.net.Uri;

import java.util.Objects;

final public class ChatViewModel {
    public Uri photoUrl;
    public String userName;
    final public long rowId;
    final public String userId;
    public String lastMessage;
    final public long timestamp;
    public int noticeCount;
    public long sortKey;
    public boolean isChecked;
    final private Callback listener;

    public ChatViewModel(String uid, String name, String message, Uri photo, long rowId, int count, long mid, long time, Callback listener) {
        userId = uid;
        userName = name;
        lastMessage = message;
        photoUrl = photo;
        this.rowId = rowId;
        noticeCount = count;
        sortKey = mid;
        timestamp = time;
        onCreated(this);
        this.listener = listener;
        if(listener != null) {
            listener.onItemCreated(this);
        }
    }

    private void onCreated(ChatViewModel chatViewModel) {
    }

    @Override
    public String toString() {
        return "ChatViewModel{" +
                "userName='" + userName + '\'' +
                ", rowId=" + rowId +
                ", lastMessageId=" + sortKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatViewModel that = (ChatViewModel) o;
        return this.rowId == that.rowId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId);
    }

    public interface Callback{
        void onItemCreated(Object item);
    }
}
