package com.gnid.social.pincee.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.gnid.social.pinceeui.MainActivity;
import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.frags.settings.MainSettingsFragment;

public class SettingsActivity extends AppCompatActivity{
    public static final String REQUEST_KEY = "request_key";
    public static final String KEY_PARENT_TITLE = "parent_title";
    public static final String KEY_TITLE = "title";
    private final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MainSettingsFragment.class, null, MainSettingsFragment.TAG)
                    .commit();
        }

    }

    public int enterFragment(Class<? extends Fragment> fragmentClass, Bundle bundle){
        return getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, fragmentClass, bundle, fragmentClass.getSimpleName())
                .addToBackStack(fragmentClass.getSimpleName())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int entryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (entryCount > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        }
        if(!super.onOptionsItemSelected(item))
            startActivity(new Intent(this, MainActivity.class));
        return true;
    }

}