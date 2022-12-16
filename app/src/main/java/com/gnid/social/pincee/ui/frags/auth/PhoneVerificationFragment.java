package com.gnid.social.pincee.ui.frags.auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.AuthenticatorActivity;
import com.gnid.social.pincee.ui.dialog.ErrorDialog;
import com.gnid.social.pincee.ui.dialog.WaitingDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneVerificationFragment extends Fragment {
    public static final String REQUEST_KEY = "request_key";
    public static final String TAG = PhoneVerificationFragment.class.getSimpleName();
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_OTHER_NUMBER = "other_number";
    public static final String ACTION = "action";
    public static final String ACTION_CHANGE_NUMBER = "change_number";
    public static final String ACTION_ADD_NUMBER = "add_number";
    private static final String KEY_COUNT_DOWN_TIME = "count_down_time";
    private static final int COUNT_DOWN_START = 20;
    public static final String RESULT_OK = "result_ok";
    private TextView txtTimeDown, txtOr;
    private CountDownRunner countDownRunner;
    private Button btnResend, btnCallMe;
    private EditText edtCode;
    private WaitingDialog waitingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        waitingDialog = new WaitingDialog();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        String phone = args.getString(KEY_PHONE_NUMBER);
        String t = getResources().getString(R.string.the_6_digit_code, phone);
        TextView txtNumLabel = view.findViewById(R.id.text_view_1);
        txtNumLabel.setText(t);

        int from = COUNT_DOWN_START;
        txtTimeDown = view.findViewById(R.id.text_view_2);
        if(savedInstanceState != null){
            from = savedInstanceState.getInt(KEY_COUNT_DOWN_TIME);
        }
        startCountDown(from);
        
        btnResend = view.findViewById(R.id.button1);
        btnCallMe = view.findViewById(R.id.button2);
        txtOr = view.findViewById(R.id.text_view_3);
        btnResend.setOnClickListener(v -> {resendSms(phone);});
        btnCallMe.setOnClickListener(v -> {callClient(phone);});
        
        edtCode = view.findViewById(R.id.edit_number);
        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String code = s.toString().trim();
                if(code.length() == 6){
                    submitCode(code);
                }
            }
        });
    }

    private void submitCode(String code) {
        hideKeyboard();
        waitingDialog.startProgress(getChildFragmentManager(), TAG, R.string.verifying_phone);
        JSONObject result = new JSONObject();
        try {
            Bundle args = requireArguments();
            String phone = args.getString(AuthenticatorActivity.KEY_PHONE_NUMBER);
            result.put("success", code.equals("123456"));
            result.put("formatted_phone_number", phone);
            result.put("client_id", "xxxxxxxxx");
            result.put("token", "xxxxxxxxxxxxxxxxxxxxxxx");
            result.put("message", phone.equals("+234123456")? "import account": "create new");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Runnable runnable = () -> onTaskCompleted(result);
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }

    private void onTaskCompleted(JSONObject result) {
        try {
            boolean success = result.getBoolean("success");
            String message = result.getString("message");
            if(success){
                // Return result to caller activity. NumberModificationActivity
                Bundle args = getArguments();
                String action = args != null ? args.getString(ACTION) : null;
                if(action != null && action.equals(ACTION_CHANGE_NUMBER)){
                    String newNumber = args.getString(KEY_PHONE_NUMBER);
                    String oldNumber = args.getString(KEY_OTHER_NUMBER);
                    changePhoneNumber(newNumber, oldNumber);
                    waitingDialog.dismiss();
                    return;
                } else if (action != null && action.equals(ACTION_ADD_NUMBER)) {
                    String newNumber = args.getString(KEY_PHONE_NUMBER);
                    addPhoneNumber(newNumber);
                    waitingDialog.dismiss();
                    return;
                }


                Toast.makeText(getContext(), "Verification success", Toast.LENGTH_SHORT).show();
                switch (message){
                    case "create new":
                        createNewAccount(result.getString("formatted_phone_number"));
                        break;
                    case "sign in":
                        autoSignInAccount(result.getString("client_id"), result.getString("token"));
                        break;
                    case "import account":
                        ((AuthenticatorActivity) requireActivity()).gotoSignInFragment(result.getString("formatted_phone_number"));
                        break;
                }
            }else {
                waitingDialog.dismiss();
                new ErrorDialog(getActivity(), R.string.error_wrong_verification_code);
            }
        } catch (JSONException e) {
            waitingDialog.dismiss();
            Toast.makeText(getContext(), "internal error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addPhoneNumber(String newNumber) {
        Toast.makeText(getContext(), "Number added", Toast.LENGTH_SHORT).show();
        String requestKey = requireArguments().getString(REQUEST_KEY);
        if(requestKey != null){
            Bundle bundle = new Bundle();
            bundle.putBoolean(RESULT_OK, true);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
        }
    }

    private void changePhoneNumber(String newNumber, String oldNumber) {
        Toast.makeText(getContext(), "Number changed", Toast.LENGTH_SHORT).show();
        String requestKey = requireArguments().getString(REQUEST_KEY);
        if(requestKey != null){
            Bundle bundle = new Bundle();
            bundle.putBoolean(RESULT_OK, true);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
        }
    }

    private void createNewAccount(String phoneNumber) {
        Log.i(TAG, "createNewAccount: "+phoneNumber);
        onAuthentication(true);
    }

    private void onAuthentication(boolean success) {
        waitingDialog.dismiss();
        if(success){
            ((AuthenticatorActivity) requireActivity()).intentComplete();
        } else {
            new ErrorDialog(getContext(), R.string.error_authentication_failed);
        }
    }

    private void autoSignInAccount(String clientId, String token) {
        Log.i(TAG, "autoSignInAccount: ");
        onAuthentication(true);
    }

//    private void requestImportOrCreate(String phoneNumber) {
//        Log.i(TAG, "requestImportOrCreate: "+phoneNumber);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity())
//                .setMessage(R.string.request_import)
//                .setPositiveButton("Sign in", (dialog, which) -> ((AuthenticatorActivity) requireActivity()).gotoSignInFragment(phoneNumber))
//                .setNegativeButton("Create new", (dialog, which) -> createNewAccount(phoneNumber));
//        alertDialog.show();
//    }

//    public WaitingDialog startProgress() {
//        if(waitingDialog == null) waitingDialog = new WaitingDialog();
//        waitingDialog.show(getChildFragmentManager(), TAG);
//        return waitingDialog;
//    }

//    public void stopProgress() {
//        if(waitingDialog != null){
//            waitingDialog.dismiss();
//        }
//    }

    private void callClient(String phone) {
        waitingDialog.startProgress(getChildFragmentManager(), TAG, R.string.waiting);
        Runnable runnable = () -> onCallRequestCompleted(phone);
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }

    private void onCallRequestCompleted(String phone) {
        waitingDialog.dismiss();
        setViewsEnable(false);
        startCountDown(COUNT_DOWN_START);
        Log.i(TAG, "onCallRequestCompleted: phone:"+phone);
    }

    private void resendSms(String phone) {
        waitingDialog.startProgress(getChildFragmentManager(), TAG, R.string.waiting);
        Runnable runnable = () -> onSmsRequestCompleted(phone);
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }

    private void onSmsRequestCompleted(String phone) {
        waitingDialog.dismiss();
        setViewsEnable(false);
        startCountDown(COUNT_DOWN_START);
        Log.i(TAG, "onSmsRequestCompleted: phone:"+phone);
    }


    private void startCountDown(int from){
        countDownRunner = new CountDownRunner(txtTimeDown,
                getResources().getString(R.string.no_code_received_try_again),
                from,
                ()->setViewsEnable(true));
        countDownRunner.run();
    }

    private void setViewsEnable(boolean enabled) {
        btnCallMe.setEnabled(enabled);
        btnCallMe.setAlpha(enabled? 1f: 0.3f);
        btnResend.setEnabled(enabled);
        btnResend.setAlpha(enabled? 1f: 0.3f);
        txtOr.setEnabled(enabled);
    }

    @Override
    public void onDestroyView() {
        if(countDownRunner != null){
            txtTimeDown.removeCallbacks(countDownRunner);
            countDownRunner = null;
        }
        super.onDestroyView();
    }

    private boolean hideKeyboard(){
        if(edtCode.isFocused()){
            edtCode.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtCode.getWindowToken(), 0);
        }
        return false;
    }

    static class CountDownRunner implements Runnable {
        private final String m;
        private final TextView v;
        private final OnCompletedCallback callback;
        private int t;

        CountDownRunner(TextView textView, String message, int time, OnCompletedCallback callback){
            this.v = textView;
            this.m = message;
            this.t = time;
            this.callback = callback;
        }

        @Override
        public void run() {
            v.setText(String.format(m, --t));
            if(t > 0) {
                v.postDelayed(this, 1000);
            }else {
                v.removeCallbacks(this);
                callback.onCompleted();
            }
        }
    }

    interface OnCompletedCallback{
        void onCompleted();
    }
}