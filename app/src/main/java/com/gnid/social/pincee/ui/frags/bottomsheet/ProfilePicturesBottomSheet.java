package com.gnid.social.pincee.ui.frags.bottomsheet;

/** REFERENCE
 * I got guide on how to drag view and how to swap recycler view list item.
 * from https://blog.stylingandroid.com/material-part-6/
 *
 * */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfilePicturesBottomSheet extends Fragment {
    public static final String KEY_PICTURES = "pictures";
    private static final String TAG = ProfilePicturesBottomSheet.class.getSimpleName();
    private static final long ANIMATION_DURATION = 300;
    private static int commitId;
    private View bottomSheetContainer;
    private ImageView recyclerItemOverlay;
    private RecyclerView rv;
    private DragController2 dragController2;
    private boolean isClosing;
    private boolean openPictureOptionOnDestroy;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dragController2 = new DragController2(getContext());
        ViewGroup fragmentRoot = (ViewGroup) inflater.inflate(R.layout.bottomsheet_list_profile_pictures, container, false);
        fragmentRoot.setOnTouchListener(dragController2);
        bottomSheetContainer = fragmentRoot.findViewById(R.id.bottomsheet_container);
        bottomSheetContainer.setTranslationY(Info.getDisplayHeight(requireContext()) * 0.33f);
        return fragmentRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerItemOverlay = bottomSheetContainer.findViewById(R.id.overlay);
        rv = bottomSheetContainer.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ProfilePicturesAdapter());
        rv.addOnItemTouchListener(dragController2);

        bottomSheetContainer.findViewById(R.id.add_picture).setOnClickListener(v -> onAddPicture());
        bottomSheetContainer.animate()
                .translationY(0)
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    private void onAddPicture() {
        close();
        openPictureOptionOnDestroy = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(openPictureOptionOnDestroy)
            PictureOptionBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), null);
    }

    private void close() {
        if(isClosing) return;
        isClosing = true;
        bottomSheetContainer.animate()
                .alpha(0)
                .translationY(getView().getHeight() * 0.33f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        getParentFragmentManager().popBackStack(commitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                })
                .start();
    }

    public static void open(@IdRes int container, FragmentManager fragmentManager, Bundle args){
        commitId = fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(container, ProfilePicturesBottomSheet.class, args, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    private class ProfilePicturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final ArrayList<Integer> dataSet = requireArguments().getIntegerArrayList(KEY_PICTURES);
        private AlertDialog dialog;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_profile_picture, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ImageView imageView = holder.itemView.findViewById(R.id.image_view);
            imageView.setImageResource(dataSet.get(position));
//            ((TextView) holder.itemView.findViewById(R.id.text_view)).setText(String.valueOf(position + 1));
            View btnRemove = holder.itemView.findViewById(R.id.button_1);
            btnRemove.setOnClickListener(v -> verifyRemove(holder.getLayoutPosition()));
        }

        public void moveItem(int from, int to){
            Integer temp = dataSet.get(from);
            dataSet.set(from, dataSet.get(to));
            dataSet.set(to, temp);
            notifyItemMoved(from, to);
        }

        private void verifyRemove(int itemPosition){
            if(dialog == null)
                dialog = new AlertDialog.Builder(getContext())
                        .setMessage(R.string.remove_profile_picture)
                        .setPositiveButton(R.string.remove, (dialog1, which) -> removeItem(dialog1, itemPosition))
                        .setNegativeButton(R.string.cancel, ((dialog1, which) -> cancelRemove(dialog1)))
                        .setOnDismissListener(this::cancelRemove)
                        .create();
            dialog.show();
        }

        private void cancelRemove(DialogInterface dialogInterface) {
            dialogInterface.dismiss();
            dialog = null;
        }

        private void removeItem(DialogInterface dialog1, int position) {
            dialog1.dismiss();
            dataSet.remove(position);
            notifyItemRemoved(position);
        }


        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    class DragController2 implements RecyclerView.OnItemTouchListener, View.OnTouchListener, GestureDetector.OnGestureListener {
        private final GestureDetectorCompat gestureDetector;
        private int handlerSide;
        private View draggingView;
        private int bottomBound, topBound = 0;
        private int swapHeight;
        private int itemPosition;
        private final int ROOT_BLOCK = 0;
        private final int RECYCLER_VIEW_BLOCK = 1;
        private final int NO_BLOCK = -1;
        private int currentBlock = NO_BLOCK;
        private boolean isItemDragging;
        private boolean isBottomSheetDragging;
        private static final long ANIMATION_DURATION = 200;
        private float startY;
        private boolean slideHandled;
        private int innerY;

        DragController2(Context context){
            gestureDetector = new GestureDetectorCompat(context, this);
        }

        private void startDragItem(float x, float y){
            Log.i(TAG, "startDragItem: ");
            if (bottomSheetContainer.getTranslationY() > 20) {
                return;
            }
            isItemDragging = true;
//            stopBottomSheetDrag();
            draggingView = rv.findChildViewUnder(x, y);
            int itemHeight = draggingView.getHeight();
            bottomBound = rv.getHeight() - itemHeight;
            swapHeight = (int) (itemHeight * 0.8);
            innerY = (int) (y - draggingView.getTop());
            draggingView.getBackground().setLevel(1);
            paintViewToOverlay(draggingView);
            recyclerItemOverlay.setTranslationY(y - innerY);
            draggingView.setVisibility(View.INVISIBLE);
            itemPosition = rv.indexOfChild(draggingView);
        }

        private void continueItemDrag(int y){
            float newY = Math.max(topBound, y - innerY);
            newY = Math.min(newY, bottomBound);
            recyclerItemOverlay.setTranslationY(newY);
            ProfilePicturesAdapter adapter = (ProfilePicturesAdapter) rv.getAdapter();
            if(newY > draggingView.getTop() && (newY - draggingView.getTop()) >= swapHeight){
                adapter.moveItem(itemPosition, ++itemPosition);
            }else if (newY < draggingView.getTop() && (draggingView.getTop()- newY) >= swapHeight){
                adapter.moveItem(itemPosition, --itemPosition);
            }
        }

        private void endItemDrag(){
            isItemDragging = false;
            currentBlock = NO_BLOCK;
            recyclerItemOverlay.setImageBitmap(null);
            draggingView.setVisibility(View.VISIBLE);
            draggingView.setTranslationY(recyclerItemOverlay.getTranslationY() - draggingView.getTop());
            draggingView.animate().translationY(0f).setDuration(ANIMATION_DURATION).start();
            draggingView.getBackground().setLevel(0);
            draggingView = null;
        }

        private void paintViewToOverlay(View draggingView) {
            Bitmap bitmap = Bitmap.createBitmap(draggingView.getWidth(), draggingView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            draggingView.draw(canvas);
            recyclerItemOverlay.setImageBitmap(bitmap);
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            if(isItemDragging) return true;
            if(handlerSide == 0){
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                if(view != null){
                    handlerSide = view.findViewById(R.id.drag_handle).getRight();
                }
            }
            if(e.getAction() == MotionEvent.ACTION_DOWN && e.getX() < handlerSide){
                startDragItem(e.getX(), e.getY());
            }
            return onTouch(rv, e);
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
             onTouch(rv, e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int id = v.getId();
                if (id == R.id.recyclerview) {
                    currentBlock = RECYCLER_VIEW_BLOCK;
                } else {
                    currentBlock = ROOT_BLOCK;
                }
            }

            if(!(currentBlock == RECYCLER_VIEW_BLOCK && isItemDragging))
                gestureDetector.onTouchEvent(event);
//            Log.i(TAG, String.format("onTouch: \nview:%s, top:%d, bottom:%d\ntouch action:%s, y:%f, rawY:%f",
//                    v.getClass().getSimpleName(), v.getTop(), v.getBottom(),
//                    MotionEvent.actionToString(event.getAction()), event.getY(), event.getRawY()));
            switch (event.getAction()){
                case MotionEvent.ACTION_MOVE: onMove(event); break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: onUp(event); break;
            }
            return currentBlock == ROOT_BLOCK;
        }

        private void slideDown() {
            bottomSheetContainer.animate()
                    .translationY(bottomSheetContainer.getHeight())
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            requireView().animate()
                                    .alpha(0)
                                    .setDuration(100)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            getParentFragmentManager().popBackStack(commitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        }
                                    })
                                    .start();
                        }
                    })
                    .start();
        }

        private void slideUp() {
            bottomSheetContainer.animate()
                    .translationY(0)
                    .setDuration((long) (ANIMATION_DURATION * 0.8f))
                    .start();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if(currentBlock == ROOT_BLOCK && e.getY() < bottomSheetContainer.getTop()){
                close();
            }else if(currentBlock == ROOT_BLOCK){
                startDragBottomSheet(e);
            }
            return false;
        }

        private void onMove(MotionEvent e){
            if(currentBlock == RECYCLER_VIEW_BLOCK && isItemDragging) {
                int y = (int) e.getY();
                continueItemDrag(y);
            }
            else if(isBottomSheetDragging){
                continueBottomSheetDrag(e);
            }
            else if(rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && !rv.canScrollVertically(-1)){
                Log.i(TAG, "onMove: sY:"+rv.getScrollY());
                startDragBottomSheet(e);
            }
        }

        private void onUp(MotionEvent e){
            if (isItemDragging){
                endItemDrag();
            }
            if (isBottomSheetDragging){
                endBottomSheetDrag(e);
            }
        }
        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(currentBlock == RECYCLER_VIEW_BLOCK && !isItemDragging){
                startDragItem(e.getX(), e.getY());
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!isBottomSheetDragging) return false;
            if(velocityY > 0){
                slideDown();
            } else {
                slideUp();
            }
            slideHandled = true;
            return false;
        }

        private void startDragBottomSheet(MotionEvent e){
            isBottomSheetDragging = true;
            startY = e.getRawY();
            innerY = (int) (startY - bottomSheetContainer.getTop());
            slideHandled = false;
        }

        private void endBottomSheetDrag(MotionEvent e){
            isBottomSheetDragging = false;
            if(slideHandled) return;
            else if(Math.abs(startY - e.getRawY()) > 10)
                slideDown();
            else
                slideUp();
        }

        private void continueBottomSheetDrag(MotionEvent e){
            int newY = Math.max(bottomSheetContainer.getTop(), (int)(e.getRawY() - innerY));
            bottomSheetContainer.setTranslationY(newY - bottomSheetContainer.getTop());
        }
    }
}
