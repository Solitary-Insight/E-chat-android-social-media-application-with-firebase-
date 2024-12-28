package com.example.social_app.authentication.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.social_app.R;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.Utils.Common.FirebaseExceptions;
import com.example.social_app.authentication.EmailVerification.EmailVerification;
import com.example.social_app.authentication.Signup.FirebaseEmailPasswordSignup;
import com.example.social_app.authentication.Signup.FirebaseGoogleAuth;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText emailSignup, passwordSignup, confirmPasswordSignup;
    private Button signupButtonSignup;
    TextView loginButtonSignup;
    private FloatingActionButton google_fab;
    FirebaseAuth mAuth;


    FirebaseEmailPasswordSignup firebaseEmailPasswordSignup;
    FirebaseGoogleAuth googleAuth;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            EmailVerification.handleEmailVerification(this,true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupButtonSignup = findViewById(R.id.signupButton);
        loginButtonSignup = findViewById(R.id.loginButton);
        google_fab = findViewById(R.id.google_fab);

        emailSignup=findViewById(R.id.emailEditText);
        passwordSignup=findViewById(R.id.passwordEditText);
        confirmPasswordSignup=findViewById(R.id.confirmPasswordEditText);

        firebaseEmailPasswordSignup=new FirebaseEmailPasswordSignup(this);
        googleAuth=new FirebaseGoogleAuth(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }

        signupButtonSignup.setOnClickListener(v -> {

            String email = String.valueOf(emailSignup.getText());
            String password = String.valueOf(passwordSignup.getText());
            String confirmPassword = String.valueOf(confirmPasswordSignup.getText());

            if(FirebaseEmailPasswordSignup.isSignupFormValid(email,password,confirmPassword,SignUpActivity.this)){
                firebaseEmailPasswordSignup.SignupUsingEmailPassword(email,password);
            }


        });

        loginButtonSignup.setOnClickListener(v -> ActivityNavigation.Navigate(this, LoginActivity.class,true,true));

        google_fab.setOnClickListener(view -> googleAuth.sign_up_with_google());
    }






            @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FirebaseGoogleAuth.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    googleAuth.firebaseAuthWithGoogle(account);
                } else {
                    CustomToast.ShowCustomToast("Sign-in failed, try again later.","Authentication failed!","error",this);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-in failed: " + e.getLocalizedMessage(), e);
                FirebaseExceptions.handleSignInError(e.getStatusCode(),this);
            }
        }
    }





}

