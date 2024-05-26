package com.example.j_ridesphere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Driver_bottom_sheet_Fragment extends BottomSheetDialogFragment {

    private EditText fromDesEditText, toDesEditText, fareEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_bottom_sheet_, container, false);

        fromDesEditText = view.findViewById(R.id.from_des);
        toDesEditText = view.findViewById(R.id.TO_des);
        fareEditText = view.findViewById(R.id.fare);

        Button findRideButton = view.findViewById(R.id.Find_ride);
        findRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Find Ride button
                saveDestinationData();
            }
        });

        return view;
    }

    private void saveDestinationData() {
        // Get the destination data from EditText fields
        String fromDes = fromDesEditText.getText().toString().trim();
        String toDes = toDesEditText.getText().toString().trim();
        String fare = fareEditText.getText().toString().trim();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail(); // Get the user's email ID

            // Get the Firestore instance
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Create a map to hold the destination data
            Map<String, Object> destinationData = new HashMap<>();
            destinationData.put("fromDestination", fromDes);
            destinationData.put("toDestination", toDes);
            destinationData.put("fare", fare);

            // Update the destination data in the user's document using their email ID
            firestore.collection("users").document(email)
                    .update(" driver's destination", destinationData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data updated successfully
                            Toast.makeText(getActivity(), "Destination data updated.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), availability_view.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error updating data
                            Log.e("Firestore", "Error updating destination data: " + e.getMessage());
                            Toast.makeText(getActivity(), "Failed to update destination data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // User is not logged in
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }


    }
}
