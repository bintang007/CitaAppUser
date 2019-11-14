package com.cita.myapplication.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cita.myapplication.R;
import com.cita.myapplication.model.Nutrient;

import java.util.ArrayList;

public class NutrientAdapter extends RecyclerView.Adapter<NutrientAdapter.NutrientViewHolder> {
    private ArrayList<Nutrient> nutrientArrayList;
    private SharedPreferences sharedPreferences;

    public NutrientAdapter(ArrayList<Nutrient> nutrientArrayList) {
        this.nutrientArrayList = nutrientArrayList;
    }

    @Override
    public NutrientAdapter.NutrientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_nutrient, parent, false);
        return new NutrientAdapter.NutrientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NutrientViewHolder holder, final int position) {
        holder.tvNutrientName.setText(nutrientArrayList.get(position).getNutrientName());
        holder.tvCarbohydrate.setText(nutrientArrayList.get(position).getCarbohydrate());
        holder.tvCalories.setText(nutrientArrayList.get(position).getCalories());
        holder.tvFat.setText(nutrientArrayList.get(position).getFat());
        holder.tvProtein.setText(nutrientArrayList.get(position).getProtein());
    }

    @Override
    public int getItemCount() {
        return nutrientArrayList.size();
    }

    public class NutrientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNutrientName, tvCarbohydrate, tvCalories, tvFat, tvProtein;

        public NutrientViewHolder(View itemView) {
            super(itemView);
            tvNutrientName = (TextView) itemView.findViewById(R.id.tv_nutrient_name);
            tvCarbohydrate = (TextView) itemView.findViewById(R.id.tv_carbohydrate);
            tvCalories = (TextView) itemView.findViewById(R.id.tv_calories);
            tvFat = (TextView) itemView.findViewById(R.id.tv_fat);
            tvProtein = (TextView) itemView.findViewById(R.id.tv_protein);
        }
    }

    public void setItems(ArrayList<Nutrient> items) {
        nutrientArrayList = items;
        notifyDataSetChanged();
    }
}
