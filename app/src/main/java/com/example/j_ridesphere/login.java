package com.example.j_ridesphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView textView;
    Button button;
    EditText email, password;
    FirebaseAuth auth;
    String regex = "^(.+)@(.+)$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        textView=(TextView)findViewById(R.id.textView3);
        button = (Button)findViewById(R.id.button);
        email=(EditText)findViewById(R.id.editText2);
        password=(EditText)findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = password.getText().toString();

                if ((TextUtils.isEmpty(Email))){
                    Toast.makeText(login.this, "enter the Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(login.this, "enter the password", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(regex)) {
                    email.setError(" please enter valid email");
                } else if (password.length()<8) {
                    password.setError("minimum 8 digit required");
                    Toast.makeText(login.this, "password must be longer than 8 character", Toast.LENGTH_SHORT).show();
                    
                } else
                {
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                try {
                                    Intent intent= new Intent(login.this,role.class);
                                    startActivity(intent);
                                    finish();

                                }catch (Exception e){
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(login.this, "you dont have account", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(login.this, "you dont have account", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });
          //its for moving login to register page;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

    }

}