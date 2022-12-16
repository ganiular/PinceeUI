package com.gnid.social.pincee.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.gnid.social.pinceeui.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WelcomeActivity extends AppCompatActivity {
    private static final int AUTO_SLIDE_INTERVAL = 3500;
    private final String TAG = WelcomeActivity.class.getSimpleName();
    ViewPager2 viewPager;
    private final Handler autoSlideHandler = new Handler();
    private final Runnable autoSlideRunner = new Runnable() {
        @Override
        public void run() {
            int currentPos = viewPager.getCurrentItem();
            if (currentPos < viewPager.getAdapter().getItemCount()) {
                viewPager.setCurrentItem(currentPos + 1, true);
                autoSlideHandler.postDelayed(this, AUTO_SLIDE_INTERVAL);
            } else {
                stopAutoSlidePage();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView backgroundImage1 = findViewById(R.id.background_image_1);
        ImageView backgroundImage2 = findViewById(R.id.background_image_2);
        Button btnGetStarted = findViewById(R.id.button);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Animation fadeOut = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade_out_welcome_view_pager);
                backgroundImage1.setImageResource(ViewPagerAdapter.imageBackgrounds[position]);
                backgroundImage2.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        backgroundImage2.setImageResource(ViewPagerAdapter.imageBackgrounds[position]);
                        if(btnGetStarted.getVisibility() == View.INVISIBLE && viewPager.getAdapter().getItemCount() == position + 1){
                            btnGetStarted.setVisibility(View.VISIBLE);
                            btnGetStarted.setOnClickListener(v -> intentComplete());
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout_dots);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        });
        mediator.attach();
    }

    private void startAutoSlidePage() {
        autoSlideHandler.postDelayed(autoSlideRunner, AUTO_SLIDE_INTERVAL);
    }

    private void stopAutoSlidePage() {
        autoSlideHandler.removeCallbacks(autoSlideRunner);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAutoSlidePage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAutoSlidePage();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: stopAutoSlidePage(); break;
            case MotionEvent.ACTION_UP: startAutoSlidePage(); break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void intentComplete(){
        startActivity(new Intent(this, AuthenticatorActivity.class));
//        setResult(RESULT_OK);
        finish();
    }
}


class ViewPagerAdapter extends FragmentStateAdapter {
    int[] imageResources = {
            R.drawable.temp_pincee,
            R.drawable.ic_sim,
            R.drawable.temp_sim_red
    };
    static final int[] imageBackgrounds = {
            R.mipmap.images_4,
            R.mipmap.images_2,
            R.mipmap.images_3
    };
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putInt(ViewPageFragment.KEY_IMAGE_VALUE, imageResources[position]);
        Fragment fragment = new ViewPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }

    public static class ViewPageFragment extends Fragment{
        static final String KEY_IMAGE_VALUE = "image_value";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.item_view_pager, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            Bundle args = requireArguments();
            ImageView imageView = view.findViewById(R.id.image_view);
            imageView.setImageResource(args.getInt(KEY_IMAGE_VALUE));
        }
    }
}