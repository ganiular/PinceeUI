package com.gnid.social.pincee.db.model;

import java.util.Objects;

public class MessageViewModel {
    public final long rowId;
    public final String senderId;
    public final String text;
    public final long timeSent;
    public final long tag;
    public int status;
    public final long tagAuthor;
    public final String tagMessage;
    private final Callback listener;
    public boolean isChecked;

    public MessageViewModel(long mid, String sender, String txt, long time, int status, Callback listener){
        rowId = mid;
        senderId = sender;
        text = txt;
        timeSent = time;
        this.tag = 0;
        this.tagAuthor = 0;
        this.tagMessage = null;
        this.status = status;
        this.listener = listener;
        if(listener != null) {
            listener.onItemCreated(this);
        }
    }

    public MessageViewModel(long mid, String sender, String txt, long time, int status, Callback listener, long tag, long tagAuthor, String tagMessage){
        rowId = mid;
        senderId = sender;
        text = txt;
        timeSent = time;
        this.tag = tag;
        this.tagAuthor = tagAuthor;
        this.tagMessage = tagMessage;
        this.status = status;
        this.listener = listener;
        if(listener != null) {
            listener.onItemCreated(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageViewModel that = (MessageViewModel) o;
        return rowId == that.rowId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId);
    }

    public interface Callback {
        void onItemCreated(MessageViewModel item);
    }
}
