package com.gnid.social.pincee.ui.frags.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.db.model.UserProfile;
import com.gnid.social.pincee.ui.settings.NumberModificationActivity;
import com.gnid.social.pincee.ui.settings.SettingsActivity;
import com.gnid.social.pincee.ui.frags.bottomsheet.EditTextBottomSheet;
import com.gnid.social.pincee.ui.frags.bottomsheet.ModalBottomSheet;
import com.gnid.social.pincee.ui.frags.bottomsheet.NumberOptionBottomSheet;
import com.gnid.social.pincee.ui.frags.bottomsheet.PictureOptionBottomSheet;
import com.gnid.social.pincee.ui.frags.bottomsheet.ProfilePicturesBottomSheet;
import com.gnid.social.pincee.ui.frags.bottomsheet.WelcomeIntervalBottomSheet;
import com.gnid.social.pincee.utils.MyClipboardManager;
import com.gnid.social.pincee.utils.helper.Info;

import java.util.List;
import java.util.Map;

public class ProfileSettingsFragment extends Fragment implements FragmentResultListener {
    private static final String WRITE_ABOUT = "write_about";
    private static final String WRITE_NAME = "write_name";
    private static final String SELECT_INTERVAL = "select_interval";
    private static final String SELECT_NUMBER_OPTION = "select_number_option";
    private static final long PICTURE_DURATION = 300;
    public static final String TAG = ProfileSettingsFragment.class.getSimpleName();
    private UserProfile profile;
    private AlertDialog alertDialog;
    public static int currentSelectedProfileImagePosition = 0;
    private boolean newlyCreated = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newlyCreated = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((SettingsActivity) requireActivity()).getSupportActionBar().setTitle(R.string.profile);

        if(newlyCreated){
            prepareTransitions();
        }

        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other
     * transitions that affect the flow.
     */
    boolean prepareTransitionsCalled = false;
    private void prepareTransitions() {
        if(prepareTransitionsCalled) return;
        prepareTransitionsCalled = true;
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // TODO: find the selected profile imageView to translate back to, then
                        //  Map the first shared element name to the ImageView.
                        sharedElements.put(names.get(0),
                                getProfileImageView(requireView(), ProfileSettingsFragment.currentSelectedProfileImagePosition));
                        Log.i(TAG, "onMapSharedElements: sharedElements:"+sharedElements);
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        profile = new UserProfile(
                "Ganiular",
                "I'm a developer",
                false,
                1,
                new CharSequence[]{
                        "07030643332", "09063232430"
                },
                new int[]{
                        R.mipmap.images_3,
                        R.mipmap.images_4,
                        R.mipmap.images_2,
                        R.mipmap.images_1
                });

        FrameLayout frmPictures = view.findViewById(R.id.pictures_frame);
        displayProfilePictures(frmPictures);

        if(newlyCreated) {
            newlyCreated = false;
            animateProfilePics2();
        }

        LinearLayout listNumber = view.findViewById(R.id.list);
        View itemView;
        for (int i = 0; i < profile.numbers.size(); i++) {
            itemView = getLayoutInflater().inflate(R.layout.item_list_settings_number, listNumber, false);
            ((TextView) itemView.findViewById(R.id.text_view)).setText(profile.numbers.get(i));
            itemView.setId(i);
            itemView.setOnClickListener(this::onNumberClicked);
            itemView.setOnLongClickListener(this::onNumberLongClicked);
            listNumber.addView(itemView, i);
        }

        view.findViewById(R.id.add_number).setOnClickListener(v -> onAddMoreNumberClicked());
        view.findViewById(R.id.set_profile_name).setOnClickListener(v -> onEditNameClicked());
        view.findViewById(R.id.set_story).setOnClickListener(v -> showEditStoryBottomSheet());
        view.findViewById(R.id.set_interval).setOnClickListener(this::showWelcomeIntervalBottomSheet);

        TextView txtName = view.findViewById(R.id.text_view_1);
        txtName.setText(profile.name);
        TextView txtStory = view.findViewById(R.id.text_view_2);
        txtStory.setText(profile.story);

        SwitchCompat switchWelcome = view.findViewById(R.id.set_switch);
        switchWelcome.setOnCheckedChangeListener(this::onCheckWelcome);
        onCheckWelcome(switchWelcome, profile.autoWelcome);

        String text = getResources().getStringArray(R.array.welcome_interval)[profile.welcomeStatus];
        TextView txtInterval = view.findViewById(R.id.text_view_3);
        txtInterval.setText(text);


        // TODO:
        //  IF: there is no profile picture use camara icon with plus sign
        //      and let the onclick show PictureOptionBottomSheet
        //  ELSE: use camera icon without plus sign
        //      and let the onclick show ProfilePicturesBottomSheet
        ImageView imgProfileCamera = view.findViewById(R.id.button_1);
        if(profile.pictures.size() > 0){
            imgProfileCamera.setImageResource(R.drawable.ic_photo_camera_24);
            imgProfileCamera.setOnClickListener(v -> showPicturesBottomSheet());
        } else {
            imgProfileCamera.setOnClickListener(v -> showPictureOptionBottomSheet());
        }
    }

    final int[] profileImageViews = new int[]{
            R.id.profile_image_1,
            R.id.profile_image_2,
            R.id.profile_image_3,
            R.id.profile_image_4,
            R.id.profile_image_5
    };
    private ImageView getProfileImageView(View container, int position){
        return container.findViewById(profileImageViews[position]);
    }

    private void displayProfilePictures(FrameLayout frmPictures) {
        View subFrame = frmPictures.findViewById(R.id.sub_frame);
        ImageView imageView;
        int displayWidth = Info.getDisplayWidth(requireContext());
        int marginWidth = (int) getResources().getDimension(R.dimen.larger_margin);
        int pictureHalfWidth = (int) getResources().getDimension(R.dimen.settings_profile_image_size) / 2;
        int width = displayWidth - marginWidth - pictureHalfWidth;
        int divider = width / (profile.pictures.size() + 1);
        divider = Math.min(divider, pictureHalfWidth);
        int pivot = divider * (profile.pictures.size() + 1) / 2;
        int offset = pivot - divider;
        for(int i=0; i<profile.pictures.size(); i++) {
            if(i==0){
                imageView = getProfileImageView(subFrame, i);
                subFrame.setTranslationX(offset);
            } else {
                imageView = getProfileImageView(frmPictures, i);
                imageView.setTranslationX(offset);
            }
            offset -= divider;
            // Set the string value of the image resource as the unique transition name for the view.
            imageView.setTransitionName(String.valueOf(profile.pictures.get(i)));

            Glide.with(requireContext())
                    .load(profile.pictures.get(i))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
            int finalI = i;
            imageView.setOnClickListener(v -> onProfileImageClicked(v, finalI));
        }
    }
    void animateProfilePics2(){
        FrameLayout frmPictures = requireView().findViewById(R.id.pictures_frame);
        frmPictures.post(new Runnable() {
            @Override
            public void run() {
                View subFrame = frmPictures.findViewById(R.id.sub_frame);
                ImageView imageView;
                int pictureHalfWidth = subFrame.getWidth() / 2;
                int width = (int) (frmPictures.getWidth() - pictureHalfWidth + getResources().getDimension(R.dimen.larger_margin));
                int divider = width / (profile.pictures.size() + 1);
                divider = Math.min(divider, pictureHalfWidth);
                int pivot = divider * (profile.pictures.size() + 1) / 2;
                int offset = pivot - divider;
                for (int i = 0; i < profile.pictures.size(); i++) {
                    if (i == 0) {
                        subFrame.setTranslationX(0);
                        subFrame.animate().setDuration(PICTURE_DURATION).translationX(offset).start();
                    } else {
                        imageView = getProfileImageView(frmPictures, i);
                        imageView.setTranslationX(0);
                        imageView.animate().setDuration(PICTURE_DURATION).translationX(offset).start();
                    }
                    offset -= divider;
                }
                frmPictures.removeCallbacks(this);
            }
        });
    }

    private void onProfileImageClicked(View imageView, int position) {
        currentSelectedProfileImagePosition = position;
        Bundle args = new Bundle();
        args.putIntegerArrayList(PicturePagerFragment.KEY_PROFILE_IMAGES, profile.pictures);
        args.putInt(PicturePagerFragment.KEY_IMAGE_SELECTED_POSITION, position);
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(imageView,  imageView.getTransitionName())
                .replace(R.id.fragment_container_view, PicturePagerFragment.class, args, PicturePagerFragment.TAG)
                .addToBackStack(PicturePagerFragment.TAG)
                .commit();
    }

    private void showWelcomeIntervalBottomSheet(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(WelcomeIntervalBottomSheet.INITIAL_VALUE, profile.welcomeStatus);
        bundle.putString(WelcomeIntervalBottomSheet.REQUEST_KEY, SELECT_INTERVAL);
        WelcomeIntervalBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), bundle);
        getParentFragmentManager().setFragmentResultListener(
                SELECT_INTERVAL, this, this);
    }

    private void onCheckWelcome(CompoundButton compoundButton, boolean isChecked) {
        ViewGroup setInterVal = requireView().findViewById(R.id.set_interval);
        setViewsEnable(setInterVal, isChecked);
        if (isChecked && (profile.story == null || profile.story.length() == 0)) {
            Toast.makeText(getContext(), R.string.write_your_story_first, Toast.LENGTH_SHORT).show();
            compoundButton.setChecked(false);
        }else {
            compoundButton.setChecked(isChecked);
            profile.autoWelcome = isChecked;
        }
    }

    private void setViewsEnable(ViewGroup container, boolean enabled) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            child.setEnabled(enabled);
        }
        container.setEnabled(enabled);
    }

    private void showPicturesBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ProfilePicturesBottomSheet.KEY_PICTURES, profile.pictures);
        ProfilePicturesBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), bundle);
    }

    private void showPictureOptionBottomSheet() {
        PictureOptionBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), null);
    }

    private void showEditStoryBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putString(EditTextBottomSheet.TITLE, getString(R.string.write_story));
        bundle.putCharSequence(EditTextBottomSheet.TEXT, profile.story);
        bundle.putString(EditTextBottomSheet.DESCRIPTION, getString(R.string.your_story_could_be_shown_to_people_as_welcome_note));
        bundle.putInt(EditTextBottomSheet.MAX_LENGTH, 250);
        bundle.putBoolean(EditTextBottomSheet.SINGLE_LINE, false);
        bundle.putString(EditTextBottomSheet.REQUEST_KEY, WRITE_ABOUT);
        EditTextBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), bundle);
        getParentFragmentManager().setFragmentResultListener(
                WRITE_ABOUT, this, this);
    }

    private void onEditNameClicked() {
        Bundle bundle = new Bundle();
        bundle.putString(EditTextBottomSheet.TITLE, getString(R.string.enter_profile_name));
        bundle.putCharSequence(EditTextBottomSheet.TEXT, profile.name);
        bundle.putString(EditTextBottomSheet.DESCRIPTION, getString(R.string.your_profile_name_can_be_visible_to_anyone));
        bundle.putString(EditTextBottomSheet.REQUIRE_MESSAGE, getString(R.string.error_profile_name_is_required));
        bundle.putInt(EditTextBottomSheet.MAX_LENGTH, 25);
        bundle.putBoolean(EditTextBottomSheet.SINGLE_LINE, true);
        bundle.putString(EditTextBottomSheet.REQUEST_KEY, WRITE_NAME);
        EditTextBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), bundle);
        getParentFragmentManager().setFragmentResultListener(
                WRITE_NAME, this, this);
    }

    private boolean onNumberLongClicked(View view) {
        MyClipboardManager.copyTextToClipboard(requireContext(), profile.numbers.get(view.getId()));
        return true;
    }

    private void onAddMoreNumberClicked() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(requireContext(), NumberModificationActivity.class);
        intent.setAction(NumberModificationActivity.INTENT_ACTION_ADD_NUMBER);
        requireActivity().startActivity(intent, bundle);
    }

    private void onNumberClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(NumberOptionBottomSheet.REQUEST_KEY, SELECT_NUMBER_OPTION);
        bundle.putInt(NumberOptionBottomSheet.PUSHBACK_DATA, view.getId());
        bundle.putBoolean(NumberOptionBottomSheet.INCLUDE_DELETE, profile.numbers.size() > 1);
        NumberOptionBottomSheet.open(R.id.fragment_container_view, getParentFragmentManager(), bundle);
        getParentFragmentManager().setFragmentResultListener(SELECT_NUMBER_OPTION, this, this);
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        switch (requestKey) {
            case WRITE_ABOUT:
                if (result.getBoolean(EditTextBottomSheet.RESULT_OK, false)) {
                    TextView txtAbout = requireView().findViewById(R.id.text_view_2);
                    CharSequence text = result.getCharSequence(EditTextBottomSheet.RESULT_DATA, null);
                    txtAbout.setText(text);
                    if (text == null || text.length() == 0) {
                        onCheckWelcome(requireView().findViewById(R.id.set_switch), false);
                        profile.story = null;
                    } else {
                        profile.story = text.toString();
                    }
                } else {
                    if (profile.story == null) {
                        onCheckWelcome(requireView().findViewById(R.id.set_switch), false);
                    }
                }
                break;
            case WRITE_NAME:
                if (result.getBoolean(EditTextBottomSheet.RESULT_OK, false)) {
                    TextView txtName = requireView().findViewById(R.id.text_view_1);
                    CharSequence name = result.getCharSequence(EditTextBottomSheet.RESULT_DATA, null);
                    txtName.setText(name);
                    profile.name = name;
                }
                break;
            case SELECT_INTERVAL:
                if(result.getBoolean(WelcomeIntervalBottomSheet.RESULT_OK, false)){
                    profile.welcomeStatus = result.getInt(WelcomeIntervalBottomSheet.RESULT_DATA_INDEX);
                    String text = getResources().getStringArray(R.array.welcome_interval)[profile.welcomeStatus];
                    TextView txtInterval = requireView().findViewById(R.id.text_view_3);
                    txtInterval.setText(text);
                }
                break;
            case SELECT_NUMBER_OPTION:
                if(result.getBoolean(NumberOptionBottomSheet.RESULT_OK, false)){
                    int option = result.getInt(NumberOptionBottomSheet.RESULT_OPTION);
                    int itemId = result.getInt(NumberOptionBottomSheet.PUSHBACK_DATA);
                    if(option == NumberOptionBottomSheet.CHANGE){
                        requestChangeNumber(itemId);
                    }else if(option == NumberOptionBottomSheet.DELETE){
                        if(alertDialog == null)
                            alertDialog = new AlertDialog.Builder(getContext())
                                    .setMessage(R.string.remove_phone_number)
                                    .setPositiveButton(R.string.remove, (dialog, which) -> removeNumber(itemId))
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> onDialogCancel(dialog))
                                    .setOnDismissListener(dialog -> onDialogCancel(dialog))
                                    .create();
                        alertDialog.show();
                    }else if(option == NumberOptionBottomSheet.COPY){
                        MyClipboardManager.copyTextToClipboard(requireContext(), profile.numbers.get(itemId));
                    }
                }
        }
    }

    private void requestChangeNumber(int itemId) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(requireContext(), NumberModificationActivity.class);
        intent.setAction(NumberModificationActivity.INTENT_ACTION_CHANGE_NUMBER);
        requireActivity().startActivity(intent, bundle);
    }

    private void onDialogCancel(DialogInterface dialog) {
        alertDialog = null;
    }

    private void removeNumber(int itemId) {
        profile.numbers.remove(itemId);
        LinearLayout listNumber = requireView().findViewById(R.id.list);
        listNumber.removeViewAt(itemId);
        for (int i = itemId; i < profile.numbers.size(); i++) {
            listNumber.getChildAt(i).setId(i);
        }
    }
}
