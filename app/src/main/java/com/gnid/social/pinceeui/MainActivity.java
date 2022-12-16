package com.gnid.social.pinceeui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gnid.social.pincee.ui.AuthenticatorActivity;
import com.gnid.social.pincee.ui.SetBasicProfileActivity;
import com.gnid.social.pincee.ui.settings.SettingsActivity;
import com.gnid.social.pincee.ui.WelcomeActivity;
import com.gnid.social.pincee.ui.frags.home.CallListFragment;
import com.gnid.social.pincee.ui.frags.home.ChatListFragment;
import com.gnid.social.pincee.ui.frags.home.StatusFragment;
import com.gnid.social.pincee.utils.helper.PrefsHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private static final int WELCOME_REQUEST = 1;
    private static final int SET_PROFILE_REQUEST = 2;
    private PrefsHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);

        prefs = PrefsHelper.getInstance(this);
        if(!prefs.getBoolean(AuthenticatorActivity.ACTIVITY_COMPLETED, false)){
            startActivityForResult(new Intent(this, WelcomeActivity.class), WELCOME_REQUEST);
            return;
        }
        if(!prefs.getBoolean(SetBasicProfileActivity.ACTIVITY_COMPLETED, false)){
            startActivityForResult(new Intent(this, SetBasicProfileActivity.class), SET_PROFILE_REQUEST);
            return;
        }

        setView();
    }

    private void setView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.TAB_NAMES[position])
        ).attach();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode:"+requestCode+" resultCode:"+resultCode);
        switch (requestCode) {
            case WELCOME_REQUEST:
                if (prefs.getBoolean(AuthenticatorActivity.ACTIVITY_COMPLETED, false)) {
                    startActivityForResult(new Intent(this, SetBasicProfileActivity.class), SET_PROFILE_REQUEST);
                } else {
                    finish();
                }
                break;
            case SET_PROFILE_REQUEST:
                if (prefs.getBoolean(SetBasicProfileActivity.ACTIVITY_COMPLETED, false)) {
                    setView();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_action_bar, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }


    static class ViewPagerAdapter extends FragmentStateAdapter {
        public static final int[] TAB_NAMES = {
                R.string.status, R.string.chats, R.string.calls
        };
        Fragment[] fragments = new Fragment[3];
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = fragments[position];
            if(fragment == null) {
                switch (position) {
                    case 1:
                        fragments[position] = new ChatListFragment();
                        break;
                    case 2:
                        fragments[position] = new CallListFragment();
                        break;
                    case 0:
                        fragments[position] = new StatusFragment();
                }
            } else {
                return fragment;
            }
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }
}