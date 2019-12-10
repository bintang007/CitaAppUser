package com.cita.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cita.myapplication.R;
import com.cita.myapplication.model.Diagnoses;
import com.cita.myapplication.ui.diagnoses.DiagnosesFragmentDirections;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiagnosesAdapter extends RecyclerView.Adapter<DiagnosesAdapter.DiagnosesViewHolder> {
    private ArrayList<Diagnoses> diagnosesArrayList;
    private static final Locale LOCALE_ID = new Locale("in", "ID");

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
    public void onBindViewHolder(@NonNull DiagnosesViewHolder holder, final int position) {
        holder.childName.setText(diagnosesArrayList.get(position).getChildName());
        holder.diagnosesResult.setText(diagnosesArrayList.get(position).getDiagnosesResult());
        String diagnosesDateTimestamp = diagnosesArrayList.get(position).getDiagnosesDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LOCALE_ID);

        try {
            Date dateTime = formatter.parse(diagnosesDateTimestamp);
            formatter = new SimpleDateFormat("dd MMM ''yy HH:mm:ss", LOCALE_ID);
            assert dateTime != null;
            String diagnosesDate = formatter.format(dateTime);
            holder.diagnosesDate.setText(diagnosesDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiagnosesFragmentDirections.ActionNavDiagnosesToNavShowDiagnoses actionNavDiagnosesToNavShowDiagnoses =
                        DiagnosesFragmentDirections.actionNavDiagnosesToNavShowDiagnoses();
                actionNavDiagnosesToNavShowDiagnoses.setDiagnosesId(diagnosesArrayList.get(position).getDiagnosesId());
                Navigation.findNavController(view).navigate(actionNavDiagnosesToNavShowDiagnoses);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diagnosesArrayList.size();
    }

    class DiagnosesViewHolder extends RecyclerView.ViewHolder {
        private TextView childName, diagnosesResult, diagnosesDate;

        DiagnosesViewHolder(View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.tv_child_name);
            diagnosesResult = itemView.findViewById(R.id.tv_diagnoses_result);
            diagnosesDate = itemView.findViewById(R.id.tv_diagnoses_date);

        }

    }

    public void setItems(ArrayList<Diagnoses> items) {
        diagnosesArrayList = items;
        notifyDataSetChanged();
    }
}
