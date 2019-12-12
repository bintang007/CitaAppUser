package com.cita.myapplication.ui.diagnoses;

import android.app.ProgressDialog;
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
import com.cita.myapplication.adapter.DiagnosesAdapter;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Diagnoses;
import com.cita.myapplication.utils.Server;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiagnosesFragment extends Fragment {

    private ProgressDialog progressDialog;

    private static final String URL = Server.URL + "user/diagnoses.php";
    private static final String TAG = DiagnosesFragment.class.getSimpleName();
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DIAGNOSES_ID = "diagnoses_id";
    private static final String TAG_CHILD_NAME = "child_name";
    private static final String TAG_DIAGNOSES_RESULT = "diagnoses_result";
    private static final String TAG_DIAGNOSES_DATE = "diagnoses_date";
    private ArrayList<Diagnoses> diagnosesArrayList;
    private int userId;
    private TextView tvEmptyChild;
    private DiagnosesAdapter diagnosesAdapter;
    private FloatingActionButton fabCreateChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_diagnoses, container, false);
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);
        tvEmptyChild = root.findViewById(R.id.tv_empty_child);

        getAllDiagnoses();

        RecyclerView rvDiagnoses = root.findViewById(R.id.rv_diagnoses);
        diagnosesAdapter = new DiagnosesAdapter(diagnosesArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDiagnoses.setLayoutManager(layoutManager);
        rvDiagnoses.setAdapter(diagnosesAdapter);


        fabCreateChild = root.findViewById(R.id.fab_create_diagnoses);
        fabCreateChild.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabCreateChild.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabCreateChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiagnosesFragmentDirections.ActionNavDiagnosesToNavCreateDiagnoses actionNavDiagnosesToNavCreateDiagnoses =
                        DiagnosesFragmentDirections.actionNavDiagnosesToNavCreateDiagnoses();
                actionNavDiagnosesToNavCreateDiagnoses.setUserId(userId);
                Navigation.findNavController(view).navigate(actionNavDiagnosesToNavCreateDiagnoses);
            }
        });
        return root;
    }

    private void getAllDiagnoses() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        diagnosesArrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                JSONArray jsonArrayDiagnosesId = jsonObject.getJSONArray(TAG_DIAGNOSES_ID);
                                JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);
                                JSONArray jsonArrayDiagnosesResult = jsonObject.getJSONArray(TAG_DIAGNOSES_RESULT);
                                JSONArray jsonArrayDiagnosesDate = jsonObject.getJSONArray(TAG_DIAGNOSES_DATE);
                                int length = jsonArrayDiagnosesId.length();
                                int[] diagnosesId = new int[length];
                                String[] childName = new String[length];
                                String[] diagnosesResult = new String[length];
                                String[] diagnosesDate = new String[length];
                                for (int i = 0; i < length; i++) {
                                    diagnosesId[i] = jsonArrayDiagnosesId.getInt(i);
                                    childName[i] = jsonArrayChildName.getString(i);
                                    diagnosesResult[i] = jsonArrayDiagnosesResult.getString(i);
                                    diagnosesDate[i] = jsonArrayDiagnosesDate.getString(i);
                                }
                                for (int i = 0; i < length; i++) {
                                    for (int j = 1; j < length - 1; j++) {
                                        int a = diagnosesId[j - 1];
                                        int b = diagnosesId[j];
                                        if (a > b) {
                                            String tempChildName = childName[j - 1];
                                            String tempDiagnosesResult = diagnosesResult[j - 1];
                                            String tempDiagnosesDate = diagnosesDate[j - 1];

                                            diagnosesId[j - 1] = b;
                                            childName[j - 1] = childName[j];
                                            diagnosesResult[j - 1] = diagnosesResult[j];
                                            diagnosesDate[j - 1] = diagnosesDate[j];

                                            diagnosesId[j] = a;
                                            childName[j] = tempChildName;
                                            diagnosesResult[j] = tempDiagnosesResult;
                                            diagnosesDate[j] = tempDiagnosesDate;

                                        }
                                    }
                                }
                                for (int i = 0; i < length; i++) {
                                    Diagnoses diagnoses = new Diagnoses();
                                    diagnoses.setDiagnosesId(diagnosesId[i]);
                                    diagnoses.setChildName(childName[i]);
                                    diagnoses.setDiagnosesResult(diagnosesResult[i]);
                                    diagnoses.setDiagnosesDate(diagnosesDate[i]);
                                    diagnosesArrayList.add(diagnoses);
                                    diagnosesAdapter.setItems(diagnosesArrayList);
                                }
                            } else if (success == 0) {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                            } else {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                                fabCreateChild.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getActivity(), "Anda belum memiliki data anak", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                params.put(TAG_USER_ID, String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
