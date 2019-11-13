package com.cita.myapplication.ui.child;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.R;
import com.cita.myapplication.adapter.ChildAdapter;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Child;
import com.cita.myapplication.utils.Server;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChildFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private final static String TAG_USER_ID = "user_id", TAG = ChildFragment.class.getSimpleName();
    private static final String URL = Server.URL + "user/read_child.php", TAG_SUCCESS = "success",
            TAG_CHILD_NAME = "child_name", TAG_DATE_OF_BIRTH = "date_of_birth", TAG_GENDER = "gender",
            TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req", TAG_CHILD_ID = "child_id";
    private ArrayList<Child> childArrayList;
    private int userId;
    private TextView tvEmptyChild;
    private ChildAdapter childAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_child, container, false);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);
        tvEmptyChild = root.findViewById(R.id.tv_empty_child);

        getAllChild();

        RecyclerView rvChild = root.findViewById(R.id.rv_childs);
        childAdapter = new ChildAdapter(childArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvChild.setLayoutManager(layoutManager);
        rvChild.setAdapter(childAdapter);


        FloatingActionButton fabCreateChild = root.findViewById(R.id.fab_create_child);
        fabCreateChild.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabCreateChild.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabCreateChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildFragmentDirections.ActionNavChildToNavCreateChild actionNavChildToNavCreateChild =
                        ChildFragmentDirections.actionNavChildToNavCreateChild();
                actionNavChildToNavCreateChild.setUserId(userId);
                Navigation.findNavController(view).navigate(actionNavChildToNavCreateChild);
            }
        });
        return root;
    }

    private void getAllChild() {
        childArrayList = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Read response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                JSONArray jsonArrayChildId = jsonObject.getJSONArray(TAG_CHILD_ID);
                                JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);
                                JSONArray jsonArrayGender = jsonObject.getJSONArray(TAG_GENDER);
                                JSONArray jsonArrayDateOfBirth = jsonObject.getJSONArray(TAG_DATE_OF_BIRTH);
                                for (int i = 0; i < jsonArrayChildName.length(); i++) {
                                    Child child = new Child();
                                    child.setChildId(jsonArrayChildId.getInt(i));
                                    child.setChildName(jsonArrayChildName.getString(i));
                                    child.setGender(jsonArrayGender.getString(i));
                                    child.setDateOfBirth(jsonArrayDateOfBirth.getString(i));
                                    childArrayList.add(child);
                                    childAdapter.setItems(childArrayList);
                                }
                            } else {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }


}
