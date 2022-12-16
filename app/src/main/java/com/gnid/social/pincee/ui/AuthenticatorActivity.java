package com.gnid.social.pincee.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gnid.social.pincee.ui.frags.auth.PhoneNumberFragment;
import com.gnid.social.pincee.ui.frags.auth.PhoneVerificationFragment;
import com.gnid.social.pincee.ui.frags.auth.SignInFragment;
import com.gnid.social.pincee.utils.helper.PrefsHelper;
import com.gnid.social.pinceeui.R;


public class AuthenticatorActivity extends AppCompatActivity {

    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String ACTIVITY_COMPLETED = "authentication completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, PhoneNumberFragment.class, null)
                    .commit();
        }
    }

    private void gotoFragment(Class<? extends Fragment> fragmentClass, Bundle args) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragmentClass, args)
                .setReorderingAllowed(true)
                .commit();
    }

    public void gotoPhoneVerificationFragment(String phone){
        Bundle params = new Bundle();
        params.putString(KEY_PHONE_NUMBER, phone);
        gotoFragment(PhoneVerificationFragment.class, params);
    }

    public void gotoSignInFragment(String phone) {
        Bundle params = new Bundle();
        params.putString(KEY_PHONE_NUMBER, phone);
        gotoFragment(SignInFragment.class, params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof EditText){
                outOfInputFocusHideKeyboard(ev, v);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void outOfEditInputHideKeyboard(MotionEvent ev, View v) {

    }
    private void outOfInputFocusHideKeyboard(MotionEvent ev, View v){
        Rect outRect = new Rect();
        v.getGlobalVisibleRect(outRect);
        if(!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())){
            v.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void intentComplete() {
        PrefsHelper.getInstance(this).setBoolean(ACTIVITY_COMPLETED, true);
        setResult(RESULT_OK);
        finish();
    }
}