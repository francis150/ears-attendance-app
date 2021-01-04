package com.capstone.earsattendanceapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShiftViewHolder extends RecyclerView.ViewHolder {

    TextView shiftTime, shiftBranch;

    public ShiftViewHolder(@NonNull View itemView) {
        super(itemView);

        shiftTime = itemView.findViewById(R.id.shift_time);
        shiftBranch = itemView.findViewById(R.id.shift_branch);
    }
}
