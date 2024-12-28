package com.example.social_app.authentication.Login;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.authentication.EmailVerification.EmailVerification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseEmailPasswordLogin {
    public  static Boolean  is_email_login_form_valid(String email, String password, Activity activity){
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            CustomToast.ShowCustomToast("Both fields are required for loginig in.","Fields missing!","error",activity);
            return false;
        }
        return true;
    }

    public static void LoginUsingEmailPassword(String email, String password,Activity activity) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
//                            ActivityNavigation.Navigate(activity,HomeActivity.class,true,true);
                            CustomToast.ShowCustomToast("You successfully logged in the app.","Authenticated!","success",activity);
                            EmailVerification.handleEmailVerification(activity,true);

                        } else {

                            CustomToast.ShowCustomToast("Authentication failed. Please try again later.","Authentication failed!","error",activity);
                        }
                    }
                });
    }
}
