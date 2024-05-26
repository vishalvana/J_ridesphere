package com.example.j_ridesphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText, passwordEditText;
    CheckBox termsCheckBox;
    Button registerButton;
    FirebaseAuth firebaseAuth;
    TextView lgbtn;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        lgbtn=(TextView)findViewById(R.id.already);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.Last_name);
        emailEditText = findViewById(R.id.Email_signin);
        phoneEditText = findViewById(R.id.phone_signin);
        passwordEditText = findViewById(R.id.password_signin);
        termsCheckBox = findViewById(R.id.checkBox);
        registerButton = findViewById(R.id.button);

        // Register Button Click Listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        lgbtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(Register.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean termsAccepted = termsCheckBox.isChecked();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || !termsAccepted) {
            Toast.makeText(this, "Please fill all fields and accept terms.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user with Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registered successfully, add user info to Firestore
                            addUserToFirestore(firstName, lastName, email, phone);
                        } else {
                            // Registration failed
                            Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToFirestore(String firstName, String lastName, String email, String phone) {
        // Create a new user document in Firestore
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("email", email);
        userInfo.put("phone", phone);

        // Create a subcollection for user information
        Map<String, Object> userInformation = new HashMap<>();
        userInformation.put("info", userInfo);

        // Create a subcollection for user history
        Map<String, Object> userHistory = new HashMap<>();
        // Add any initial history data if needed

        // Create a map to hold both subcollections
        Map<String, Object> userData = new HashMap<>();
        userData.put("information", userInformation);
        userData.put("history", userHistory);

        // Add the user data map to Firestore under the user's document
        firestore.collection("users").document(email)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // User data added to Firestore successfully
                            Toast.makeText(Register.this, "User registered successfully.", Toast.LENGTH_SHORT).show();

                            // Redirect user to login or home screen
                            startActivity(new Intent(Register.this, login.class));
                            finish();
                        } else {
                            // Error adding user data to Firestore
                            Toast.makeText(Register.this, "Failed to add user data to Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
