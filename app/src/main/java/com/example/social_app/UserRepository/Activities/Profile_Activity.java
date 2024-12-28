package com.example.social_app.UserRepository.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_app.social_Repo.Activites.HomeActivity;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.UserUtils;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile_Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile2);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();
        if (current_user == null) {
            finish();
        } else {
            UserUtils.checkUserExistsAndFetchInfo(current_user.getUid(),this, (userModel, userExists) -> {
                if(userExists){
                   setProfileFields(userModel);
                }
            });
        }


    }
// if user exist then load his Data
    private void setProfileFields(UserModel userModel) {
        // Get views by ID
        ImageView userImage = findViewById(R.id.user_image);
        TextView usernameHead = findViewById(R.id.username_head);
        TextView bio = findViewById(R.id.bio);
        TextView username = findViewById(R.id.username);
//        TextView gender = findViewById(R.id.gender);
//        TextView age = findViewById(R.id.age);
//        TextView dob = findViewById(R.id.dob);
        TextView mobile = findViewById(R.id.mobile);
        TextView email = findViewById(R.id.email);


        // Set the values based on userModel data for each TextView

        usernameHead.setText(userModel.getUsername()); // Username for the usernameHead TextView
        bio.setText(userModel.getBio()); // Bio for the bio TextView
        username.setText(userModel.getUsername()); // Username for the username TextView
         // Mobile number for the mobile TextView
        email.setText(userModel.getEmail()); // Email for the email TextView
        mobile.setText(userModel.getPhone());
        userImage.setImageResource(R.drawable.baseline_person_24);



       Glide.with(this).load(userModel.getProfilePictureUrl())  // URL or URI of the image
                .apply(RequestOptions.centerCropTransform())
                .override(200, 200) // Set the width and height for cropping
                .placeholder(R.drawable.baseline_person_24)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(userImage);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection.
        int id = item.getItemId();
        if (id == R.id.logout_menu_button) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        ActivityNavigation.Navigate(this, HomeActivity.class, true, true);

    }



}
