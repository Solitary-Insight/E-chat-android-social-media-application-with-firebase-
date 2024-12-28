package com.example.social_app.UserRepository.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.authentication.Activities.LoginActivity;
import com.example.social_app.social_Repo.Activites.Post_detail_activity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;


public class ProfileFragment extends Fragment {


View layoutview;
UserModel current_user;
MaterialCardView logout_card;
    LottieAnimationView animationView;

    public ProfileFragment(UserModel currentUser) {

        this.current_user=currentUser;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutview=inflater.inflate(R.layout.fragment_profile, container, false);
        logout_card=layoutview.findViewById(R.id.logout_card);
        animationView=layoutview.findViewById(R.id.edit_lottie);
        logout_card.setOnClickListener(view -> {
        logout();
        });
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToEditPage();
            }
        });
        return layoutview;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProfileFields(current_user,layoutview);


    }


    void  logout(){
        FirebaseAuth.getInstance().signOut();
        ActivityNavigation.Navigate(getActivity(), LoginActivity.class,true,true);
    }



    private  void navigateToEditPage(){

        Intent intent = new Intent(getActivity(), User_info_activity.class);
        String userJson = new Gson().toJson(current_user);
        intent.putExtra("CURRENT_USER", userJson);
        getActivity().startActivity(intent);
    }


    // if user exist then load his Data
    private void setProfileFields(UserModel userModel,View view) {

        // Get views by ID
        ShapeableImageView userImage = view.findViewById(R.id.user_image);
        TextView usernameHead =  view.findViewById(R.id.username_head);
        TextView bio =  view.findViewById(R.id.bio);
        TextView username = view. findViewById(R.id.username);

        TextView mobile =  view.findViewById(R.id.mobile);
        TextView email = view. findViewById(R.id.email);


        // Set the values based on userModel data for each TextView

        usernameHead.setText(userModel.getUsername()); // Username for the usernameHead TextView
        bio.setText(userModel.getBio()); // Bio for the bio TextView
        username.setText(userModel.getUsername()); // Username for the username TextView
        // Mobile number for the mobile TextView
        email.setText(userModel.getEmail()); // Email for the email TextView
        mobile.setText(userModel.getPhone());
        Glide.with(this).load(userModel.getProfilePictureUrl())  // URL or URI of the image
                                .transform(new CircleCrop())
                .placeholder(R.drawable.user)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(userImage);





    }
}