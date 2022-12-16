package com.gnid.social.pincee.utils.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.db.model.ChatViewModel;
import com.gnid.social.pincee.utils.helper.TimeFormatter;

import java.util.ArrayList;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> implements ChatViewModel.Callback {
    private final String TAG = ChatListAdapter.class.getSimpleName();
    final private ArrayList<ChatViewModel> dataSet;
    final private LayoutInflater inflater;
    private final Callbacks listener;

    public boolean isSelectionMode = false;
    private final int CAPACITY_MULTIPLIER = 50;

    public ChatListAdapter(Context context, Callbacks listener){
        this.dataSet = new ArrayList<>(CAPACITY_MULTIPLIER);
        inflater = LayoutInflater.from(context);
        setHasStableIds(true);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(dataSet.get(position));
    }

    public void removeItem(ChatViewModel item){
        int position = dataSet.indexOf(item);
        dataSet.remove(position);
        notifyItemRemoved(position);
    }
    public void removeMark(ChatViewModel item) {
        int position = dataSet.indexOf(item);
        notifyItemChanged(position);
    }

    public void markItem(View view, int selectedPosition){
        assert isSelectionMode;
        view.setBackgroundResource(R.drawable.selectable_item_background);
        ChatViewModel item = dataSet.get(selectedPosition);
        if(item.isChecked){
            item.isChecked = false;
            view.setActivated(false);
            view.findViewById(R.id.image_view_2).setVisibility(View.GONE);
        } else {
            item.isChecked = true;
            view.setActivated(true);
            view.findViewById(R.id.image_view_2).setVisibility(View.VISIBLE);
        }
        listener.onChatItemSelected(selectedPosition, item);
    }

    @Override
    public int getItemCount() {
        int size = dataSet.size();
        listener.onEmptyList(size == 0);
        return size;
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).rowId;
    }

    @Override
    public void onItemCreated(Object item) {
        dataSet.add((ChatViewModel) item);
        notifyItemInserted(0);
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ChatViewModel chatViewModel) {
            // TODO: If there is a place holder before this binding of data occur
            //  i should invisible the place holder from here
            TextView txtUsername = itemView.findViewById(R.id.text_view_1);
            TextView txtMessage = itemView.findViewById(R.id.text_view_2);
            ImageView imgProfile = itemView.findViewById(R.id.image_view);
            ImageView imgChecked = itemView.findViewById(R.id.image_view_2);
            TextView txtTime = itemView.findViewById(R.id.text_view_3);
            TextView txtCount = itemView.findViewById(R.id.text_view_4);
            txtUsername.setText(chatViewModel.userName);
            txtMessage.setText(chatViewModel.lastMessage);
            txtTime.setText(TimeFormatter.format(chatViewModel.timestamp));
            imgProfile.setImageURI(chatViewModel.photoUrl);
            txtCount.setText(String.valueOf(chatViewModel.noticeCount));
            if(!isSelectionMode){
                if(itemView.isActivated()){
                    TypedValue outValue = new TypedValue();
                    itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    itemView.setBackgroundResource(outValue.resourceId);
                    imgChecked.setVisibility(View.GONE);
                    itemView.setActivated(false);
                }
                if(chatViewModel.isChecked) {
                    chatViewModel.isChecked = false;
                }
            } else {
                itemView.setBackgroundResource(R.drawable.selectable_item_background);
                itemView.setActivated(chatViewModel.isChecked);
                imgChecked.setVisibility(chatViewModel.isChecked? View.VISIBLE: View.GONE);
            }

//            itemView.setBackgroundColor(chatViewModel.isChecked? Color.GRAY: Color.WHITE);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onChatItemClick(v, getItemId(), getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return listener.onChatItemLongClick(v, getLayoutPosition());
        }
    }

    public interface Callbacks {
        void onEmptyList(boolean v);

        void onChatItemClick(View v, long rowId, int position);

        boolean onChatItemLongClick(View v, int layoutPosition);

        void onChatItemSelected(int selectedPosition, ChatViewModel isChecked);
    }
}
