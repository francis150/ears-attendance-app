package com.capstone.earsattendanceapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BranchViewHolder extends RecyclerView.ViewHolder {

    TextView name, description;

    public BranchViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.branch_name_text);
        description = itemView.findViewById(R.id.branch_description_text);
    }
}
