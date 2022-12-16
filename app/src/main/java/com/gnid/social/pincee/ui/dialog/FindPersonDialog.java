package com.gnid.social.pincee.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class FindPersonDialog extends DialogFragment{
    private static final String TAG = FindPersonDialog.class.getSimpleName();
    ProgressBar progressBar;
    EditText edtPhone;
    TextView txtError, txtResponseMessage;
    private Button btnOk;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialog_input_find_person, null);
        progressBar = root.findViewById(R.id.progress_circular);
        edtPhone = root.findViewById(R.id.edit_phone);
        txtError = root.findViewById(R.id.text_view_1);
        txtResponseMessage = root.findViewById(R.id.text_view_2);
        btnOk = root.findViewById(R.id.button_1);
        btnOk.setOnClickListener(v -> submit());
        root.findViewById(R.id.button_2).setOnClickListener(v -> cancel());
        edtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String query = v.getText().toString().trim();
                    if(query.length() > 0) {
                        validateInput(query);
                        return true;
                    }
                }
                return false;
            }
        });

        Dialog dialog = new AlertDialog.Builder(requireActivity()).setView(root).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void submit() {
        String query = edtPhone.getText().toString().trim();
        if(query.length() > 0){
            validateInput(query);
        } else {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(R.string.error_required);
            txtResponseMessage.setVisibility(View.GONE);
        }
    }

    private void validateInput(String query) {
        txtError.setVisibility(View.INVISIBLE);
        txtResponseMessage.setVisibility(View.GONE);
        PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
        String e164number;
        try {
            Phonenumber.PhoneNumber phoneNumber = ph.parse(query, Info.getCountryRegion(getContext()));
            e164number = ph.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            onSubmit(e164number);
        } catch (NumberParseException e) {
            Log.i(TAG, "validateInput: "+e);
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(R.string.error_invalid_input);
        }
    }

    private void cancel() {
        getDialog().cancel();
    }

    /** User submit search query */
    public void onSubmit(String query) {
        Log.i(TAG, "onSubmit: "+query);
        // TODO: invoke onProcess, search query from local then on server, report response onSuccess of onFailure
        startProgress();
        if (query.equals("+234123456")) {
            progressBar.postDelayed(() -> onSuccess("xxx"), 2000);
        } else if(query.equals("+234000000")) {
            progressBar.postDelayed(() -> onFailure(new Exception("Network Issue")), 2000);
        } else {
            progressBar.postDelayed(() -> onSuccess(null), 2000);
        }
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        edtPhone.setEnabled(false);
        btnOk.setEnabled(false);
    }

    private void stopProgress(){
        progressBar.setVisibility(View.GONE);
        edtPhone.setEnabled(true);
        btnOk.setEnabled(true);
    }

    /**
     * Response from the server
     */
    public void onSuccess(String userId) {
        stopProgress();
        // TODO: open users profile page.
        if(userId != null) {
            dismiss();
            Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
        } else {
            txtResponseMessage.setVisibility(View.VISIBLE);
            txtResponseMessage.setText(R.string.user_not_found);
        }
    }

    /**
     * Response from the server
     */
    public void onFailure(Exception e) {
        stopProgress();
        txtResponseMessage.setVisibility(View.VISIBLE);
        txtResponseMessage.setText(R.string.error_network_issue);
    }

    /**
     * User or system canceled the process
     */
    @Override
    public void onCancel(DialogInterface dialog) {

    }
}