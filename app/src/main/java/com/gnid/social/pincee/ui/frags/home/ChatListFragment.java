package com.gnid.social.pincee.ui.frags.home;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.MainActivity;
import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.db.UserDB;
import com.gnid.social.pincee.db.model.ChatViewModel;
import com.gnid.social.pincee.ui.ChatActivity;
import com.gnid.social.pincee.ui.dialog.ChatListDeleteDialog;
import com.gnid.social.pincee.ui.dialog.FindPersonDialog;
import com.gnid.social.pincee.ui.frags.bottomsheet.GridItemBottomSheet;
import com.gnid.social.pincee.utils.adapters.ChatListAdapter;
import com.gnid.social.pincee.utils.system.service.MyKeyboardManager;

import java.util.LinkedList;

public class ChatListFragment extends Fragment implements ChatListAdapter.Callbacks, ActionMode.Callback, GridItemBottomSheet.CallBacks {
    private int whichActionMode;
    private static final int CHAT_ITEM_ACTION_MODE = 1;
    private static final int SEARCH_ACTION_MODE = 2;
    private final String TAG = ChatListFragment.class.getSimpleName();
    private RecyclerView rv;
    private ChatListAdapter adapter;
    private boolean isEmptyVisible = false;
    private LinkedList<ChatViewModel> selectedItems;
    private ActionMode chatItemActionMode;
    private ActionMode searchActionMode;

    private void deleteChatItems(boolean includeMedia) {
        int size = selectedItems.size();
        for (int i = size-1; i >= 0 ; i--) {
            adapter.removeItem(selectedItems.removeFirst());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new ChatListAdapter(getContext(), this);
        UserDB.queryChats(adapter);

        rv = view.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//            SeparatorDecoration decoration = new SeparatorDecoration(getContext(), Color.GRAY, 1.5f);
//            rv.addItemDecoration(decoration);
        rv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chat_fragment_action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search: popupSearchOption();
            return true;
            case R.id.action_files:
                String[] mediaNames = getResources().getStringArray(R.array.media_files);
                int[] mediaIcons = {
                        R.drawable.ic_outline_image_24,
                        R.drawable.ic_baseline_local_movies_24,
                        R.drawable.ic_baseline_headset_24,
                        R.drawable.ic_outline_other_file_24,
                        R.drawable.ic_outline_snippet_folder_24,
                };
                GridItemBottomSheet bottomSheet = new GridItemBottomSheet(R.string.media_files, mediaNames, mediaIcons, 3, this);
                bottomSheet.show(getChildFragmentManager(), GridItemBottomSheet.TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void popupSearchOption() {
        View anchor = requireActivity().findViewById(R.id.action_search);
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.main_search_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()){
                case R.id.action_search_person:
                    showFindPersonDialog();
                    return true;
                case R.id.action_search_message:
                    return actionHandled();
            }
            return true;
        });
        popup.show();
    }

    private FindPersonDialog dialog;
    private void showFindPersonDialog() {
        if(dialog == null)
            dialog = new FindPersonDialog();
        dialog.show(getChildFragmentManager().beginTransaction(), "lookup");
    }

    private boolean actionHandled() {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        whichActionMode = SEARCH_ACTION_MODE;
        searchActionMode = requireActivity().startActionMode(this);
        return true;
    }

    @Override
    public void onEmptyList(boolean v){
        if(v && !isEmptyVisible) {
            isEmptyVisible = true;
            getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }
        else if(isEmptyVisible){
            isEmptyVisible = false;
            getView().findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    @Override
    public void onChatItemClick(View v, long userId, int position) {
        if(adapter.isSelectionMode){
            if(position == RecyclerView.NO_POSITION) return;
            adapter.markItem(v, position);
        } else {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_RECEIVER_ID, userId);
            intent.putExtra("REFERRER", MainActivity.class.getName());

            // make transition to ChatActivity
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    // for profile image
                    new Pair<>(v.findViewById(R.id.image_view), ChatActivity.VIEW_NAME_PROFILE_IMAGE),
                    // for username
                    new Pair<>(v.findViewById(R.id.text_view_1), ChatActivity.VIEW_NAME_USERNAME)
            );
            startActivity(intent, activityOptions.toBundle());
        }
    }

    @Override
    public boolean onChatItemLongClick(View v, int layoutPosition) {
        if(adapter.isSelectionMode) return false;

        whichActionMode = CHAT_ITEM_ACTION_MODE;
        chatItemActionMode = requireActivity().startActionMode(this);
        adapter.isSelectionMode = true;
        adapter.markItem(v, layoutPosition);
        return true;
    }

    @Override
    public void onChatItemSelected(int selectedPosition, ChatViewModel item) {
        if(item.isChecked){
            if(selectedItems == null) selectedItems = new LinkedList<>();
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }
        if(selectedItems.size() == 0){
            onDestroyActionMode(chatItemActionMode);
        } else {
            chatItemActionMode.setTitle(String.valueOf(selectedItems.size()));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        switch (whichActionMode){
            case CHAT_ITEM_ACTION_MODE: mode.getMenuInflater().inflate(R.menu.chat_list_item_context, menu);
            return true;
            case SEARCH_ACTION_MODE:
                View v = getLayoutInflater().inflate(R.layout.action_mode_search_bar, null);
                mode.setCustomView(v);

                EditText editText = v.findViewById(R.id.edit_search);
                editText.setOnEditorActionListener((v1, actionId, event) -> {
                    if(actionId == EditorInfo.IME_ACTION_SEARCH){
                        String query = v1.getText().toString().trim();
                        if(query.length() > 0) {
                            onSubmitSearchQuery(query);
                            mode.finish();
                            return true;
                        }
                    }
                    return false;
                });
                MyKeyboardManager.requestKeyboardDelay(getContext(), editText, 300);
                return true;
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(item.getItemId() == R.id.action_delete){
            item.setEnabled(false);
            new ChatListDeleteDialog(getActivity(), selectedItems.size()) {
                @Override
                protected void onCanceled(DialogInterface dialog) {
                    item.setEnabled(true);
                }

                @Override
                public void onItemClick(DialogInterface dialog, int which) {
                    if(which == DELETE_WITH_MEDIA){
                        deleteChatItems(true);
                        dialog.dismiss();
                        mode.finish();
                    }else if(which == DELETE_WITHOUT_MEDIA){
                        deleteChatItems(false);
                        dialog.dismiss();
                        mode.finish();
                    } else {
                        dialog.cancel();
                    }
                }
            };
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if(whichActionMode == CHAT_ITEM_ACTION_MODE) {
            adapter.isSelectionMode = false;
            int size = selectedItems.size();
            for (int i = 0; i < size; i++) {
                adapter.removeMark(selectedItems.removeFirst());
            }
            mode.finish();
            selectedItems = null;
            chatItemActionMode = null;
        } else if (whichActionMode == SEARCH_ACTION_MODE){
            mode.finish();
            searchActionMode = null;
        }
    }

    private void onSubmitSearchQuery(String query){
        Toast.makeText(getContext(), "search for: "+query, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        if(chatItemActionMode != null){
            onDestroyActionMode(chatItemActionMode);
        }
        if(searchActionMode != null){
            onDestroyActionMode(searchActionMode);
        }
    }

    @Override
    public void onBottomSheetItemClicked(View view, int position) {
        Log.i(TAG, "onBottomSheetItemClicked: position:"+position);
    }
}