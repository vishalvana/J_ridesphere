package com.example.j_ridesphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class passanger_bottom_sheet extends BottomSheetDialogFragment {

    private EditText fromDesEditText, toDesEditText;
    private Button findRideButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_passanger_bottom_sheet, container, false);

        fromDesEditText = view.findViewById(R.id.from_des);
        toDesEditText = view.findViewById(R.id.TO_des);
        findRideButton = view.findViewById(R.id.Find_ride);

        findRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePassengerData();
            }
        });

        return view;
    }

    private void savePassengerData() {
        String fromDes = fromDesEditText.getText().toString().trim();
        String toDes = toDesEditText.getText().toString().trim();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail(); // Get the user's email ID
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Create a map to hold the passenger data
            Map<String, Object> passengerData = new HashMap<>();
            passengerData.put("fromDestination", fromDes);
            passengerData.put("toDestination", toDes);

            // Save the data to Firestore under the user's ID
            firestore.collection("users")
                    .document(email)
                    .update("passanger's destination", passengerData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Passenger data saved.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), availability_view.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to save passenger data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}
