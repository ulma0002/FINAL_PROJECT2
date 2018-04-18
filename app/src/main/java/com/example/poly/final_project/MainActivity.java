package com.example.poly.final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    protected static final String ACTIVITY_NAME = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTransport = findViewById(R.id.Button1);
        Button buttonClinic    = findViewById(R.id.Button2);
        Button buttonMovies    = findViewById(R.id.Button3);
        Button buttonQuiz      = findViewById(R.id.Button4);

        buttonTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transportIntent = new Intent(MainActivity.this, TransportActivity.class);
                startActivity(transportIntent);
                Log.i(ACTIVITY_NAME, "In onCreate()");
            }
        });

        buttonClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clinicIntent = new Intent(MainActivity.this, ClinicActivity.class);
                startActivity(clinicIntent);
                Log.i(ACTIVITY_NAME, "In onCreate()");
            }
        });

        buttonMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moviesIntent = new Intent(MainActivity.this, MoviesActivity.class);
                startActivity(moviesIntent);
                Log.i(ACTIVITY_NAME, "In onCreate()");
            }
        });

        buttonQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quizIntent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(quizIntent);
                Log.i(ACTIVITY_NAME, "In onCreate()");
            }
        });

    }

        @Override
        public void onStart() {
            super.onStart();
            Log.i(ACTIVITY_NAME, "In onStart()");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.i(ACTIVITY_NAME, "In onResume()");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.i(ACTIVITY_NAME, "In onPause()");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(ACTIVITY_NAME, "In onStop()");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(ACTIVITY_NAME, "In onDestroy()");
        }
    }

