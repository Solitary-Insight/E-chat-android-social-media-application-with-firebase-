package com.example.social_app.authentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.social_app.R;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button btnCheckVerification, btnResendEmail, btntryAnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Initialize Firebase Auth and User
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Reference UI components
        btnCheckVerification = findViewById(R.id.btnCheckVerification);
        btnResendEmail = findViewById(R.id.btnResendEmail);


        // try another email
        btntryAnother=findViewById(R.id.try_other_account);

        btntryAnother.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            ActivityNavigation.Navigate(this,LoginActivity.class,true,true);
        });

        // Check Email Verification Status
        btnCheckVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUser.isEmailVerified()) {
                            Toast.makeText(EmailVerificationActivity.this, "Email verified! You can now log in.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EmailVerificationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EmailVerificationActivity.this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }

        // Resend Verification Email
        btnResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    currentUser.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EmailVerificationActivity.this, "Verification email resent. Check your inbox.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmailVerificationActivity.this, "Failed to resend email. Try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(EmailVerificationActivity.this, "User not logged in. Please sign in again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
