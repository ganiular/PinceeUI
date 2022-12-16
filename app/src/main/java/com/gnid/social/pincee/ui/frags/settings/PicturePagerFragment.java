package com.gnid.social.pincee.ui.frags.settings;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.adapters.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PicturePagerFragment extends Fragment {
    public static final String TAG = PicturePagerFragment.class.getSimpleName();
    public static final String KEY_PROFILE_IMAGES = "profile_images";
    public static final String KEY_IMAGE_SELECTED_POSITION = "image_position";

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Transition transition = TransitionInflater.from(requireContext())
//                .inflateTransition(R.transition.shared_iamge);
//        setSharedElementEnterTransition(transition);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewPager2 viewPager = (ViewPager2) inflater.inflate(R.layout.fragment_picture_pager, container, false);
        ArrayList<Integer> profileImages = requireArguments().getIntegerArrayList(KEY_PROFILE_IMAGES);
        viewPager.setAdapter(new ImagePagerAdapter(this, profileImages));
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        viewPager.setCurrentItem(requireArguments().getInt(KEY_IMAGE_SELECTED_POSITION), false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                ProfileSettingsFragment.currentSelectedProfileImagePosition = position;
            }
        });

        prepareSharedElementTransition();

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        return viewPager;
    }

    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.shared_image);
        setSharedElementEnterTransition(transition);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Locate the image view at the primary fragment (the ImageFragment that is currently
                // visible). To locate the fragment, with its fragment manager find fragment tag prefix
                // with 'f' then next the to fragment index
                Fragment currentFragment = getChildFragmentManager().
                        findFragmentByTag("f" + ProfileSettingsFragment.currentSelectedProfileImagePosition);

                if (currentFragment == null || currentFragment.getView() == null) {
                    return;
                }

                // Map the first shared element name to the child ImageView.
                sharedElements.put(names.get(0), currentFragment.getView().findViewById(R.id.image_view));
                Log.i(TAG, "onMapSharedElements: sharedElements:"+sharedElements);
            }
        });
    }
}
