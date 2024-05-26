package com.example.j_ridesphere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class availability_view extends AppCompatActivity {

    private TextView noCommutersTextView;
    private TextView nameTextView;
    private TextView destinationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_view);

        noCommutersTextView = findViewById(R.id.textView6);
        nameTextView = findViewById(R.id.nameTextView);
        destinationTextView = findViewById(R.id.destinationTextView);

        // Set OnClickListener to redirect to CurrentRideDetailsActivity
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(availability_view.this, current_ride_details.class);
                startActivity(intent);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail(); // Get the user's email ID
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("users").document(email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> userData = documentSnapshot.getData();
                                if (userData != null) {
                                    // Check if user is a driver or passenger
                                    if (userData.containsKey("driver's destination")) {
                                        Map<String, Object> driverDestination = (Map<String, Object>) userData.get("driver's destination");
                                        if (driverDestination != null) {
                                            String userDestination = (String) driverDestination.get("toDestination");
                                            findMatches(userDestination, true);
                                        }
                                    } else if (userData.containsKey("passanger's destination")) {
                                        Map<String, Object> passengerDestination = (Map<String, Object>) userData.get("passanger's destination");
                                        if (passengerDestination != null) {
                                            String userDestination = (String) passengerDestination.get("toDestination");
                                            findMatches(userDestination, false);
                                        }
                                    } else {
                                        Toast.makeText(availability_view.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(availability_view.this, "User data is null.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(availability_view.this, "Document snapshot not exists ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error fetching user document: " + e.getMessage());
                            Toast.makeText(availability_view.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void findMatches(String userDestination, boolean isDriver) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String queryField = isDriver ? "driver's destination.toDestination" : "passanger's destination.toDestination";

        firestore.collection("users")
                .whereEqualTo(queryField, userDestination)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // Skip current user's data
                            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if (userEmail != null && userEmail.equals(snapshot.getId())) {
                                continue;
                            }

                            if (isDriver) {
                                displayDriverData(snapshot);
                            } else {
                                displayPassengerData(snapshot);
                            }
                        }
                    } else {
                        noCommutersTextView.setVisibility(View.VISIBLE);
                        nameTextView.setVisibility(View.GONE);
                        destinationTextView.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(availability_view.this, "Failed to fetch matching users.", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error fetching matching users: " + e.getMessage());
                });
    }

    private void displayPassengerData(DocumentSnapshot documentSnapshot) {
        String passengerName = documentSnapshot.getString("information.info.firstName") + " " + documentSnapshot.getString("information.info.lastName");
        String passengerFromDestination = documentSnapshot.getString("passanger's destination.fromDestination");
        String passengerToDestination = documentSnapshot.getString("passanger's destination.toDestination");

        nameTextView.setText("Passenger's Name: " + passengerName);
        destinationTextView.setText("Destination: " + passengerFromDestination + " to " + passengerToDestination);
    }

    private void displayDriverData(DocumentSnapshot documentSnapshot) {
        String driverName = documentSnapshot.getString("information.info.firstName") + " " + documentSnapshot.getString("information.info.lastName");
        String driverFromDestination = documentSnapshot.getString("driver's destination.fromDestination");
        String driverToDestination = documentSnapshot.getString("driver's destination.toDestination");
        String fare = documentSnapshot.getString("driver's destination.fare");

        nameTextView.setText("Driver's Name: " + driverName);
        destinationTextView.setText("Destination: " + driverFromDestination + " to " + driverToDestination + ", Fare: $" + fare);
    }
}
