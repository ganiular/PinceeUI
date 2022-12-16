package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;

public class NumberOptionBottomSheet extends PictureOptionBottomSheet{

    public static final String REQUEST_KEY = "request_key";
    public static final String PUSHBACK_DATA = "pushback_data";
    public static final String INCLUDE_DELETE = "include_delete";
    private static final String TAG = NumberOptionBottomSheet.class.getSimpleName();
    public static final int CHANGE = 1;
    public static final int DELETE = 2;
    public static final int COPY = 3;
    public static final String RESULT_OK = "result_ok";
    public static final String RESULT_OPTION = "result_option";

    public static void open(@IdRes int container, FragmentManager fragmentManager, Bundle args){
        commitId = fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(container, NumberOptionBottomSheet.class, args, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewRoot = (ViewGroup) inflater.inflate(R.layout.bottomsheet_number_option, container, false);
        viewRoot.setOnTouchListener(new DragController(getContext()));
        bottomSheetContainer = viewRoot.findViewById(R.id.bottomsheet_container);
        bottomSheetContainer.setTranslationY(Info.getDisplayHeight(requireContext()) * 0.33f);
        return viewRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.item_1).setOnClickListener(v -> submitOption(CHANGE));
        if(requireArguments().getBoolean(INCLUDE_DELETE, false)){
            view.findViewById(R.id.item_2).setOnClickListener(v -> submitOption(DELETE));
        } else {
            view.findViewById(R.id.item_2).setVisibility(View.GONE);
        }
        view.findViewById(R.id.item_3).setOnClickListener(v -> submitOption(COPY));
    }

    private Bundle result;
    private void submitOption(int option) {
        result = new Bundle();
        result.putBoolean(RESULT_OK, true);
        result.putInt(RESULT_OPTION, option);
        result.putInt(PUSHBACK_DATA, requireArguments().getInt(PUSHBACK_DATA));
        close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(result != null){
            String requestKey = requireArguments().getString(REQUEST_KEY);
            getParentFragmentManager().setFragmentResult(requestKey, result);
        }
    }
}
