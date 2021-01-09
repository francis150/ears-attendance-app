package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoadActivity extends AppCompatActivity {

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    Branch branch;
    EmployeeBasic employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        final SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat forDbFormat = new SimpleDateFormat("dd-MM-yyyy");

        // CURRENT DATE TIME
        final Date current = Calendar.getInstance().getTime();

        final String path = getIntent().getStringExtra("EMPLOYEE_PATH");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");
        employee = new EmployeeBasic();

        firedb.getReference().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // Create employee object
                    employee.setKey(snapshot.child("key").getValue().toString());
                    employee.setFname(snapshot.child("fname").getValue().toString());
                    employee.setLname(snapshot.child("lname").getValue().toString());
                    employee.setDesignation(snapshot.child("designation").getValue().toString());

                    if (snapshot.child("image_url").exists()) {employee.setImage_url(snapshot.child("image_url").getValue().toString());}
                    if (snapshot.child("deactivated_by").exists()) {employee.setDeactivated_by(snapshot.child("deactivated_by").getValue().toString());}


                    firedb.getReference().child("attendance").child(forDbFormat.format(current)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                int i1 = 0, i2 = 0;
                                for (DataSnapshot branchData : snapshot.getChildren()) {
                                    i1++;
                                    for (DataSnapshot attendanceData: branchData.getChildren()) {
                                        i2++;
                                        Attendance attendance = attendanceData.getValue(Attendance.class);

                                        Date timeTo = Calendar.getInstance().getTime();
                                        Date timeCurrent = Calendar.getInstance().getTime();

                                        try {
                                            timeCurrent = inputTimeFormat.parse(inputTimeFormat.format(current));
                                            timeTo = inputTimeFormat.parse(attendance.getShift().getTo());
                                        } catch (ParseException e) {e.printStackTrace();}

                                        long diffMin = (timeCurrent.getTime() - timeTo.getTime()) / 60000;

                                        if (attendance.getTimed_out() == null && diffMin < 30) {
                                            //ACTIVE
                                            employee.setShiftIn(attendance.getShift());
                                        }

                                        if (i1 == snapshot.getChildrenCount() && i2 == branchData.getChildrenCount()) { redirect(); }

                                    }
                                }

                            } else { redirect(); }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoadActivity.this, "Something went wrong. Please restart the app.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void redirect() {

        if (employee.isDeactivated()) {
            // EMPLOYEE INACTIVE
            Intent intent = new Intent(LoadActivity.this, InactiveActivity.class);
            intent.putExtra("EMPLOYEE", employee);
            intent.putExtra("BRANCH", branch);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
            startActivity(intent, options.toBundle());
        } else if (employee.isIn()) {
            // EMPLOYEE IN
            Intent intent = new Intent(LoadActivity.this, ActionsActivity.class);
            intent.putExtra("EMPLOYEE", employee);
            intent.putExtra("BRANCH", branch);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
            startActivity(intent, options.toBundle());
        } else {
            // EMPLOYEE OUT
            Intent intent = new Intent(LoadActivity.this, ShiftsActivity.class);
            intent.putExtra("EMPLOYEE", employee);
            intent.putExtra("BRANCH", branch);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
            startActivity(intent, options.toBundle());
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(LoadActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}