package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GridItemBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = GridItemBottomSheet.class.getSimpleName();
    private final CallBacks mListener;
    private final int mSpanCount;
    private final ItemAdapter mAdapter;
    private final int mTitle;

    public GridItemBottomSheet(int title, String[] names, int[] iconRes, int spanCount, CallBacks listener){
        this.mListener = listener;
        this.mSpanCount = spanCount;
        this.mTitle = title;
        this.mAdapter = new ItemAdapter(names, iconRes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        rv.setAdapter(mAdapter);
        final TextView tv = view.findViewById(R.id.text_view);
        tv.setText(mTitle);
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(v -> mListener.onBottomSheetItemClicked(v, getLayoutPosition()));
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {
        DataModel[] dataSet;

        public ItemAdapter(String[] text, int[] imageRes) {
            this.dataSet = new DataModel[text.length];
            for (int i = 0; i < dataSet.length; i++) {
                dataSet[i] = new DataModel(text[i], imageRes[i]);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    getLayoutInflater().inflate(R.layout.item_bottom_sheet_grid, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(dataSet[position].text);
            holder.imageView.setImageResource(dataSet[position].image);
        }

        @Override
        public int getItemCount() {
            return dataSet.length;
        }
    }

    public interface CallBacks{
        void onBottomSheetItemClicked(View view, int position);
    }

    class DataModel{
        final String text;
        final int image;
        DataModel(String t, int i){
            text = t; image = i;
        }
    }
}
