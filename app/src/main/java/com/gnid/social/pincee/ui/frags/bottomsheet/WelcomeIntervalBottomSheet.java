package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;

public class WelcomeIntervalBottomSheet extends PictureOptionBottomSheet{
    static final String TAG = WelcomeIntervalBottomSheet.class.getSimpleName();
    public static final String INITIAL_VALUE = "initial_value";
    public static final String RESULT_DATA_INDEX = "result_data_index";
    public static final String RESULT_OK = "result_ok";
    public static final String REQUEST_KEY = "request_key";

    @IdRes
    private final int[] options = {
            R.id.first_time,
            R.id.daily,
            R.id.weekly,
            R.id.monthly
    };
    private DragController dragController;

    public static void open(@IdRes int container, FragmentManager fragmentManager, Bundle args){
        commitId = fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(container, WelcomeIntervalBottomSheet.class, args, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_interval_option, container, false);
        dragController = new DragController(getContext());
        view.setOnTouchListener(dragController);
        bottomSheetContainer = view.findViewById(R.id.bottomsheet_container);
        bottomSheetContainer.setTranslationY(Info.getDisplayHeight(requireContext()) * 0.33f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        int initialValue = requireArguments().getInt(INITIAL_VALUE, 0);

        radioGroup.check(options[initialValue]);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.first_time:
                        submitOption(0);
                        break;
                    case R.id.daily:
                        submitOption(1);
                        break;
                    case R.id.weekly:
                        submitOption(2);
                        break;
                    case R.id.monthly:
                        submitOption(3);
                        break;
                }
            }
        });

        for(int i=0; i<radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean action = dragController.onTouch(v, event);
                    if(action){
                        event.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    return false;
                }
            });
        }
        view.findViewById(R.id.button).setOnClickListener(v -> close());
    }

    Bundle response;
    private void submitOption(int selectedIndex){
        response = new Bundle();
        response.putBoolean(RESULT_OK, true);
        response.putInt(RESULT_DATA_INDEX, selectedIndex);
        close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(response != null){
            String requestKey = requireArguments().getString(REQUEST_KEY);
            getParentFragmentManager().setFragmentResult(requestKey, response);
        }
    }
}
