package com.cita.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cita.myapplication.R;
import com.cita.myapplication.model.Diagnoses;

import java.util.ArrayList;

public class DiagnosesAdapter extends RecyclerView.Adapter<DiagnosesAdapter.DiagnosesViewHolder> {
    private ArrayList<Diagnoses> diagnosesArrayList;

    public DiagnosesAdapter(ArrayList<Diagnoses> diagnosesArrayList) {
        this.diagnosesArrayList = diagnosesArrayList;
    }

    @NonNull
    @Override
    public DiagnosesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_diagnoses, parent, false);
        return new DiagnosesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosesViewHolder holder, int position) {
        holder.childName.setText(diagnosesArrayList.get(position).getChildName());
        holder.gender.setText(diagnosesArrayList.get(position).getGender());
        holder.childAge.setText(diagnosesArrayList.get(position).getChildAge());
        holder.weightChild.setText(diagnosesArrayList.get(position).getWeightChild());
        holder.heightChild.setText(diagnosesArrayList.get(position).getHeightCild());
        holder.diagnosesResult.setText(diagnosesArrayList.get(position).getDiagnosesResult());
        holder.description.setText(diagnosesArrayList.get(position).getDescription());
        holder.diagnosesDate.setText(diagnosesArrayList.get(position).getDiagnosesDate());
    }

    @Override
    public int getItemCount() {
        return diagnosesArrayList.size();
    }

    class DiagnosesViewHolder extends RecyclerView.ViewHolder {
        private TextView childName, description, diagnosesResult, diagnosesDate, childAge, weightChild,
                heightChild, gender;

        DiagnosesViewHolder(View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.tv_child_name);
            gender = itemView.findViewById(R.id.tv_gender);
            childAge = itemView.findViewById(R.id.tv_child_age);
            weightChild = itemView.findViewById(R.id.tv_weight_child);
            heightChild = itemView.findViewById(R.id.tv_height_child);
            diagnosesResult = itemView.findViewById(R.id.tv_diagnoses_result);
            description = itemView.findViewById(R.id.tv_description);
            diagnosesDate = itemView.findViewById(R.id.tv_diagnoses_date);

        }

    }

    public void setItems(ArrayList<Diagnoses> items) {
        diagnosesArrayList = items;
        notifyDataSetChanged();
    }
}
