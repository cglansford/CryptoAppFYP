package com.example.cryptoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mUsername, mPassword, mPasswordCheck;
    Button mRegisterBTN;
    TextView mLoginBTN;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mPasswordCheck = findViewById(R.id.passwordCheck);
        mRegisterBTN = findViewById(R.id.registerBTN);
        mLoginBTN = findViewById(R.id.createAccount);

        fAuth = FirebaseAuth.getInstance();

        mRegisterBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordCheck = mPasswordCheck.getText().toString().trim();



                if(TextUtils.isEmpty(username)){
                    mUsername.setError("Username is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6 ){
                    mPassword.setError("Password must have 6 or more characters");
                    return;
                }

                if(!password.equalsIgnoreCase(passwordCheck)){
                    mPassword.setError("Passwords must match");
                    return;
                }

                //register the user in firebase
                fAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(Register.this, "Account Created. ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }else {
                            Toast.makeText(Register.this, "Error !. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //Go to login Page
        mLoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
}
