package com.gnid.social.pincee.ui.frags.bottom.sheet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gnid.social.pinceeui.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ItemListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";

    // TODO: Customize parameters
    public static ItemListDialogFragment newInstance(int itemCount) {
        final ItemListDialogFragment fragment = new ItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new ItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;

        ItemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(
                    getLayoutInflater().inflate(R.layout.fragment_item_list_dialog_list_dialog_item2, parent, false)
            );

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }
}