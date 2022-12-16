package com.gnid.social.pincee.ui.frags.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;
import com.gnid.social.pincee.utils.system.service.MyKeyboardManager;

public class EditTextBottomSheet extends PictureOptionBottomSheet {
    public static final String MAX_LENGTH = "maxLength";
    public static final String SINGLE_LINE = "singleLine";
    public static final String RESULT_OK = "ok";
    public static final String RESULT_DATA = "result";
    public static final String REQUEST_KEY = "request_key";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String TEXT = "text";
    public static final String REQUIRE_MESSAGE = "require_message";
    static final String TAG = EditTextBottomSheet.class.getSimpleName();
    private EditText editText;


    public static void open(@IdRes int container, FragmentManager fragmentManager, Bundle args){
        commitId = fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(container, EditTextBottomSheet.class, args, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_edit_text, container, false);
        view.setOnTouchListener(new DragController(getContext()));
        bottomSheetContainer = view.findViewById(R.id.bottomsheet_container);
        bottomSheetContainer.setTranslationY(Info.getDisplayHeight(requireContext()) * 0.33f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.text_view_1);
        textView.setText(requireArguments().getString(TITLE));
        textView = view.findViewById(R.id.text_view_2);
        String description = requireArguments().getString(DESCRIPTION, null);
        if(description == null){
            textView.setVisibility(View.GONE);
        }else{
            textView.setText(description);
        }
        int maxLength = requireArguments().getInt(MAX_LENGTH);
        boolean singleLine = requireArguments().getBoolean(SINGLE_LINE);
        CharSequence text = requireArguments().getCharSequence(TEXT, null);
        TextView txtCounter = view.findViewById(R.id.text_view_3);
        txtCounter.setText(String.valueOf(maxLength));

        editText = view.findViewById(R.id.edit_text);
        editText.setText(text);
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxLength)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                txtCounter.setText(String.valueOf(Math.max(0, maxLength - s.length())));
            }
        });
        if(singleLine)
            editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);

        view.findViewById(R.id.button_1).setOnClickListener(v -> submitText());
        view.findViewById(R.id.button_2).setOnClickListener(v -> close());
        MyKeyboardManager.requestKeyboard(getContext(), editText);
    }

    Bundle response;
    private void submitText(){
        String text = editText.getText().toString().trim();
        String requireMessage = requireArguments().getString(REQUIRE_MESSAGE, null);
        if(requireMessage != null && (text.length() == 0 || text.isEmpty())){
            Toast.makeText(getContext(), requireMessage, Toast.LENGTH_SHORT).show();
        } else {
            response = new Bundle();
            response.putCharSequence(RESULT_DATA, text);
            response.putBoolean(RESULT_OK, true);
            close();
        }
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
