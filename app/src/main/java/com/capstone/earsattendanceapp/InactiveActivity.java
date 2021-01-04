package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class InactiveActivity extends AppCompatActivity {

    CircleImageView employeeImage;
    TextView employeeName, employeeDesignation;

    EmployeeBasic employee;
    Branch branch;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive);

        employeeImage = (CircleImageView) findViewById(R.id.inactive_profile_image);
        employeeName = (TextView) findViewById(R.id.inactive_employee_name);
        employeeDesignation = (TextView) findViewById(R.id.inactive_employee_designation);

        employee = (EmployeeBasic) getIntent().getSerializableExtra("EMPLOYEE");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");

        // SET employee data to ui
        employeeName.setText(employee.getLname() + ", " + employee.getFname());
        if (employee.hasImage()) {

            Picasso.get().load(employee.getImage_url()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.color.lightGrey).into(employeeImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(employee.getImage_url()).placeholder(R.color.lightGrey).into(employeeImage);
                }
            });

        }

        firedb.getReference().child("employee_designations").child(employee.getDesignation()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    employeeDesignation.setText(snapshot.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InactiveActivity.this, ScanActivity.class);
        intent.putExtra("BRANCH", branch);
        startActivity(intent);
    }
}