package com.example.poly.final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Snapshot;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ClinicActivity extends Activity {

    protected static final String ACTIVITY_NAME = "ClinicActivity";

    Button btnDoctor;
    Button btnDentist;
    Button btnOptometrist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);
        Log.i(ACTIVITY_NAME, "In OnCreate() of ClinicActivity");

        btnDoctor = (Button) findViewById(R.id.docBtn);
        btnDentist = (Button) findViewById(R.id.denBtn);
        btnOptometrist = (Button) findViewById(R.id.optBtn);

        btnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicActivity.this, PatientForm.class);
                startActivityForResult(intent, 50);
                Log.i(ACTIVITY_NAME, "Doctor was selected");
            }
        });

        btnDentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicActivity.this, PatientForm.class);
                startActivityForResult(intent, 50);
                Log.i(ACTIVITY_NAME, "Dentist was selected");
            }
        });

        btnOptometrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicActivity.this, PatientForm.class);
                startActivityForResult(intent, 50);
                Log.i(ACTIVITY_NAME, "Optometrist was selected");
            }
        });

    }
}
