package com.gnid.social.pincee.utils.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gnid.social.pincee.ui.frags.settings.ImageFragment;

import java.util.ArrayList;

public class ImagePagerAdapter extends FragmentStateAdapter {
    private final ArrayList<Integer> profileImages;

    public ImagePagerAdapter(Fragment fragment, ArrayList<Integer> profileImages) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
        this.profileImages = profileImages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImageFragment.newInstance(profileImages.get(position));
    }

    @Override
    public int getItemCount() {
        return profileImages.size();
    }
}
