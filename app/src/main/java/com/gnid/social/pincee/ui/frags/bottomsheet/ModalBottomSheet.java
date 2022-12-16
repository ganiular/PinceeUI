package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.frags.settings.ProfileSettingsFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = ModalBottomSheet.class.getSimpleName();
    private RecyclerViewDragHandler dragHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CoordinatorLayout view = (CoordinatorLayout) inflater.inflate(R.layout.fragment_bottomsheet_container, container, false);
        LinearLayout bottomSheetContainer = view.findViewById(R.id.bottomsheet_container);
        ImageView overlay = view.findViewById(R.id.overlay);
        RecyclerView rv = bottomSheetContainer.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ProfilePicturesAdapter());

        dragHandler = new RecyclerViewDragHandler(rv);

        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(bottomSheetContainer);
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.i(TAG, "onStateChanged: newState:"+newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i(TAG, "onSlide: offset:"+slideOffset);
            }
        };
        behavior.addBottomSheetCallback(bottomSheetCallback);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSaveFlags(BottomSheetBehavior.SAVE_NONE);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ProfilePicturesAdapter extends RecyclerView.Adapter {
        final ArrayList<Integer> dataSet = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_profile_picture, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView.findViewById(R.id.text_view)).setText(String.valueOf(dataSet.get(position)));
            View dragHandle = holder.itemView.findViewById(R.id.drag_handle);
//            dragHandle.setOnTouchListener(dragHandler::onDragHandleTouched);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    static class RecyclerViewDragHandler {
        private final RecyclerView rv;
        private ViewGroup itemView;

        RecyclerViewDragHandler(RecyclerView rv){
            this.rv = rv;
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        public boolean onDragHandleTouched(View dragHandle, MotionEvent motionEvent) {
            Log.i(TAG, "onDragHandleTouched: "+motionEvent);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN: return startDrag(dragHandle);
                case MotionEvent.ACTION_MOVE: return drag(motionEvent.getY());
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: return dropDrag();
            }
            return false;
        }

        private boolean startDrag(View dragHandle) {
            itemView = (ViewGroup) dragHandle.getParent();
            itemView.getBackground().setLevel(1);
            rv.setNestedScrollingEnabled(true);
            rv.requestLayout();
            rv.suppressLayout(true);
            dragHandle.performClick();
            return true;
        }

        private boolean drag(float y) {
            itemView.setTranslationY(-50f);
            return true;
        }

        private boolean dropDrag() {
            itemView.getBackground().setLevel(0);
            rv.setNestedScrollingEnabled(false);
            rv.requestLayout();
            rv.suppressLayout(false);
            itemView = null;
            return true;
        }
    }
}
