package com.gnid.social.pincee.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.ui.dialog.ActionsDialog;
import com.gnid.social.pincee.utils.helper.PrefsHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SetBasicProfileActivity extends AppCompatActivity {
    private static final int CONTACT_AND_STORAGE_REQUEST = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CHOICE_IMAGE = 102;
    private static final int REQUEST_IMAGE_CROP = 103;
    private static final int REQUEST_IMAGE_SELECT = 104;
    private static final int STORAGE_REQUEST = 105;
    private final String TAG = SetBasicProfileActivity.class.getSimpleName();
    public static final String ACTIVITY_COMPLETED = "set basic profile completed";
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_basic_profile);

        TextInputLayout inputLayout = findViewById(R.id.edit_text_layout);
        EditText edtProfileName = inputLayout.findViewById(R.id.edit_text);
        edtProfileName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) inputLayout.setError(null);
            }
        });

        imgProfile = findViewById(R.id.image_view);
        imgProfile.setOnClickListener(v -> uploadImage());
        findViewById(R.id.button).setOnClickListener(v -> validateInput(inputLayout, edtProfileName));

        if (savedInstanceState == null) {
            Handler handler = new Handler();
            handler.postDelayed(this::tasksOnPermission, 200);
        }
    }

    private void uploadImage() {
        // check if there is permission to allow storage access
        boolean storageAccess = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(storageAccess) {
            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                new ActionsDialog(this) {
                    @Override
                    public void onItemClick(android.app.AlertDialog dialog, int which) {
                        dialog.dismiss();
                        if (which == TAKE_PICTURE) {
                            dispatchTakePictureIntent();
                        } else if (which == SELECT_IMAGE) {
                            dispatchSelectImageIntent();
                        }
                    }
                };
            } else {
                dispatchSelectImageIntent();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST);
        }
    }

    private void dispatchSelectImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_SELECT);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, R.string.no_activity_found, Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        Log.i(TAG, "createImageFile: path:"+image.getAbsolutePath());
        return image;
    }

    Uri currentPhotoUri;
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure there is a camera activity to handle the action
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null){
                currentPhotoUri = FileProvider.getUriForFile(this,
                        "com.gnid.pincee.FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchCropImageIntent(Uri imageUri, boolean fromCamera) {
        Intent cropImageIntent = new Intent("com.android.camera.action.CROP")
                .setDataAndType(imageUri,"image/*")
                .putExtra("crop", "true")
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra("outputX", 128)
                .putExtra("outputY", 128)
                .putExtra("scale", true)
                .putExtra("scaleUpIfNeeded", true)
//                .putExtra("return-data", true)
//                .putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(cropImageIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(cropImageIntent, REQUEST_IMAGE_CROP);
        } else {
            Toast.makeText(this, R.string.no_activity_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: req:"+requestCode+" res:"+resultCode+" data:"+data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            assert currentPhotoUri != null;
            dispatchCropImageIntent(currentPhotoUri, true);
        }
        if(requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Uri d = data.getData();
            if(extras != null){
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if(imageBitmap != null){
                    Log.i(TAG, "onActivityResult: imageBitmap:"+imageBitmap);
                    imgProfile.setImageBitmap(imageBitmap);
                }
            }
            else {
                Log.i(TAG, "onActivityResult: d:"+d);
                imgProfile.setImageURI(d);
            }

        }
        if(requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            dispatchCropImageIntent(imageUri, false);
        }
    }

    private void validateInput(TextInputLayout inputLayout, EditText edtProfileName) {
        String name = edtProfileName.getText().toString().trim();
        if(name.isEmpty()){
            inputLayout.setError(" ");
        } else {
            // TODO: do not show loading to update user profile.
            //  Update local change. Update server change in background.
            //  if there is no network, auto update is when there is network.
            PrefsHelper.getInstance(this).setBoolean(ACTIVITY_COMPLETED, true);
            finish();
        }
    }

    private void tasksOnPermission(){
        boolean contactAccess = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean storageAccess = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!(contactAccess && storageAccess)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.contacts_and_media)
                    .setMessage(R.string.request_contacts_and_storage_permission)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(this,
                                new String[]{
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                CONTACT_AND_STORAGE_REQUEST);
                    })
                    .setNeutralButton(R.string.not_now, (dialog, which) -> dialog.dismiss());
            builder.show();
        }
        if(contactAccess){
            // TODO: Start contact synchronization
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
        for(String p: permissions){
            Log.i(TAG, "permission: "+p);
        }
        for(int i: grantResults){
            Log.i(TAG, "grantResult: "+i);
        }
        if (requestCode == CONTACT_AND_STORAGE_REQUEST) {
            // if request is cancelled, the result array are empty.
            if (grantResults.length > 0) {
                // if contacts granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: " + permissions[0] + " granted");
                    // TODO: Start contact synchronization
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: " + permissions[0] + " denied");
                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: " + permissions[1] + " granted");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: " + permissions[1] + " denied");
                }
            }
        }
        if (requestCode == STORAGE_REQUEST && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadImage();
            }
        }
    }
}