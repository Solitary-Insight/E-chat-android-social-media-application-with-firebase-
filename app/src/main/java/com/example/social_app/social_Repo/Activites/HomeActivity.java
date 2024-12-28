package com.example.social_app.social_Repo.Activites;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;

import com.example.social_app.R;
import com.example.social_app.UserRepository.Activities.ProfileFragment;
import com.example.social_app.UserRepository.Activities.Profile_Activity;
import com.example.social_app.UserRepository.Activities.User_info_activity;
import com.example.social_app.UserRepository.Controllers.Callbacks.OnPostListener;
import com.example.social_app.UserRepository.Controllers.FirebasePostController;
import com.example.social_app.UserRepository.Controllers.LoadingDialogue;
import com.example.social_app.UserRepository.Controllers.UserUtils;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnCreatePostClickListener;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnMediaSelectedListener;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnUploadMediaClickListener;
import com.example.social_app.authentication.Activities.LoginActivity;
import com.example.social_app.social_Repo.MediaUri;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
   BottomNavigationView bottomNavigationView;
   FrameLayout container;
    FragmentManager fragmentManager ;
    FirebaseAuth mAuth;
    FirebaseUser current_user;
    UserModel current_User;
    FeedFragment feedFragment=null;
    CreatePostFragment createPostFragment=null;
    ProfileFragment profileFragment;
    Friends_Fragment friendsFragment;
    OnMediaSelectedListener mediaListener;

    final int GALLERY_MEDIA_PICK_CODE=10;
    private static final int IMAGES_REQUEST_CODE = 1;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();
        if (current_user == null) {
            ActivityNavigation.Navigate(this, LoginActivity.class,true,false);
        } else {
            UserUtils.checkUserExistsAndFetchInfo(current_user.getUid(),this, (userModel, userExists) -> {
                if(userExists){
                   current_User=userModel;
                   profileFragment=new ProfileFragment(current_User);
                    friendsFragment=new Friends_Fragment(current_User);

                }
                else{
                    CustomToast.ShowCustomToast("Complete user profile first.","User not identified!","error",this);
                    ActivityNavigation.Navigate(this, User_info_activity.class,false,false);
                }
            });
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView=findViewById(R.id.btmNavBar);
        container=findViewById(R.id.container);


        fragmentManager=getSupportFragmentManager();
        feedFragment=new FeedFragment();
        fitFragment(feedFragment ,false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }







        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id=item.getItemId();
                if(item_id==R.id.feed){
                    if(feedFragment==null){
                        feedFragment=new FeedFragment();
                    }
                    fitFragment(feedFragment,true);
                } else if (item_id==R.id.post) {
                    if(createPostFragment==null){
                        createPostFragment=new CreatePostFragment(new OnCreatePostClickListener() {
                            @Override
                            public void onCreatePostClick(ArrayList<MediaUri> mediaUris, String post) {
                                Post(mediaUris,post);
                            }
                        }, new OnUploadMediaClickListener() {

                            @Override
                            public void uploadMediaClicked(OnMediaSelectedListener onMediaSelectedListener) {
                                mediaListener = onMediaSelectedListener;
                                getMedia();
                            }
                        });
                    }

                    fitFragment(createPostFragment,true);
                }
                else if (item_id==R.id.community) {
                    if(current_User!=null){
                        if(friendsFragment==null){
                            friendsFragment=new Friends_Fragment(current_User);

                        }
                        fitFragment(friendsFragment,true);

                    }else{
                        CustomToast.ShowCustomToast("Profile data is loading. Please wait for sometime.","Profile loading!","warning",getApplicationContext());
                        return  false;
                    }


                }else{
                    if(current_User==null){
                        CustomToast.ShowCustomToast("Profile data is loading! Please wait.","Profile loading!","warning",getApplicationContext());
                        return  false;
                    }
                    fitFragment(profileFragment,true);

                }

                return true;
            }
        });


   }



    private void fitFragment(Fragment fragment, boolean replace) {
        try {
            if(replace){
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                return;
            }
            fragmentManager.beginTransaction().add(R.id.container,fragment).commit();
        }catch (Exception e){
            Log.d("Fragments ",e.getLocalizedMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_nav_bar_menu, menu);
        return true;
    }


    private void getMedia() {
        if(requestPermission()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // To allow multiple selection
            startActivityForResult(Intent.createChooser(intent, "Select Media"), GALLERY_MEDIA_PICK_CODE);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_MEDIA_PICK_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) { // Multiple files selected
                ArrayList<MediaUri> mediaUris = new ArrayList<>();
                ClipData mediaData = data.getClipData();
                int count = mediaData.getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri mediaUri = mediaData.getItemAt(i).getUri();

                    // Check if file exists
                    if (isFileExists(mediaUri)) {
                        mediaUris.add(new MediaUri(mediaUri));
                        Log.i("MEDIA_INTAKE", "Multiple - " + mediaUri.toString());
                    } else {
                        Log.e("MEDIA_INTAKE", "File does not exist: " + mediaUri.toString());
                    }
                }
                mediaListener.OnMultipleMediaSelected(mediaUris);
            } else if (data.getData() != null) { // Single file selected
                Uri mediaUri = data.getData();

                // Check if file exists
                if (isFileExists(mediaUri)) {
                    mediaListener.OnSingleMediaSelected(mediaUri);
                    Log.i("MEDIA_INTAKE", "Single - " + mediaUri.toString());
                } else {
                    Log.e("MEDIA_INTAKE", "File does not exist: " + mediaUri.toString());
                }
            }
        }
    }
    private boolean isFileExists(Uri uri) {
        if (uri == null) return false;
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream != null) {
                inputStream.close(); // Close the stream after testing
                return true;
            }
        } catch (Exception e) {
            Log.e("MEDIA_INTAKE", "Error checking file existence: " + e.getMessage());
        }
        return false;
    }

    private void Post(ArrayList<MediaUri> mediaUris,String post) {



        if(post.replace(" ","").equals("") && mediaUris==null){
            CustomToast.ShowCustomToast("There is nothing to be posted!","Blank Post!","error",this);

        }
        else{
            FirebasePostController controller= new FirebasePostController(this);
            if(current_user==null){
                CustomToast.ShowCustomToast("Looks like user data is not loaded, please relogin","User identification failed!","error",this);
            return;
            }

            final Dialog[] dialog = {LoadingDialogue.createLoadingDialog(HomeActivity.this, "Posting! please wait.", R.raw.loading_lottie)};
            dialog[0].show();
            controller.Post(post,current_User,mediaUris, new OnPostListener() {
                @Override
                public void onPosted(String message) {
                    CustomToast.ShowCustomToast(message,"Post upload","success",HomeActivity.this);
                    createPostFragment.reset();
                    dialog[0].dismiss();
                }

                @Override
                public void onPostFailure(String error) {
                    CustomToast.ShowCustomToast(error,"Post failed!","error",HomeActivity.this);
                    dialog[0].dismiss();
                }

                @Override
                public void onPostProgress(int progress) {
                    dialog[0] = LoadingDialogue.createLoadingDialog(HomeActivity.this,"Posting media saving "+progress+"% please wait.",R.raw.loading_lottie);
                }
            });
        }

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
    private void showInContextUI(Activity activity, Boolean override) {
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
                        CustomToast.ShowCustomToast( "Permission denied, media features will be unavailable.","Permission denied","error",activity );
                    }
                })
                .create()
                .show();
    }


    //navigate to setting for permission grant
    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IMAGES_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getMedia();

                } else {
                    CustomToast.ShowCustomToast("Permission denied, media features will be unavailable.","Permission denied!", "error", this);
                    showInContextUI(this, true);
                }
                return;
        }

    }



    //Get media Type
    private String getMediaType(Uri uri) {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        String type = resolver.getType(uri);

        if (type != null) {
            if (type.startsWith("video")) {
                return "video";
            } else if (type.startsWith("image")) {
                return "image";
            }
        }
        return "unknown";
    }



}
