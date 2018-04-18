package com.example.poly.final_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_holder, new BusStopFragment());
                ft.commit();
            }
        });

        routes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_holder, new RoutesFragment());
                ft.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(getString(R.string.octranspoapp));
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.octranspo_menu, menu);
        return true;
    }

    private void showHelp(){
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Help")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).setMessage("Author: Sohaila Ridwan\nVersion: 1.1\nUser guide: Click 'Bus Stops' and select 'ADD BUS STOP' button to enter bus stop number which you want to get information about. Then select 'ROUTS' to see detail information of the bus routs of that bus stop. Click on the stop number with bus route from the list to see trip detail including gps speed. Thank you!").show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
