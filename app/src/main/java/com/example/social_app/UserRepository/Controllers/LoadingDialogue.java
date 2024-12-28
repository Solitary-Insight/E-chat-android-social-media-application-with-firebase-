package com.example.social_app.UserRepository.Controllers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.social_app.R;

public class LoadingDialogue {

    // Method to create the loading dialog
    public static Dialog createLoadingDialog(Context context,String heading, int jsonResource) {
        Dialog loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loadingdialog); // Set the custom layout
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background

       LottieAnimationView loadingAnimation = loadingDialog.findViewById(R.id.loading_animation);
       loadingAnimation.setAnimation(jsonResource);
        loadingAnimation.playAnimation();
       TextView head= loadingDialog.findViewById(R.id.dialogue_heading);
       head.setText(heading);
        loadingDialog.setCancelable(false); // Prevent dismissal by clicking outside
        return loadingDialog;
    }
}
