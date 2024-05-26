package com.example.j_ridesphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class role extends AppCompatActivity {
    CardView imageView , imageview2 ;
    ImageView imageView14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        imageView=(CardView) findViewById(R.id.create_ride);
        imageview2=(CardView) findViewById(R.id.search_ride);
        imageView14=(ImageView) findViewById(R.id.imageView8);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(role.this,Vehicle_info.class);
                startActivity(intent);
//                finish();
            }
        });
        imageView14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(role.this,profile.class);
                startActivity(intent);
//                finish();
            }
        });

        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(role.this,passanger_map_activity.class);
                startActivity(intent);
//                finish();
            }
        });
    }
}