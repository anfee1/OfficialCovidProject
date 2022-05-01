package com.ccc.androidcrud1;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccc.androidcrud1.util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListItem extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView listView;
    SimpleAdapter adapter;
    ProgressDialog loading;
    EditText editTextSearchItem;
    TextView usernameText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        String name = getIntent().getStringExtra("name_key");
        usernameText = findViewById(R.id.textView5);
        usernameText.setText(name);

        listView = (ListView) findViewById(R.id.lv_item);

        listView.setOnItemClickListener(this);

        editTextSearchItem = (EditText)findViewById(R.id.et_search);
        if(name.equals("admin")){getItems();}
        else{
            getItems();
        }


    }


    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utilities.webAppUrl+"?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String itemId = jo.getString("itemId");
                String itemName = jo.getString("itemName");
                String brand = jo.getString("brand");
                String price = jo.getString("price");
                String status = jo.getString("status");
                String username = jo.getString("username");


                HashMap<String, String> item = new HashMap<>();
                item.put("itemId",itemId);
                item.put("itemName", itemName);
                item.put("brand", brand);
                item.put("price",price);
                item.put("status",status);

                if (getIntent().getStringExtra("name_key").equals("admin")) {
                    list.add(item);
                }else if (username.equals(getIntent().getStringExtra("name_key"))) {
                    list.add(item);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"itemName","brand","status","price","itemId","username"},new int[]{R.id.tv_item_name,R.id.tv_brand,R.id.patientStatus});


        listView.setAdapter(adapter);
        loading.dismiss();

        editTextSearchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                ListItem.this.adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ItemDetails.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String itemId = map.get("itemId").toString();
        String itemName = map.get("itemName").toString();
        String brand = map.get("brand").toString();
        String price = map.get("price").toString();
        String status = map.get("status").toString();


        // String sno = map.get("sno").toString();

        // Log.e("SNO test",sno);
        intent.putExtra("itemId",itemId);
        intent.putExtra("itemName",itemName);
        intent.putExtra("brand",brand);
        intent.putExtra("status",status);
        intent.putExtra("price",price);


        startActivity(intent);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}