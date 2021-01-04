package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActionsActivity extends AppCompatActivity {

    CircleImageView employeeImage;
    TextView employeeName, employeeDesignation, employeeStatus, shiftTime;
    Button timeInBtn, timeOutBtn, activityLogBtn;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    EmployeeBasic employee;
    Shift selectedShift;
    Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        employeeImage = (CircleImageView) findViewById(R.id.actions_profile_image);
        employeeName = (TextView) findViewById(R.id.actions_employee_name);
        employeeDesignation = (TextView) findViewById(R.id.actions_employee_designation);
        employeeStatus = (TextView) findViewById(R.id.actions_employee_status);
        shiftTime = (TextView) findViewById(R.id.actions_shift_time);
        timeInBtn = (Button) findViewById(R.id.actions_timein_btn);
        timeOutBtn = (Button) findViewById(R.id.actions_timeout_btn);
        activityLogBtn = (Button) findViewById(R.id.actions_activitylog_btn);

        employee = (EmployeeBasic) getIntent().getSerializableExtra("EMPLOYEE");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");


        // SET PROFILE UI
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

        // SET STATUS SETTINGS
        if (employee.isIn()) {
            // FROM SCAN
            // EMPLOYEE IS TIEMD IN
            employeeStatus.setText("Timed-in");
            employeeStatus.setTextColor(getResources().getColor(R.color.blue));

            shiftTime.setText(employee.getShiftIn().getFrom() + " - " + employee.getShiftIn().getTo());
            timeOutBtn.setEnabled(true);
            timeInBtn.setEnabled(false);

        } else {
            // FROM SHIFTS
            // EMPLOYEE IS TIMED OUT
            selectedShift = (Shift) getIntent().getSerializableExtra("EMPLOYEE_SHIFT");

            employeeStatus.setText("Timed-out");
            employeeStatus.setTextColor(getResources().getColor(R.color.darkGrey));

            shiftTime.setText(selectedShift.getFrom() + " - " + selectedShift.getTo());
            timeOutBtn.setEnabled(false);
            timeInBtn.setEnabled(true);
        }


        // BUTTONS
        timeInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (!employee.isIn()) {

                    SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");

                    // CURRENT DATE TIME
                    Date current = Calendar.getInstance().getTime();

                    // SELECTED SHIFT TIME
                    Date timeFrom = Calendar.getInstance().getTime();
                    Date timeTo = Calendar.getInstance().getTime();
                    Date timeCurrent = Calendar.getInstance().getTime();
                    try {
                        timeCurrent = inputTimeFormat.parse(inputTimeFormat.format(current));
                        timeFrom = inputTimeFormat.parse("24:02");
                        timeTo = inputTimeFormat.parse(selectedShift.getTo());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long diffMin = (timeCurrent.getTime() - timeFrom.getTime()) / 60000;

                    if (diffMin < -5) {

                        // if the employee is early for more than 5mins.
                        new AlertDialog.Builder(ActionsActivity.this)
                                .setMessage("You are " + String.valueOf(Math.abs(diffMin)) + "min(s) early for this shift. Are you sure you want to time-in now?")
                                .setPositiveButton("Time-in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Time-in early.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();

                    } else if (diffMin >= -5 && diffMin <= 0) {

                        // if the employee is early for less than 5mins.
                        new AlertDialog.Builder(ActionsActivity.this)
                                .setMessage("You are just in time for this shift. Confirm to time-in.")
                                .setPositiveButton("Time-in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Time-on normal.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();

                    } else if (diffMin > 0) {

                        // if employee is late
                        new AlertDialog.Builder(ActionsActivity.this)
                                .setMessage("You are " + String.valueOf(diffMin) + "min(s) late for this shift. Are you sure you want to time-in?")
                                .setPositiveButton("Time-in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Time-in late.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ActionsActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();

                    }

                } else {
                    new AlertDialog.Builder(ActionsActivity.this)
                            .setMessage("You are currently Timed-in.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    
                                }
                            }).show();
                }
            }
        });


    }




    @Override
    public void onBackPressed() {
        Intent intent;

        if (employee.isIn()) {
            intent = new Intent(ActionsActivity.this, ScanActivity.class);
            intent.putExtra("BRANCH", branch);
        } else {
            intent = new Intent(ActionsActivity.this, ShiftsActivity.class);
            intent.putExtra("BRANCH", branch);
            intent.putExtra("EMPLOYEE", employee);
        }

        startActivity(intent);
    }
}