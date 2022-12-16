package com.gnid.social.pincee.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentResultListener;

import android.os.Bundle;
import android.view.MenuItem;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.frags.auth.PhoneNumberFragment;
import com.gnid.social.pincee.ui.frags.auth.PhoneVerificationFragment;
import com.gnid.social.pincee.ui.frags.settings.AddNumberFragment;
import com.gnid.social.pincee.ui.frags.settings.ChangeNumberFragment;
import com.gnid.social.pincee.ui.frags.settings.NumberChangeOrAdditionNoticeFragment;

public class NumberModificationActivity extends AppCompatActivity implements FragmentResultListener {

    public static final String INTENT_ACTION_CHANGE_NUMBER = "com.gnid.pincee.CHANGE_NUMBER";
    public static final String INTENT_ACTION_ADD_NUMBER = "com.gnid.pincee.ADD_NUMBER";
    private static final String NOTIFY_CHARGE = "notify_action_charge";
    private static final String CHANGE_NUMBER = "change_number";
    private static final String VERIFY_NUMBER = "verify_number";
    private static final String ADD_NUMBER = "add_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.number_modification);
        }

        if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            bundle.putString(NumberChangeOrAdditionNoticeFragment.REQUEST_KEY, NOTIFY_CHARGE);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, NumberChangeOrAdditionNoticeFragment.class, bundle, NumberChangeOrAdditionNoticeFragment.TAG)
                    .commit();
            getSupportFragmentManager().setFragmentResultListener(NOTIFY_CHARGE, this, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int entryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (entryCount > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        switch (requestKey){
            case NOTIFY_CHARGE:
                switch (getIntent().getAction()){
                    case INTENT_ACTION_CHANGE_NUMBER:
                        Bundle bundle = new Bundle();
                        bundle.putString(ChangeNumberFragment.REQUEST_KEY, CHANGE_NUMBER);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, ChangeNumberFragment.class, bundle, ChangeNumberFragment.TAG)
                                .commit();
                        getSupportFragmentManager().setFragmentResultListener(CHANGE_NUMBER, this, this);
                        break;
                    case INTENT_ACTION_ADD_NUMBER:
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(AddNumberFragment.REQUEST_KEY, ADD_NUMBER);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, AddNumberFragment.class, bundle1, AddNumberFragment.TAG)
                                .commit();
                        getSupportFragmentManager().setFragmentResultListener(ADD_NUMBER, this, this);
                        break;
                    default: finish();
                }
                break;
            case CHANGE_NUMBER:
                Bundle bundle = new Bundle();
                bundle.putString(PhoneVerificationFragment.REQUEST_KEY, VERIFY_NUMBER);
                bundle.putString(PhoneVerificationFragment.KEY_PHONE_NUMBER,
                        result.getString(ChangeNumberFragment.RESULT_NUMBER_NEW));
                bundle.putString(PhoneVerificationFragment.KEY_OTHER_NUMBER, result.getString(ChangeNumberFragment.RESULT_NUMBER_OLD));
                bundle.putString(PhoneVerificationFragment.ACTION, PhoneVerificationFragment.ACTION_CHANGE_NUMBER);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, PhoneVerificationFragment.class, bundle, PhoneVerificationFragment.TAG)
                        .addToBackStack(PhoneVerificationFragment.TAG)
                        .commit();
                getSupportFragmentManager().setFragmentResultListener(VERIFY_NUMBER, this, this);
                break;
            case ADD_NUMBER:
                Bundle bundle2 = new Bundle();
                bundle2.putString(PhoneVerificationFragment.REQUEST_KEY, VERIFY_NUMBER);
                bundle2.putString(PhoneVerificationFragment.KEY_PHONE_NUMBER,
                        result.getString(AddNumberFragment.RESULT_NUMBER_NEW));
                bundle2.putString(PhoneVerificationFragment.ACTION, PhoneVerificationFragment.ACTION_ADD_NUMBER);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, PhoneVerificationFragment.class, bundle2, PhoneVerificationFragment.TAG)
                        .addToBackStack(PhoneVerificationFragment.TAG)
                        .commit();
                getSupportFragmentManager().setFragmentResultListener(VERIFY_NUMBER, this, this);
                break;
            case VERIFY_NUMBER:
                if(result.getBoolean(PhoneVerificationFragment.RESULT_OK, false)){
                    finish();
                }
                break;
        }
    }
}