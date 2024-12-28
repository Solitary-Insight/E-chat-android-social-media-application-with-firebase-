package com.example.social_app.Utils.Common;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.CommonStatusCodes;

public class FirebaseExceptions {

  public static   void handleSignInError(int statusCode, Activity activity) {
        String errorMessage;
        switch (statusCode) {
            case CommonStatusCodes.NETWORK_ERROR:
                errorMessage = "Network error. Please check your connection.";
                break;
            case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                errorMessage = "Sign-in failed. Please try again later.";
                break;
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                errorMessage = "Sign-in canceled. Please try again.";
                break;
            case GoogleSignInStatusCodes.INVALID_ACCOUNT:
                errorMessage = "Invalid account. Please check and try again.";
                break;
            default:
                errorMessage = "An unknown error occurred. Please try again.";
                break;
        }
      Log.e(TAG,"ERR-MESSAGE: "+errorMessage);
      Log.e(TAG,"ERR-MESSAGE: "+statusCode);


        // Display the error message as a Toast
        CustomToast.ShowCustomToast(errorMessage,"Firebase error!","error", activity);

    }
}
