package com.cita.myapplication.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Child;
import com.cita.myapplication.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChildAsyncTask extends AsyncTask<Void, Void, ArrayList<Child>> {

    public interface ChildAsyncResponse {
        void processFinish(ArrayList<Child> children);
    }

    private ChildAsyncResponse delegate;

    private static final String TAG_CHILD_ID = "child_id", TAG_CHILD_NAME = "child_name",
            TAG_USER_ID = "user_id", TAG_JSON_OBJ = "json_obj_req";

    private int userId;

    private Context context;

    public ChildAsyncTask(int userId, Context context, ChildAsyncResponse delegate) {
        this.userId = userId;
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Child> doInBackground(Void... voids) {
        final ArrayList<Child> children = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "user/child_name.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(ChildAsyncTask.class.getSimpleName(), response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArrayChildId = jsonObject.getJSONArray(TAG_CHILD_ID);
                            JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);

                            for (int i = 0; i < jsonArrayChildId.length(); i++) {
                                Child child = new Child();
                                child.setChildId(jsonArrayChildId.getInt(i));
                                child.setChildName(jsonArrayChildName.getString(i));
                                children.add(child);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_USER_ID, String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, context);
        return children;
    }


    @Override
    protected void onPostExecute(ArrayList<Child> result) {
        delegate.processFinish(result);
    }

}
