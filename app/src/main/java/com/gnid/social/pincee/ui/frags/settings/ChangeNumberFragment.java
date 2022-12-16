package com.gnid.social.pincee.ui.frags.settings;

import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.dialog.WaitingDialog;
import com.gnid.social.pincee.utils.helper.PrefsHelper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class ChangeNumberFragment extends Fragment {
    public static final String TAG = ChangeNumberFragment.class.getSimpleName();
    public static final String REQUEST_KEY = "request_key";
    public static final String RESULT_OK = "result_ok";
    public static final String RESULT_NUMBER_OLD = "old_number";
    public static final String RESULT_NUMBER_NEW = "new_number";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.change_number);
        return inflater.inflate(R.layout.fragment_change_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String userCountryRegion = PrefsHelper.getInstance(getContext()).getCountryRegion();
        int userCountryCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(userCountryRegion);
        EditText edtCodeOld = view.findViewById(R.id.edit_code_1);
        EditText edtCodeNew = view.findViewById(R.id.edit_code_2);
        edtCodeOld.setText(String.valueOf(userCountryCode));
        edtCodeNew.setText(String.valueOf(userCountryCode));

        EditText edtPhoneOld = view.findViewById(R.id.edit_phone_1);
        EditText edtPhoneNew = view.findViewById(R.id.edit_phone_2);

        PhoneNumberFormattingTextWatcher phoneWatcher = new PhoneNumberFormattingTextWatcher();
        edtPhoneOld.addTextChangedListener(phoneWatcher);
        edtPhoneNew.addTextChangedListener(phoneWatcher);

        view.findViewById(R.id. button).setOnClickListener(v -> verifyInputs());
    }

    private void verifyInputs() {
        EditText edtCodeOld = requireView().findViewById(R.id.edit_code_1);
        EditText edtCodeNew = requireView().findViewById(R.id.edit_code_2);
        EditText edtPhoneOld = requireView().findViewById(R.id.edit_phone_1);
        EditText edtPhoneNew = requireView().findViewById(R.id.edit_phone_2);

        try {
            int oldCountryCode = Integer.parseInt(edtCodeOld.getText().toString());
            int newCountryCode = Integer.parseInt(edtCodeNew.getText().toString());

            String oldPhoneNumber = edtPhoneOld.getText().toString().trim();
            String newPhoneNumber = edtPhoneNew.getText().toString().trim();

            if(oldCountryCode * newCountryCode == 0 || oldPhoneNumber.isEmpty() || newPhoneNumber.isEmpty()){
                Toast.makeText(getContext(), R.string.input_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber oldNumber = ph.parse(oldPhoneNumber, ph.getRegionCodeForCountryCode(oldCountryCode));
            Phonenumber.PhoneNumber newNumber = ph.parse(newPhoneNumber, ph.getRegionCodeForCountryCode(newCountryCode));

            String oldE164 = ph.format(oldNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            String newE164 = ph.format(newNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

            onInputValidated(oldE164, newE164);
        } catch (NumberFormatException e){
            Toast.makeText(getContext(), R.string.invalid_country_code_format, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "verifyInputs: ", e);
        } catch (NumberParseException e) {
            Toast.makeText(getContext(), R.string.invalid_phone_number_format, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "verifyInputs: ", e);
        }
    }

    private void onInputValidated(String oldNumber, String newNumber) {
        if(!isRecognised(oldNumber)){
            Toast.makeText(getContext(), R.string.old_number_is_not_recognised, Toast.LENGTH_SHORT).show();
        } else if(isRecognised(newNumber)){
            Toast.makeText(getContext(), R.string.the_number_is_already_reg_by_u, Toast.LENGTH_SHORT).show();
        } else {
            startProgress();

            // send verification sms to newNumber
            Runnable runnable = () -> onTaskCompleted(oldNumber, newNumber);
            Handler handler = new Handler();
            handler.postDelayed(runnable, 2000);
        }
    }

    private boolean isRecognised(String number) {
        return number.equals("+2347030643332") || number.equals("+2349063232430");
    }

    private void onTaskCompleted(String oldNumber, String newNumber) {
        // verification sms sent successful
        stopProgress();
        try {
            PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phone = ph.parse(newNumber, PrefsHelper.getInstance(getContext()).getCountryRegion());
            String newRegion = ph.getRegionCodeForNumber(phone);
            if(newRegion != null){
                PrefsHelper.getInstance(getContext()).setCountryRegion(newRegion);
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        String requestKey = requireArguments().getString(REQUEST_KEY);
        Bundle bundle = new Bundle();
        bundle.putBoolean(RESULT_OK, true);
        bundle.putString(RESULT_NUMBER_OLD, oldNumber);
        bundle.putString(RESULT_NUMBER_NEW, newNumber);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }

    WaitingDialog waitingDialog;
    private void startProgress(){
        if(waitingDialog == null) {
            waitingDialog = new WaitingDialog();
        }
        waitingDialog.show(getParentFragmentManager(), WaitingDialog.TAG);
    }

    private void stopProgress(){
        if(waitingDialog != null){
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }
}
