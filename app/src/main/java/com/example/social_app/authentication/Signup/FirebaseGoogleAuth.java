package com.example.social_app.authentication.Signup;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.social_app.R;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.authentication.EmailVerification.EmailVerification;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseGoogleAuth {
    private GoogleSignInClient mGoogleSignInClient;

    public static final int RC_SIGN_IN = 1001;
    Activity activity;
    FirebaseAuth mAuth;

    public FirebaseGoogleAuth(Activity activity) {
        this.activity=activity;
        this.mGoogleSignInClient = mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
       this.mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

       mAuth=FirebaseAuth.getInstance();

    }


    //signup

    public void sign_up_with_google(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            signIn();
        } else {
            revokeAccessAndSignIn();
        }
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
       activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void revokeAccessAndSignIn() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(activity, task ->
                        signIn());
    }



    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            EmailVerification.handleEmailVerification(activity,false);
                        } else {
                            Log.w(TAG, "Sign-in failed: ", task.getException());
                            CustomToast.ShowCustomToast("Sign-in failed, try again later.","Authentication failed!","error", activity);
                        }
                    }
                });
    }



}
