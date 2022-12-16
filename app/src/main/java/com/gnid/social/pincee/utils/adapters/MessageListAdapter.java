package com.gnid.social.pincee.utils.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.db.model.MessageViewModel;
import com.gnid.social.pincee.utils.helper.TimeFormatter;
import com.gnid.social.pincee.utils.view.OnSingleDoubleClickListener;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ReceivedMessageHolder> implements MessageViewModel.Callback{
    private static final String TAG = MessageListAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT_WITH_TAG = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_WITH_TAG = 4;
    private final LayoutInflater mInflater;
    private final ArrayList<MessageViewModel> dataSet;
    private final MyCallbacks callbacks;
    public boolean isSelectionMode = false;

    public MessageListAdapter(Context context, MyCallbacks callbacks){
        mInflater = LayoutInflater.from(context);
        this.dataSet = new ArrayList<>(50);
        this.callbacks = callbacks;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public MessageListAdapter.ReceivedMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = mInflater.inflate(R.layout.item_chat_received, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SENT:
                view = mInflater.inflate(R.layout.item_chat_sent, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SENT_WITH_TAG:
                view =  mInflater.inflate(R.layout.item_chat_sent_with_tag, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED_WITH_TAG:
                view = mInflater.inflate(R.layout.item_chat_received_with_tag, parent, false);
                return new ReceivedMessageHolder(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageViewModel message = dataSet.get(position);
        if(message.senderId.equals("me")){
            if(message.tag == 0)
                return VIEW_TYPE_MESSAGE_SENT;
            else
                return VIEW_TYPE_MESSAGE_SENT_WITH_TAG;
        } else {
            if(message.tag == 0)
                return VIEW_TYPE_MESSAGE_RECEIVED;
            else
                return VIEW_TYPE_MESSAGE_RECEIVED_WITH_TAG;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ReceivedMessageHolder holder, int position) {
        MessageViewModel message = dataSet.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        int size = dataSet.size();
        callbacks.onEmptyList(size == 0);
        return size;
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).rowId;
    }

    @Override
    public void onItemCreated(MessageViewModel item) {
        dataSet.add(item);
        notifyItemInserted(0);
    }

    public void markItem(View view, int position) {
        MessageViewModel item = dataSet.get(position);
        if(item.isChecked){
            item.isChecked = false;
            view.setActivated(false);
            view.findViewById(R.id.image_view_2).getBackground().setLevel(0);
        } else {
            item.isChecked = true;
            view.setActivated(true);
            view.findViewById(R.id.image_view_2).getBackground().setLevel(1);
        }
        callbacks.onItemSelected(position, item);
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(MessageViewModel message) {
            TextView txtMessage = itemView.findViewById(R.id.text_view_1);
            TextView txtTime = itemView.findViewById(R.id.text_view_2);
            ImageView imgCheck = itemView.findViewById(R.id.image_view_2);
            View layout = itemView.findViewById(R.id.layout);
            txtMessage.setText(message.text);
            txtTime.setText(TimeFormatter.format(message.timeSent));
            if(message.tag != 0){
                // TODO: say if (Math.max(getAuthorName().length, message.tagMessage.length) > message.text.length))
                if(50 > message.text.length()){
                    ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) itemView.findViewById(R.id.frame_layout).getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
            if(!isSelectionMode){
                if(itemView.isSelected()) {
                    TypedValue outValue = new TypedValue();
                    itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    itemView.setBackgroundResource(outValue.resourceId);
                    itemView.setSelected(false);
                    itemView.setActivated(false);
                    if(message.senderId.equals("me")) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                        lp.setMarginStart(callbacks.getDp(40));
                        lp.setMarginEnd(0);
//                      layout.requestLayout();
                    }
                    imgCheck.setVisibility(View.GONE);
                }
            } else {
                if(!itemView.isSelected()) {
                    itemView.setBackgroundResource(R.drawable.selectable_item_background);
                    itemView.setSelected(true);
                    imgCheck.setVisibility(View.VISIBLE);
                    imgCheck.getBackground().setLevel(0);
                    if(message.senderId.equals("me")) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                        lp.setMarginStart(callbacks.getDp(24));
                        lp.setMarginEnd(callbacks.getDp(32));
//                      layout.requestLayout();
                    }
                }
                if(message.isChecked){
                    itemView.setActivated(true);
                    imgCheck.getBackground().setLevel(1);
                } else {
                    itemView.setActivated(false);
                    imgCheck.getBackground().setLevel(0);
                }
            }
            itemView.setOnClickListener(new OnSingleDoubleClickListener() {
                @Override
                protected void onSingleClick(View v) {
                    if(isSelectionMode && getLayoutPosition() != RecyclerView.NO_POSITION){
                        markItem(v, getLayoutPosition());
                    }
                }

                @Override
                protected void onDoubleClick(View v) {
                    if(!isSelectionMode)
                    callbacks.onItemDoubleClicked(v, dataSet.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            return callbacks.onItemLongClick(v, getLayoutPosition());
        }
    }

    public class SentMessageHolder extends ReceivedMessageHolder{

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind(MessageViewModel message) {
            super.bind(message);
            ImageView imgStatus = itemView.findViewById(R.id.image_view);
            switch (message.status) {
                case 0: imgStatus.setImageResource(R.drawable.ic_waiting_24); break;
                case 1: imgStatus.setImageResource(R.drawable.ic_sent_24); break;
                case 2: imgStatus.setImageResource(R.drawable.ic_delivered_24); break;
                case 3: imgStatus.setImageResource(R.drawable.ic_seen_24); break;
            }
        }
    }

    public interface MyCallbacks {
        void onEmptyList(boolean v);
        void onItemDoubleClicked(View v, MessageViewModel message);
        boolean onItemLongClick(View v, int layoutPosition);

        void onItemSelected(int position, MessageViewModel item);

        int getDp(int value);
    }
}
