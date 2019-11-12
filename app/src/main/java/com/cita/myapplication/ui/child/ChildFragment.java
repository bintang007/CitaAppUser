package com.cita.myapplication.ui.child;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class ChildFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private final static String TAG_USER_ID = "user_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_child, container, false);
        FloatingActionButton fabCreateChild = root.findViewById(R.id.fab_create_child);
        fabCreateChild.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabCreateChild.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabCreateChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = Objects.requireNonNull(getActivity())
                        .getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                                Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt(TAG_USER_ID, 0);
                ChildFragmentDirections.ActionNavChildToNavCreateChild actionNavChildToNavCreateChild =
                        ChildFragmentDirections.actionNavChildToNavCreateChild();
                actionNavChildToNavCreateChild.setUserId(userId);
                Navigation.findNavController(view).navigate(actionNavChildToNavCreateChild);

            }
        });
        return root;
    }


}
