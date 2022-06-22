package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class PomodoroActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;

    TextView clock_text;
    TextView clock_state;

    Button start_stop;

    public CountDownTimer cdt = null;

    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        clock_text = findViewById(R.id.text_clock);
        start_stop  = findViewById(R.id.button_startstop);
        clock_state = findViewById(R.id.text_state);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.dlayout);
        navigationView = findViewById(R.id.navbar);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PomodoroActivity.this, drawerLayout, toolbar, R.string.open_navigation_menu, R.string.close_navigation_menu);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        start_stop.setOnClickListener(v -> {
//            ColorDrawable color = (ColorDrawable) start_stop.getBackground();
//            color.getColor();
//            System.out.println(color.getColor() + "   " + Color.GREEN);
            if ( ((ColorDrawable) start_stop.getBackground()).getColor() == Color.GREEN){
                if (start_stop.getText().equals("Start")){
                    clock_state.setText("Break");
                    // set up timer
                    cdt = new CountDownTimer(300000, 1000) { // 25 minute timer
                        public void onTick(long millisUntilFinished) {
                            long minutes = (millisUntilFinished / 1000)  / 60;
                            String stringSeconds;
                            int seconds = (int)((millisUntilFinished / 1000) % 60);
                            stringSeconds = String.valueOf(seconds);
                            if (seconds < 11) {
                                stringSeconds = '0' + stringSeconds;
                            }
                            clock_text.setText(minutes+":"+stringSeconds);
                        }
                        public void onFinish() {
                            start_stop.setText("Start");
                            // we will setup break timer
                            start_stop.setBackgroundColor(Color.RED);
                            clock_text.setText("25:00");
                            clock_state.setText("Work");

                        }
                    };
                    cdt.start();
                    start_stop.setText("Stop");

                } else if (start_stop.getText().equals("Stop")) {  // if startstop is STOP then we stop time
                    clock_state.setText("Break");
                    try {
                        cdt.cancel();
                    }catch(Exception e) {
                        //  we dont care if this doesnt work - if theres a countdown we will cancel
                    }
                    start_stop.setText("Start");
                    // we will setup break timer
                    start_stop.setBackgroundColor(Color.RED);
                    clock_text.setText("25:00");
                    clock_state.setText("Work");

                }
            } else {
            // if startstop is START then we start time
            if (start_stop.getText().equals("Start")){
                clock_state.setText("Work");
                // set up timer
                cdt = new CountDownTimer(1500000, 1000) { // 25 minute timer
                    public void onTick(long millisUntilFinished) {
                        long minutes = (millisUntilFinished / 1000)  / 60;
                        String stringSeconds;
                        int seconds = (int)((millisUntilFinished / 1000) % 60);
                        stringSeconds = String.valueOf(seconds);
                        if (seconds < 10) {
                            stringSeconds = '0' + stringSeconds;
                        }
                        clock_text.setText(minutes+":"+stringSeconds);
                    }
                    public void onFinish() {
                        start_stop.setText("Start");
                        // we will setup break timer
                        start_stop.setBackgroundColor(Color.GREEN);
                        clock_text.setText("5:00");
                        clock_state.setText("Break");

                    }
                };
                cdt.start();
                start_stop.setText("Stop");

            } else if (start_stop.getText().equals("Stop")) {  // if startstop is STOP then we stop time
                clock_state.setText("Work");
                try {
                    cdt.cancel();
                }catch(Exception e) {
                    //  we dont care if this doesnt work - if theres a countdown we will cancel
                }
                start_stop.setText("Start");
                // we will setup break timer
                start_stop.setBackgroundColor(Color.GREEN);
                clock_text.setText("5:00");
                clock_state.setText("Break");
            }}
    });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PomodoroActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.menu_home:
                startActivity(new Intent(PomodoroActivity.this, MainActivity.class));
                finish();
                return true;
            case R.id.menu_messages:
                startActivity(new Intent(PomodoroActivity.this, MessagesActivity.class));
                finish();
                return true;
            case R.id.menu_groups:
                startActivity(new Intent(PomodoroActivity.this, GroupsActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }
}