package com.example.fyp_app;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private TextView Register;
    private EditText email, password;

    private Button login;
    private Button forcelogin;
    private Button forcelogin2;

    FirebaseUser firebaseUser;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        // Creating events

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginTextEmail);
        password = findViewById(R.id.loginTextPassword);
        Register = findViewById(R.id.login_register);
        login = findViewById(R.id.loginButton);

        forcelogin = findViewById(R.id.forcelogin);
        forcelogin2 = findViewById(R.id.forcelogin2);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "All fields are needed", Toast.LENGTH_SHORT).show();
                } else{
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Auth has failed", Toast.LENGTH_LONG);
                                    }
                                }
                            });
                }
            }
        });


        Register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forcelogin.setOnClickListener(v -> {
          email.setText("testaccount@gmail.com");
          password.setText("");

        });

        forcelogin2.setOnClickListener(v -> {
          email.setText("testaccount3@gmail.com");
          password.setText("");

        });


    }
}
