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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    String designationString = null;

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

        final SimpleDateFormat dbDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

        firedb.getReference().child("employee_designations").child(employee.getDesignation()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    employeeDesignation.setText(snapshot.child("name").getValue().toString());
                    designationString = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

            String timeFrom = null, timeTo = null;

            try {
                timeFrom = outputTimeFormat.format(inputTimeFormat.parse(employee.getShiftIn().getFrom()));
                timeTo = outputTimeFormat.format(inputTimeFormat.parse(employee.getShiftIn().getTo()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            shiftTime.setText(timeFrom + " - " + timeTo);
            timeOutBtn.setEnabled(true);
            timeInBtn.setEnabled(false);

        } else {
            // FROM SHIFTS
            // EMPLOYEE IS TIMED OUT
            selectedShift = (Shift) getIntent().getSerializableExtra("EMPLOYEE_SHIFT");

            employeeStatus.setText("Timed-out");
            employeeStatus.setTextColor(getResources().getColor(R.color.darkGrey));

            String timeFrom = null, timeTo = null;

            try {
                timeFrom = outputTimeFormat.format(inputTimeFormat.parse(selectedShift.getFrom()));
                timeTo = outputTimeFormat.format(inputTimeFormat.parse(selectedShift.getTo()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            shiftTime.setText(timeFrom + " - " + timeTo);
            timeOutBtn.setEnabled(false);
            timeInBtn.setEnabled(true);
        }

        timeInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!employee.isIn()) {

                    Date current = Calendar.getInstance().getTime();

                    Attendance attendance = new Attendance(
                            dbDateFormat.format(current),
                            employee.getFname(),
                            employee.getLname(),
                            designationString,
                            employee.hasImage() ? employee.getImage_url() : null,
                            branch.getName(),
                            branch.getDescription(),
                            inputTimeFormat.format(current),
                            null,
                            selectedShift
                    );

                    firedb.getReference().child("active_attendance").child(dbDateFormat.format(current)).child(branch.getKey()).child(employee.getKey()).setValue(attendance)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(ActionsActivity.this, SuccessActivity.class);
                                    employee.setShiftIn(selectedShift);
                                    intent.putExtra("EMPLOYEE", employee);
                                    intent.putExtra("BRANCH", branch);
                                    intent.putExtra("ACTION", "Timed-in");
                                    startActivity(intent);
                                }
                            });

                } else {
                    new AlertDialog.Builder(ActionsActivity.this)
                            .setMessage("You are already currently timed-in.")
                            .setPositiveButton("Okay", null)
                            .show();
                }
            }
        });

        timeOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date current = Calendar.getInstance().getTime();

                final DatabaseReference ref = firedb.getReference().child("active_attendance").child(dbDateFormat.format(current)).child(branch.getKey()).child(employee.getKey());

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Attendance attendance = snapshot.getValue(Attendance.class);
                            attendance.setTimed_out(inputTimeFormat.format(current));

                            firedb.getReference().child("attendance_log").child(dbDateFormat.format(current)).child(branch.getKey()).child(employee.getKey()).setValue(attendance)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            ref.removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            employee.setShiftIn(null);
                                                            Intent intent = new Intent(ActionsActivity.this, SuccessActivity.class);
                                                            intent.putExtra("EMPLOYEE", employee);
                                                            intent.putExtra("BRANCH", branch);
                                                            intent.putExtra("ACTION", "Timed-out");
                                                            startActivity(intent);

                                                        }
                                                    });

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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