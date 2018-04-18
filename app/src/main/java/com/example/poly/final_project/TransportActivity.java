package com.example.poly.final_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.poly.final_project.transport.BusStopFragment;
import com.example.poly.final_project.transport.RoutesFragment;

public class TransportActivity extends AppCompatActivity {

    Button bus_stops_btn;
    Button routes_btn;
    FrameLayout fragment_holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        bus_stops_btn = (Button) findViewById(R.id.bus_stops_btn);
        routes_btn = (Button) findViewById(R.id.routes_btn);
        fragment_holder = (FrameLayout) findViewById(R.id.fragment_holder);

        bus_stops_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_holder, new BusStopFragment());
                ft.commit();
            }
        });

        routes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_holder, new RoutesFragment());
                ft.commit();
            }
        });

    }
}
