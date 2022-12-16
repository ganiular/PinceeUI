package com.gnid.social.pincee.ui.frags.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gnid.social.pincee.ui.AuthenticatorActivity;
import com.gnid.social.pincee.ui.dialog.ErrorDialog;
import com.gnid.social.pincee.ui.dialog.WaitingDialog;
import com.gnid.social.pincee.utils.adapters.PhoneCountriesAdapter;
import com.gnid.social.pincee.utils.helper.PrefsHelper;
import com.gnid.social.pinceeui.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final String TAG = PhoneNumberFragment.class.getSimpleName();
    private EditText edtPhone;
    private TextView txtCodeNumber;
    private CheckBox cbxAgree;
    private Button btnContinue;
    private PhoneCountriesAdapter.CountryData countryData;
    private WaitingDialog waitingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PhoneCountriesAdapter adapter = new PhoneCountriesAdapter(getContext());
        Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        edtPhone = view.findViewById(R.id.edit_phone);
        txtCodeNumber = view.findViewById((R.id.text_view));
        cbxAgree = view.findViewById(R.id.checkbox);
        btnContinue = view.findViewById(R.id.button);
        btnContinue.setOnClickListener(v -> validateInputFields());
        edtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void onDestroyView() {
        stopProgress();
        super.onDestroyView();
    }

    private void validateInputFields() {
        if(countryData == null){
            new ErrorDialog(getContext(), R.string.error_country_not_selected);
            return;
        }

        String phone = edtPhone.getText().toString().trim();
        if(phone.isEmpty()){
            new ErrorDialog(getContext(), R.string.error_phone_number_field_is_empty);
            return;
        }
        PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
        String e164number;
        try {
            Phonenumber.PhoneNumber phoneNumber = ph.parse(phone, countryData.countryRegion);
            e164number = ph.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            Log.e(TAG, "isFieldsValidate: ", e);
            new ErrorDialog(getContext(), R.string.error_invalid_input);
            return;
        }

        if(!cbxAgree.isChecked()){
            new ErrorDialog(getContext(), R.string.error_terms_agreement_not_checked);
            return;
        }
        submitNumber(e164number);
    }

    private void submitNumber(String e164number) {
        // waiting bar
        startProgress().setMessage(R.string.sending_verification);

        Runnable runnable = () -> onTaskCompleted(e164number);
        btnContinue.postDelayed(runnable, 2000);
    }

    private void onTaskCompleted(String e164number) {
        stopProgress();

        PrefsHelper.getInstance(getContext())
                .setCountryRegion(countryData.countryRegion);
        ((AuthenticatorActivity) requireActivity()).gotoPhoneVerificationFragment(e164number);
    }

    private boolean isFieldsValidate() {
        if(countryData == null){
            new ErrorDialog(getContext(), R.string.error_country_not_selected);
            return false;
        }

        String phone = edtPhone.getText().toString().trim();
        if(phone.isEmpty()){
            new ErrorDialog(getContext(), R.string.error_phone_number_field_is_empty);
            return false;
        }
        PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
        String e164number;
        try {
            Phonenumber.PhoneNumber phoneNumber = ph.parse(phone, countryData.countryRegion);
            e164number = ph.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            Log.e(TAG, "isFieldsValidate: ", e);
            new ErrorDialog(getContext(), R.string.error_invalid_input);
            return false;
        }

        if(!cbxAgree.isChecked()){
            new ErrorDialog(getContext(), R.string.error_terms_agreement_not_checked);
            return false;
        }
        return true;
    }

    private boolean hasAutoSelected = false;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!hasAutoSelected){
            hasAutoSelected = true;
            onReady(parent);
        } else {
            countryData = (PhoneCountriesAdapter.CountryData) parent.getSelectedItem();
            PhoneNumberUtil ph = PhoneNumberUtil.getInstance();
            int countryCode = ph.getCountryCodeForRegion(countryData.countryRegion);
            txtCodeNumber.setText("+"+countryCode);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void onReady(AdapterView<?> adapterView){
        adapterView.setSelection(((PhoneCountriesAdapter) adapterView.getAdapter()).getDefaultItemId());
    }

    public WaitingDialog startProgress(){
        if(waitingDialog == null) {
            waitingDialog = new WaitingDialog();
        }
        waitingDialog.show(getChildFragmentManager(), TAG);
        return waitingDialog;
    }

    public void stopProgress() {
        if(waitingDialog != null){
            waitingDialog.dismiss();
        }
    }
}