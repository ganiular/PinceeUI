package com.gnid.social.pincee.ui.frags.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gnid.social.pinceeui.R;

public class ImageFragment extends Fragment {
    private static final String TAG = ImageFragment.class.getSimpleName();
    private static final String KEY_IMAGE_URL = "image_url";

    public static ImageFragment newInstance(int imageUrl) {
        Bundle args = new Bundle();
        args.putInt(KEY_IMAGE_URL, imageUrl);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @DrawableRes int imageUrl = requireArguments().getInt(KEY_IMAGE_URL);
        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
        ImageView imageView = view.findViewById(R.id.image_view);

        // Just like I do when to each profile image in the ProfileSettingsFragment,
        // the transition name to be the string value of the image res.
        imageView.setTransitionName(String.valueOf(imageUrl));

        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.i(TAG, "onLoadFailed: ");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // start the postponeEnterTransition in PictureViewFragment
                        assert getParentFragment() instanceof PicturePagerFragment;
                                getParentFragment().startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
        return view;
    }
}
