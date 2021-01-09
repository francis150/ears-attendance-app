package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuccessActivity extends AppCompatActivity {

    CircleImageView employeeImage;
    TextView employeeName, employeeDesignation, employeeStatus, messageText;
    Button confirmButton;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    EmployeeBasic employee;
    Branch branch;
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        employeeImage = (CircleImageView) findViewById(R.id.success_profile_image);
        employeeName = (TextView) findViewById(R.id.success_employee_name);
        employeeDesignation = (TextView) findViewById(R.id.success_employee_designation);
        employeeStatus = (TextView) findViewById(R.id.success_employee_status);
        messageText = (TextView) findViewById(R.id.success_text);
        confirmButton = (Button) findViewById(R.id.success_button);

        employee = (EmployeeBasic) getIntent().getSerializableExtra("EMPLOYEE");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");
        action = getIntent().getStringExtra("ACTION");

        employeeName.setText(employee.getLname() + ", " + employee.getFname());
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
        messageText.setText("Successfully " + action + "!");
        if (employee.hasImage()) {
            Picasso.get().load(employee.getImage_url()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.color.lightGrey).into(employeeImage, new Callback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(employee.getImage_url()).placeholder(R.color.lightGrey).into(employeeImage);
                }
            });
        }

        if (employee.isIn()) {
            employeeStatus.setText("Timed-in");
            employeeStatus.setTextColor(getResources().getColor(R.color.blue));
        } else {
            employeeStatus.setText("Timed-out");
            employeeStatus.setTextColor(getResources().getColor(R.color.darkGrey));
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, ScanActivity.class);
                intent.putExtra("BRANCH", branch);
                startActivity(intent);
            }
        });
    }
}