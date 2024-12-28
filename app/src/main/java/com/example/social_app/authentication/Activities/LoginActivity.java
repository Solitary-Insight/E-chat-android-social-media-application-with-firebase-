package com.example.social_app.authentication.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.social_app.R;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.Utils.Common.FirebaseExceptions;
import com.example.social_app.authentication.EmailVerification.EmailVerification;
import com.example.social_app.authentication.Login.FirebaseEmailPasswordLogin;
import com.example.social_app.authentication.Signup.FirebaseEmailPasswordSignup;
import com.example.social_app.authentication.Signup.FirebaseGoogleAuth;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 234;

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupButton;
    private FloatingActionButton googlefab;
    FirebaseAuth mAuth;
    FirebaseEmailPasswordSignup firebaseEmailPasswordSignup;
    FirebaseGoogleAuth firebaseGoogleAuth;

    @Override
    public void onStart(){
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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        googlefab = findViewById(R.id.google_fab);

        firebaseGoogleAuth=new FirebaseGoogleAuth(this);
        firebaseEmailPasswordSignup=new FirebaseEmailPasswordSignup(this);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }


        googlefab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                firebaseGoogleAuth.sign_up_with_google();
            }
        });




        loginButton.setOnClickListener(v -> {
            String email = String.valueOf(emailEditText.getText());
            String password = String.valueOf(passwordEditText.getText());
            if(FirebaseEmailPasswordLogin.is_email_login_form_valid(email,password,LoginActivity.this)){
                FirebaseEmailPasswordLogin.LoginUsingEmailPassword(email,password,LoginActivity.this);
            }

        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigation.Navigate(LoginActivity.this,SignUpActivity.class,true,true);
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FirebaseGoogleAuth.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseGoogleAuth.firebaseAuthWithGoogle(account);
                } else {
                    CustomToast.ShowCustomToast("Sign-in failed, try again later.","Authentication failed1","error",this);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-in failed: " + e.getLocalizedMessage(), e);
                FirebaseExceptions.handleSignInError(e.getStatusCode(),this);
            }
        }
    }
}