package com.example.poly.final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PatientForm extends Activity {

    protected static final String ACTIVITY_NAME = "PatientForm";
    Button patBackBtn;
    Button patSaveBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_form);

        patBackBtn = (Button) findViewById(R.id.patBack);
        patSaveBtn = (Button) findViewById(R.id.patSave);

        patBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientForm.this, ClinicActivity.class);
                startActivityForResult(intent, 50);
            }
        });




    }
}
