package com.gnid.social.pincee.ui.frags.auth;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.AuthenticatorActivity;
import com.gnid.social.pincee.ui.dialog.ErrorDialog;
import com.gnid.social.pincee.ui.dialog.WaitingDialog;

public class SignInFragment extends Fragment {
    private static final String TAG = SignInFragment.class.getSimpleName();
    EditText edtEmail, edtPassword;
    Button btnSignIn;
    private WaitingDialog waitingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        waitingDialog = new WaitingDialog();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        edtEmail = view.findViewById(R.id.edit_email);
        edtPassword = view.findViewById(R.id.edit_password);
        view.findViewById(R.id.text_view_1).setOnClickListener(v -> {
            resetPassword();});
        view.findViewById(R.id.text_view_2).setOnClickListener(v -> {createNewAccount();});
        btnSignIn = view.findViewById(R.id.button);
        btnSignIn.setOnClickListener(v -> validateInputFields());
    }

    private void validateInputFields() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if(email.isEmpty()) {
            edtEmail.setError(getString(R.string.error_email_required));
            return;
        }
        if(password.isEmpty()) {
            edtPassword.setError(getString(R.string.error_password_required));
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        signIn(email, password);
    }

    private void signIn(String email, String password) {
        waitingDialog.show(getChildFragmentManager(), TAG);

        btnSignIn.postDelayed(() -> {
            onSignInResult(email.equals("abc@d.com") && password.equals("123456"));
        }, 2000);
    }

    private void onSignInResult(boolean success) {
        waitingDialog.dismiss();
        if(success){
            ((AuthenticatorActivity) requireActivity()).intentComplete();
        } else {
            new ErrorDialog(getContext(), R.string.error_incorrect_email_or_password);
        }
    }

    private void resetPassword() {
        waitingDialog.show(getChildFragmentManager(), TAG);
        btnSignIn.postDelayed(this::onEmailSent, 2000);
    }

    private void onEmailSent() {
        waitingDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.reset_password)
                .setMessage(R.string.pincee_had_sent_you_an_email)
                .setPositiveButton(R.string.ok, null);
        builder.show();
    }

    private void createNewAccount() {
        String phoneNumber = requireArguments().getString(AuthenticatorActivity.KEY_PHONE_NUMBER);
        Log.i(TAG, "createNewAccount: "+phoneNumber);

        waitingDialog.show(getChildFragmentManager(), TAG);
        btnSignIn.postDelayed(()-> onCreateNewAccount(true), 2000);
    }

    private void onCreateNewAccount(boolean success) {
        waitingDialog.dismiss();
        if(success){
            ((AuthenticatorActivity) requireActivity()).intentComplete();
        } else {
            new ErrorDialog(getContext(), R.string.error_authentication_failed);
        }
    }
}