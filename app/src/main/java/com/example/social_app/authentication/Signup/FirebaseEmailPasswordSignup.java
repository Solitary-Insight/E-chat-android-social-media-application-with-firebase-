package com.example.social_app.authentication.Signup;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.authentication.EmailVerification.EmailVerification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseEmailPasswordSignup {
    Activity activity;;
    FirebaseAuth firebaseAuth;

    public FirebaseEmailPasswordSignup(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    //    check if required fields are filled
   public static boolean isSignupFormValid(String email, String password, String confirmPassword, Context context){

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {

            CustomToast.ShowCustomToast("All fields are required for signing up.","Fields missing!","warn",context);
            return false;

        } else if (!password.equals(confirmPassword)) {

            CustomToast.ShowCustomToast("Passwords do not match","Password missmatched!","warn",context);
            return false;

        } else if (password.length() < 6) {

            CustomToast.ShowCustomToast("Passwords should be greater than 6 characters","Short password","error",context);
            return false;
        } else {
        return true;
        }
    }


    // signup using email password

    public  void SignupUsingEmailPassword(String email, String password){

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                CustomToast.ShowCustomToast("Your account has been created on the app.","Account created","success",activity);
                                EmailVerification.handleEmailVerification(activity,false);
                            } else {
                                CustomToast.ShowCustomToast("Authentication failed! please try again.","Authentication failed!","error",activity);
                            }
                        }
                    });
        }
        catch (Exception e){
            Log.d("TAG", "SignupUsingEmailPassword: "+e.getMessage());
            CustomToast.ShowCustomToast("Authentication failed! please try again","Authentication failed!","error",activity);
        }

    }


}
