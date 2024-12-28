package com.example.social_app.authentication.EmailVerification;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.authentication.Activities.EmailVerificationActivity;
import com.example.social_app.social_Repo.Activites.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification {

    public static void handleEmailVerification(Activity activity ,Boolean silentCheck) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (user.isEmailVerified()) {
                    if (!silentCheck){
//                        CustomSnackbar.ShowSnackBar("Signup Successful and email Verified","success",activity);
                    }

                    ActivityNavigation.Navigate(activity, HomeActivity.class,true,true);
                } else {
                    if(silentCheck){
                        CustomToast.ShowCustomToast("Verify your email before proceeding","Email veification required","warn",activity);
                        ActivityNavigation.Navigate(activity,EmailVerificationActivity.class,false,true);
                    }else{
                        sendVerificationEmailAndNaviagte(activity);

                    }


                }

            }
        });
    }




    public static void sendVerificationEmailAndNaviagte(Activity parentActivity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Send verification email
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                CustomToast.ShowCustomToast("Verification email sent. Please check your inbox.","Email verification","success",parentActivity);
                                // Redirect to email verification page

                                ActivityNavigation.Navigate(parentActivity,EmailVerificationActivity.class,true,false);
                            } else {
                                CustomToast.ShowCustomToast("Failed to send verification email. Try Again ","Email verification failed!","error",parentActivity);
                            }
                        }
                    });
        }
    }
}
