package com.example.j_ridesphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Vehicle_info extends AppCompatActivity {
    EditText vehicleMakeEditText, vehicleModelEditText, licensePlateEditText, availableSeatsEditText;
    RadioGroup vehicleTypeRadioGroup;
    Button submitButton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String regex = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        vehicleMakeEditText = findViewById(R.id.vehicleMakeEditText);
        vehicleModelEditText = findViewById(R.id.vehicleModelEditText);
        licensePlateEditText = findViewById(R.id.licensePlateEditText);
        availableSeatsEditText = findViewById(R.id.available_seats);
        vehicleTypeRadioGroup = findViewById(R.id.vehicleTypeRadioGroup);
        submitButton = findViewById(R.id.Sbutton);

        // Submit Button Click Listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVehicleInfo();

            }
        });
    }

    private void saveVehicleInfo() {
        String vehicleMake = vehicleMakeEditText.getText().toString().trim();
        String vehicleModel = vehicleModelEditText.getText().toString().trim();
        String licensePlate = licensePlateEditText.getText().toString().trim();
        String availableSeats = availableSeatsEditText.getText().toString().trim();

        int selectedRadioButtonId = vehicleTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String vehicleType = selectedRadioButton.getText().toString();

        // Get current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DocumentReference userRef = firestore.collection("users").document(email);

            // Create a new vehicle data map
            Map<String, Object> vehicleInfo = new HashMap<>();
            vehicleInfo.put("vehicleMake", vehicleMake);
            vehicleInfo.put("vehicleModel", vehicleModel);
            vehicleInfo.put("licensePlate", licensePlate);
            vehicleInfo.put("availableSeats", availableSeats);
            vehicleInfo.put("vehicleType", vehicleType);
//            if (!licensePlate.matches(regex)) {
//                licensePlateEditText.setError(" please enter valid license number");
//            }else{
//
//            }

            // Update the user's information in Firestore with vehicle data
            userRef.update("vehicle", vehicleInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Vehicle info saved successfully
                                Toast.makeText(Vehicle_info.this, "Vehicle info saved successfully.", Toast.LENGTH_SHORT).show();
                                // Redirect user to driver home page
                                startActivity(new Intent(Vehicle_info.this, Driver_Home_page.class));
                                finish(); // Finish the current activity
                            } else {
                                // Error saving vehicle info
                                Toast.makeText(Vehicle_info.this, "Failed to save vehicle info.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // User is not logged in
            Toast.makeText(Vehicle_info.this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

}
