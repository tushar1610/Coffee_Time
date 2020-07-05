package com.example.android.coffeetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    Button reg_signUP;
    TextInputLayout fullNameEntered, usernameEntered, emailEntered, phoneEntered, passwordEntered;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        reg_signUP = findViewById(R.id.reg_signUP);
        fullNameEntered = findViewById(R.id.reg_name);
        usernameEntered = findViewById(R.id.reg_username);
        emailEntered = findViewById(R.id.reg_email);
        phoneEntered = findViewById(R.id.reg_phoneNo);
        passwordEntered = findViewById(R.id.reg_password);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        reg_signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }
    private void registerUser() {
        final String fullName = fullNameEntered.getEditText().getText().toString().trim();
        final String username = usernameEntered.getEditText().getText().toString().trim();
        final String email = emailEntered.getEditText().getText().toString().trim();
        final String phone = phoneEntered.getEditText().getText().toString().trim();
        String password = passwordEntered.getEditText().getText().toString().trim();

        if(fullName.isEmpty()){
            fullNameEntered.setError("Field cannot be empty");
            fullNameEntered.requestFocus();
            return;
        }

        if(username.isEmpty()){
            usernameEntered.setError("Field cannot be empty");
            usernameEntered.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEntered.setError("Field cannot be empty");
            emailEntered.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEntered.setError("Enter a valid email");
            emailEntered.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            phoneEntered.setError("Field cannot be empty");
            phoneEntered.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordEntered.setError("Field cannot be empty");
            passwordEntered.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    userHelper user = new userHelper(fullName, username, email, phone);

                    databaseReference.child(mAuth.getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendEmailVerification();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void sendEmailVerification(){
        mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Registration.this, "Successfully registered. Verification mail sent!!", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(Registration.this, dashboard.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Registration.this, "Verification mail hasn't been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
