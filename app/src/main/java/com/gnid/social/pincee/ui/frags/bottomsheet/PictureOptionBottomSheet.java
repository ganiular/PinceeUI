package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;

public class PictureOptionBottomSheet extends Fragment {
    private static final String TAG = PictureOptionBottomSheet.class.getSimpleName();
    private static final long ANIMATION_DURATION = 200;
    static int commitId;
    View bottomSheetContainer;
    private boolean isClosing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewRoot = (ViewGroup) inflater.inflate(R.layout.bottomsheet_picture_option, container, false);
        viewRoot.setOnTouchListener(new DragController(getContext()));
        bottomSheetContainer = viewRoot.findViewById(R.id.bottomsheet_container);
        bottomSheetContainer.setTranslationY(Info.getDisplayHeight(requireContext()) * 0.33f);
        return viewRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetContainer.post(new Runnable() {
            @Override
            public void run() {
                bottomSheetContainer.animate()
                        .translationY(0)
                        .setDuration(ANIMATION_DURATION)
                        .start();
                bottomSheetContainer.removeCallbacks(this);
            }
        });
    }

    void close() {
        if (isClosing) return;
        isClosing = true;
        bottomSheetContainer.animate()
                .alpha(0)
                .translationY(getView().getHeight() * 0.33f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        try {
                            getParentFragmentManager().popBackStack(commitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } catch (IllegalStateException e){
                            Log.e(TAG, "onAnimationEnd: ", e);
                        }
                    }
                })
                .setStartDelay(100);
    }

    public static void open(@IdRes int container, FragmentManager fragmentManager, Bundle args){
        commitId = fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(container, PictureOptionBottomSheet.class, args, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    class DragController implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private static final long ANIMATION_DURATION = 200;
        private final GestureDetectorCompat gestureDetector;
        private boolean isDragging;
        private int innerY;
        private float startY = 0f;
        private boolean slideHandled;
        private float lastScrollDistanceY;
        private final int NO_BLOCK = -1;
        private final int ROOT_BLOCK = 0;
        private final int OTHER_BLOCK = 1;
        private int currentBlock = NO_BLOCK;
        private int otherViewTop;

        DragController(Context context) {
            this.gestureDetector = new GestureDetectorCompat(context, this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent motion) {
            if(motion.getAction() == MotionEvent.ACTION_DOWN) {
                if (v == getView()) {
                    currentBlock = ROOT_BLOCK;
                } else {
                    currentBlock = OTHER_BLOCK;
                    otherViewTop = v.getTop();
                }
            }
            gestureDetector.onTouchEvent(motion);
//            Log.i(TAG, String.format("onTouch: y:%f, Y:%f", motion.getY(), motion.getRawY()));
            switch (motion.getAction()) {
                case MotionEvent.ACTION_MOVE: onMove(getY(motion)); break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: onUp(getY(motion)); break;
            }

            if(currentBlock == ROOT_BLOCK)
                return true;
            return currentBlock == OTHER_BLOCK && isDragging;
        }

        private float getY(MotionEvent e){
            if(currentBlock == ROOT_BLOCK){
                return e.getY();
            } else {
                return e.getRawY();// bottomSheetContainer.getTop() + otherViewTop + e.getY();
            }
        }

        private void slideDown() {
            bottomSheetContainer.animate()
                    .translationY(bottomSheetContainer.getHeight())
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
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

        private void onMove(float y){
            if(!isDragging) return;
            int newY = Math.max(bottomSheetContainer.getTop(), (int) (y - innerY));
            bottomSheetContainer.setTranslationY(newY - bottomSheetContainer.getTop());
        }

        @Override
        public boolean onDown(MotionEvent e) {
            float y = getY(e);
            if(y < bottomSheetContainer.getTop()){
                close();
            } else if (currentBlock == ROOT_BLOCK){
                isDragging = true;
                startY = y;
                innerY = (int) (startY - bottomSheetContainer.getTop());
                slideHandled = false;
                lastScrollDistanceY = 0;
            }
            return false;
        }

        private void onUp(float y) {
            if(!isDragging) return;
            isDragging = false;
            currentBlock = NO_BLOCK;
            if(slideHandled) return;
            if(lastScrollDistanceY < 0)
                slideDown();
            else if(lastScrollDistanceY > 0)
                slideUp();
            else if(Math.abs(startY - y) > 10)
                slideDown();
            else
                slideUp();
        }

        @Override
        public void onShowPress(MotionEvent e) {}

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false; }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            lastScrollDistanceY = distanceY;
            if(!isDragging && currentBlock == OTHER_BLOCK && distanceY < -3){
                isDragging = true;
                startY = e2.getRawY();
                innerY = (int) (startY - bottomSheetContainer.getTop());
                slideHandled = false;
                lastScrollDistanceY = 0;
            }
            Log.i(TAG, "onScroll: y:"+distanceY);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!isDragging) return false;
            if(velocityY > 0){
                slideDown();
            } else {
                slideUp();
            }
            slideHandled = true;
            return true;
        }
    }
}
