package com.gnid.social.pincee.ui.frags.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gnid.social.pinceeui.R;

public class NumberChangeOrAdditionNoticeFragment extends Fragment {
    public static final String TAG = NumberChangeOrAdditionNoticeFragment.class.getSimpleName();
    public static final String RESULT_OK = "result_ok";
    public static final String REQUEST_KEY = "request_key";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_number_modification_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.button).setOnClickListener(this::onNext);
    }

    private void onNext(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(RESULT_OK, true);
        String requestKey = requireArguments().getString(REQUEST_KEY);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
