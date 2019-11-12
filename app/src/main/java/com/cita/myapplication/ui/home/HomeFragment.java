package com.cita.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.cita.myapplication.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        final EditText editTextUsername = root.findViewById(R.id.et_username);
        Button buttonNext = root.findViewById(R.id.btn_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                HomeFragmentDirections.ActionNavHomeToNavGallery3 actionNavHomeToNavGallery3 =
                        HomeFragmentDirections.actionNavHomeToNavGallery3(username);
                Navigation.findNavController(view).navigate(actionNavHomeToNavGallery3);
            }
        });
        return root;
    }
}