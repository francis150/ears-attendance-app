package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BranchesActivity extends AppCompatActivity {

    TextView titleText;
    RecyclerView branchesRecycleView;

    Animation fadeIn;
    LayoutAnimationController fadeInLayoutAnimation;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    FirebaseRecyclerOptions<Branch> branchRecyclerOptions;
    FirebaseRecyclerAdapter<Branch, BranchViewHolder> branchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        titleText = (TextView) findViewById(R.id.branches_title_text);
        branchesRecycleView = (RecyclerView) findViewById(R.id.branches_recycler_view);
        branchesRecycleView.setLayoutManager(new LinearLayoutManager(BranchesActivity.this));

        fadeIn = AnimationUtils.loadAnimation(BranchesActivity.this, R.anim.fade_in);
        titleText.setAnimation(fadeIn);

        // Branches Recycler view Handles
        branchRecyclerOptions = new FirebaseRecyclerOptions.Builder<Branch>().setQuery(firedb.getReference().child("branches"), Branch.class).build();
        branchAdapter = new FirebaseRecyclerAdapter<Branch, BranchViewHolder>(branchRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BranchViewHolder holder, int position, @NonNull Branch model) {

                holder.itemView.setAnimation(AnimationUtils.loadAnimation(BranchesActivity.this, R.anim.fade_in_long));

                holder.name.setText(model.getName());
                holder.description.setText(model.getDescription());
            }

            @NonNull
            @Override
            public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch, parent,false);
                return new BranchViewHolder(view);
            }
        };

        branchAdapter.startListening();
        branchesRecycleView.setAdapter(branchAdapter);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(BranchesActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}