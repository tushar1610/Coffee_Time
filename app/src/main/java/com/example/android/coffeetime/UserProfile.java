package com.example.android.coffeetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    EditText fullNameEntered, usernameEntered, emailEntered, phoneEntered, passwordEntered;
    Button update, showAllUsers, logOut, save;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    TextView pass;
    DatabaseReference databaseReference;
    String fullName, username, email, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullNameEntered = findViewById(R.id.user_fullName);
        usernameEntered = findViewById(R.id.user_username);
        emailEntered = findViewById(R.id.user_email);
        phoneEntered = findViewById(R.id.user_phone);
        passwordEntered = findViewById(R.id.update_password);
        update = findViewById(R.id.update);
        showAllUsers = findViewById(R.id.all_users);
        logOut = findViewById(R.id.log_out);
        save = findViewById(R.id.update_save);
        pass = findViewById(R.id.attribute5);
        pass.setVisibility(View.INVISIBLE);
        passwordEntered.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        firebaseUser = mAuth.getCurrentUser();

        showUserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pass.setVisibility(View.VISIBLE);
                passwordEntered.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });

        showAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, All_users.class);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(UserProfile.this, dashboard.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullName = fullNameEntered.getText().toString();
                username = usernameEntered.getText().toString();
                email = emailEntered.getText().toString();
                phone = phoneEntered.getText().toString();
                password = passwordEntered.getText().toString();

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

                updateUserInfo();
                updateUserPassword();

                Toast.makeText(getApplicationContext(), "Changes saved. Login to see changes!!", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(UserProfile.this, dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


    }

    private void updateUserInfo() {

        final userHelper user = new userHelper(fullName, username, email, phone);

        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(updateScreen.this, "Information changed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateUserPassword() {

        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
//                    Toast.makeText(updateScreen.this, "Password changed", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showUserData() {

        databaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHelper user = dataSnapshot.getValue(userHelper.class);
                fullNameEntered.setText(user.getName());
                usernameEntered.setText(user.getUsername());
                emailEntered.setText(user.getEmail());
                phoneEntered.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
