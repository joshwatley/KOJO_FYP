package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fyp_app.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button Register;
    private EditText textfirstname, textlastname, textemail, textpassword;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        textfirstname = findViewById(R.id.register_firstname);
        textlastname = findViewById(R.id.register_lastname);
        textemail = findViewById(R.id.register_email);
        textpassword = findViewById(R.id.register_password);
        progressBar = findViewById(R.id.progressBar);


        Register = findViewById(R.id.register_button);

        Register.setOnClickListener(v -> registerUser());
    }

    private void registerUser(){
        final String email = textemail.getText().toString().trim();
        final String firstname = textfirstname.getText().toString().trim();
        final String lastname = textlastname.getText().toString().trim();
        final String imageLoc = "";
        String password = textpassword.getText().toString().trim();

        if(email.isEmpty()){
            textemail.setError("Email is required!");
            textemail.requestFocus();
            return;
        }
        if(firstname.isEmpty()){
            textfirstname.setError("First Name is required!");
            textfirstname.requestFocus();
            return;
        }
        if(lastname.isEmpty()){
            textlastname.setError("Last Name is required!");
            textlastname.requestFocus();
            return;
        }
        if(password.isEmpty()){
            textpassword.setError("Password is required!");
            textpassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textemail.setError("Please ensure a valid email!");
            textemail.requestFocus();
            return;
        }
        if(password.length() < 6){
            textpassword.setError("Password should be more than 6 characters!");
            textpassword.requestFocus();
            return;
        }

        // need to make sure the emulator is connected to the internet
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Todo: Ensure this code works by connecting to internet and testing
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("email", email);
                        hashMap.put("firstname", firstname);
                        hashMap.put("lastname", lastname);
                        hashMap.put("imageLoc", "default");
                        User user = new User(userid, firstname, lastname, email, imageLoc);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Failed to register try again!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });


                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        }

}
