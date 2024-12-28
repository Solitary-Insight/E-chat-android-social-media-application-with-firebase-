package com.example.social_app.UserRepository.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.Callbacks.ProfileCallbacks.ProfileSaveListener;
import com.example.social_app.UserRepository.Controllers.LoadingDialogue;
import com.example.social_app.UserRepository.Controllers.UserFirebaseHandler;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class User_info_activity extends AppCompatActivity {
    private static final int IMAGES_REQUEST_CODE = 1;

    private ImageView ivProfilePicture;
    private EditText etUsername, etEmail, etPhone, etBio;
    private Button btnUploadPicture, btnSaveProfile;
    private Uri profilePictureUri;
    UserFirebaseHandler userFirebaseHandler;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



//        initialize elements
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etBio = findViewById(R.id.et_bio);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        btnUploadPicture = findViewById(R.id.btn_upload_picture);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        userFirebaseHandler=new UserFirebaseHandler(this);

        String userJson = getIntent().getStringExtra("CURRENT_USER");
        UserModel user = new Gson().fromJson(userJson, UserModel.class);
        if(user!=null){
            Log.d("CURRENT_USER",user.toString());
            setUserOnView(user);

        }else{
            currentUser= FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser!=null){

                user=new UserModel(currentUser.getUid(),currentUser.getDisplayName(),currentUser.getEmail(), currentUser.getPhoneNumber(),currentUser.getPhotoUrl()==null?"":currentUser.getPhotoUrl().toString(),"",new HashMap<>(),new HashMap<>(),new HashMap<>(),new ArrayList<>());

                setUserOnView(user);

            }

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }

        //button Click for getting image from gallery
        btnUploadPicture.setOnClickListener(view -> {
            if(requestPermission()){
                openImageGallery();
                Log.d("PERMISSION_REQUEST","perission given");
            }else{
                CustomToast.ShowCustomToast("Permission denied for media accees","Permission denied!","error",this);
                Log.d("PERMISSION_REQUEST","perission denied");

            }
        });


        UserModel finalUser = user;
        btnSaveProfile.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String bio = etBio.getText().toString();




            Dialog dialog= LoadingDialogue.createLoadingDialog(this,"Saving user profile",R.raw.loading_lottie);
            if(UserFirebaseHandler.isUserFormValid(finalUser,username, email, phone, bio, profilePictureUri,this)){
                dialog.show();

                userFirebaseHandler.saveUserProfile(this,username, email, phone, bio, profilePictureUri, finalUser, new ProfileSaveListener() {
                    @Override
                    public void onProfileSaved(String message) {
                        dialog.dismiss();
                        CustomToast.ShowCustomToast(message,"Success","success",User_info_activity.this);
                        finish();

                    }

                    @Override
                    public void onProfileSaveFailure(String error) {
                        dialog.dismiss();
                        CustomToast.ShowCustomToast(error,"Failed!","error",User_info_activity.this);

                    }
                });
            }

        });

    }

    private void setUserOnView(UserModel user) {

        etUsername.setText(user.getUsername());
        etBio.setText(user.getBio());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());
        Glide.with(this).load(user.getProfilePictureUrl())  // URL or URI of the image
                .transform(new CircleCrop())
                .placeholder(R.drawable.user)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(ivProfilePicture);

        etUsername.setText(user.getUsername());

    }


    // requesting Permission


    public boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted. No need to request agai

            return  true;

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_MEDIA_IMAGES)) {
            // Explain why the permission is needed and show the UI
            showInContextUI(this,false);
        } else {
            // Request permission if not granted and no rationale needed

            requestPermissions(new String[] { Manifest.permission.READ_MEDIA_IMAGES},IMAGES_REQUEST_CODE);
        }
        return  false;
    }

    // Show an educational UI explaining why the permission is needed

// Show the rationale UI in a dialog
        private void showInContextUI( Activity activity,Boolean override) {
            new AlertDialog.Builder(activity)
                    .setTitle("Permission Needed")
                    .setMessage("This app requires access to your images for uploading profile pictures and media posts. " +
                            "Please grant this permission to continue using these features.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Request permission after explaining why it's needed
                            if(!override){
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, IMAGES_REQUEST_CODE);
                            }else{
                                goToAppSettings();
                            }
                        }
                    })
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue without granting permission, show a toast or other behavior
                            CustomToast.ShowCustomToast( "Permission denied, media features will be unavailable.","Permission denied!","error",activity );
                        }
                    })
                    .create()
                    .show();
        }

        //Lauch Gallary Screen
        // ActivityResultLauncher to handle the result of the image picker
        private final ActivityResultLauncher<Intent> imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        // Handle the selected image URI (e.g., upload it or display it)
                        profilePictureUri=imageUri;
                        try {
                            Glide.with(this).load(imageUri)  // URL or URI of the image
                                    .transform(new CircleCrop())
                                    .placeholder(R.drawable.user)   // Optional placeholder while loading
                                    .error(R.drawable.user)               // Optional error image
                                    .into(ivProfilePicture);;

                        }catch (Exception e){
                            e.printStackTrace();
                            CustomToast.ShowCustomToast("No image selected."+e.getLocalizedMessage(),"Media uptake failed!", "error", this);
                        }
                        CustomToast.ShowCustomToast("Image selected successfully, continue .","Success" , "success", this);
                    } else {
                        CustomToast.ShowCustomToast("No image selected. Please chooose image.","Image selection failed!", "warning", this);
                    }
                });



        // open Gallary
    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }


    //navigate to setting for permission grant
    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }


        // handle on request granted

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IMAGES_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    CustomToast.ShowCustomToast("Permission granted for media access.", "Permission granted","info",this);
                    openImageGallery();

                } else {
                    CustomToast.ShowCustomToast( "Permission denied, media features will be unavailable.","Permission denied!","error",this );
                    showInContextUI(this,true);
                }
                return;
        }

    }
}




