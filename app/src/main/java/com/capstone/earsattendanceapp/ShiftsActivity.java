package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShiftsActivity extends AppCompatActivity {

    TextView employeeName, employeeDesignation, shiftsPlaceholderText;
    CircleImageView employeeImage;
    RecyclerView shiftsRecyclerView;

    Calendar calendar = Calendar.getInstance();

    FirebaseDatabase  firedb = FirebaseDatabase.getInstance();

    FirebaseRecyclerOptions<Shift> shiftRecyclerOptions;
    FirebaseRecyclerAdapter<Shift, ShiftViewHolder> shiftAdapter;

    EmployeeBasic employee;
    Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);

        Date date = calendar.getTime();
        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

        employeeImage = (CircleImageView) findViewById(R.id.shifts_profile_image);
        employeeName = (TextView) findViewById(R.id.shifts_employee_name);
        employeeDesignation = (TextView) findViewById(R.id.shifts_employee_designation);
        shiftsRecyclerView = (RecyclerView) findViewById(R.id.shifts_recycler_view);
        shiftsRecyclerView.setLayoutManager(new LinearLayoutManager(ShiftsActivity.this));
        shiftsPlaceholderText = (TextView) findViewById(R.id.shifts_placeholder_text);

        employee = (EmployeeBasic) getIntent().getSerializableExtra("EMPLOYEE");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");

        // Set profile ui
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

        Query shiftsReference = firedb.getReference().child("employees").child(employee.getKey()).child("shifts").child(dayName.toLowerCase()).orderByChild("branch_key").equalTo(branch.getKey());
        shiftRecyclerOptions = new FirebaseRecyclerOptions.Builder<Shift>().setQuery(shiftsReference, Shift.class).build();
        shiftAdapter = new FirebaseRecyclerAdapter<Shift, ShiftViewHolder>(shiftRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder holder, int position, @NonNull final Shift model) {

                shiftsPlaceholderText.setVisibility(View.GONE);

                holder.itemView.setAnimation(AnimationUtils.loadAnimation(ShiftsActivity.this, R.anim.fade_in_long));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShiftsActivity.this, ActionsActivity.class);
                        intent.putExtra("EMPLOYEE", employee);
                        intent.putExtra("EMPLOYEE_SHIFT", model);
                        intent.putExtra("BRANCH", branch);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShiftsActivity.this, null);
                        startActivity(intent, options.toBundle());
                    }
                });

                holder.shiftTime.setText(model.getFrom() + " - " + model.getTo() + " •");
                holder.shiftBranch.setText(model.getBranch_name());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };

        shiftAdapter.startListening();
        shiftsRecyclerView.setAdapter(shiftAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShiftsActivity.this, ScanActivity.class);
        intent.putExtra("BRANCH", branch);
        startActivity(intent);
    }
}