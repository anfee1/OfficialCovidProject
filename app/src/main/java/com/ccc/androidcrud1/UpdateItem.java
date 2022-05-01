package com.ccc.androidcrud1;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccc.androidcrud1.util.Utilities;

import java.util.HashMap;
import java.util.Map;





public class UpdateItem extends AppCompatActivity implements View.OnClickListener {


    EditText editTextName,editTextVaccine,editTextLastShot, editTextEmail;
    Button buttonUpdateItem;
    String itemId, name, vaccine, lastShot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.update_item);


        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        name = intent.getStringExtra("name");
        vaccine = intent.getStringExtra("vaccine");
        lastShot = intent.getStringExtra("lastShot");



        editTextName = (EditText)findViewById(R.id.et_full_name);
        editTextVaccine = (EditText)findViewById(R.id.et_vaccine_type);
        editTextLastShot = (EditText)findViewById(R.id.et_date);


        editTextName.setText(name);
        editTextVaccine.setText(vaccine);
        editTextLastShot.setText(lastShot);


        buttonUpdateItem = (Button)findViewById(R.id.btn_update_item);
        buttonUpdateItem.setOnClickListener(this);


    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   updateItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Updating Item","Please wait");
        final String name = editTextName.getText().toString().trim();
        final String vaccine = editTextVaccine.getText().toString().trim();
        final String lastShot = editTextLastShot.getText().toString().trim();





        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilities.webAppUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(UpdateItem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","updateItem");
                parmas.put("name",name);
                parmas.put("vaccine",vaccine);
                parmas.put("lastShot",lastShot);
                parmas.put("status","Pending");


                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }




    @Override
    public void onClick(View v) {

        if(v==buttonUpdateItem){
            updateItemToSheet();

            //Define what to do when button is clicked
        }
    }
}
