package com.cita.myapplication.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cita.myapplication.model.Child;

import java.util.ArrayList;

import com.cita.myapplication.R;
import com.cita.myapplication.ui.child.ChildFragmentDirections;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private ArrayList<Child> childArrayList;
    private SharedPreferences sharedPreferences;
    public ChildAdapter(ArrayList<Child> childArrayList) {
        this.childArrayList = childArrayList;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChildViewHolder holder, final int position) {
        holder.tvChildName.setText(childArrayList.get(position).getChildName());
        holder.tvDateOfBirth.setText(childArrayList.get(position).getDateOfBirth());
        holder.tvGender.setText(childArrayList.get(position).getGender());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildFragmentDirections.ActionNavChildToNavShowChild actionNavChildToNavShowChild =
                        ChildFragmentDirections.actionNavChildToNavShowChild();
                actionNavChildToNavShowChild.setChildId(childArrayList.get(position).getChildId());
                Navigation.findNavController(view).navigate(actionNavChildToNavShowChild);
                Toast.makeText(holder.itemView.getContext(), holder.tvChildName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return childArrayList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChildName, tvDateOfBirth, tvGender;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tvChildName = (TextView) itemView.findViewById(R.id.tv_child_name);
            tvDateOfBirth = (TextView) itemView.findViewById(R.id.tv_date_of_birth);
            tvGender = (TextView) itemView.findViewById(R.id.tv_gender);
        }
    }

    public void setItems(ArrayList<Child> items) {
        childArrayList = items;
        notifyDataSetChanged();
    }
}
