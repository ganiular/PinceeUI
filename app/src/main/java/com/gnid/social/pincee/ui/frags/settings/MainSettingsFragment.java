package com.gnid.social.pincee.ui.frags.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.settings.SettingsActivity;

public class MainSettingsFragment extends Fragment{
    public static final String TAG = MainSettingsFragment.class.getSimpleName();
    private int commitId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((SettingsActivity) requireActivity()).getSupportActionBar().setTitle(R.string.settings);
        prepareTransitions();
        return inflater.inflate(R.layout.fragment_home_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.profile_settings).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.chats_settings).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.security_settings).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.notification_settings).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.send_feedback).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.invite_friend).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.about).setOnClickListener(this::onListItemClicked);
        view.findViewById(R.id.help).setOnClickListener(this::onListItemClicked);
    }

    private void onListItemClicked(View view) {
        if(view.getId() == R.id.profile_settings){
            this.commitId = getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, ProfileSettingsFragment.class, null, ProfileSettingsFragment.TAG)
                    .addToBackStack(ProfileSettingsFragment.TAG)
                    .commit();
        } else
            Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
    }

    private void prepareTransitions(){
//        setExitTransition();
//        setExitSharedElementCallback();
    }
}
