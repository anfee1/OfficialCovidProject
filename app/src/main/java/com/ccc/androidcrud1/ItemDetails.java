package com.ccc.androidcrud1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class ItemDetails extends AppCompatActivity implements View.OnClickListener {


    TextView textViewitemName, textViewbrand, textViewprice, textViewStatus, usernameText;
    Button buttonEdit, buttonDeleteItem;
    String itemId, itemName, brand, price, status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.item_details);

        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        itemName = intent.getStringExtra("itemName");
        brand = intent.getStringExtra("brand");
        price = intent.getStringExtra("price");
        status = intent.getStringExtra("status");

        textViewStatus = (TextView)findViewById(R.id.tv_id);
        textViewitemName = (TextView) findViewById(R.id.tv_item_name);
        textViewbrand = (TextView) findViewById(R.id.tv_brand);
        textViewprice = (TextView) findViewById(R.id.tv_price);
        buttonEdit= (Button)findViewById(R.id.btn_edit);
        buttonEdit.setOnClickListener(this);
        buttonDeleteItem = (Button)findViewById(R.id.btn_delete);
        buttonDeleteItem.setOnClickListener(this);

        textViewStatus.setText(status);
        textViewitemName.setText(itemName);
        textViewbrand.setText(brand);
        textViewprice.setText(price);


    }


    private void  deleteItemFromSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Deleting Item","Please wait");





        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilities.webAppUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(ItemDetails.this,response,Toast.LENGTH_LONG).show();
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
                parmas.put("action","deleteItem");
                parmas.put("itemId",itemId);
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

        if(v==buttonEdit){
            Intent intent = new Intent(this,UpdateItem.class);
            // String sno = map.get("sno").toString();

            // Log.e("SNO test",sno);
            intent.putExtra("itemId",itemId);
            intent.putExtra("itemName",itemName);
            intent.putExtra("brand",brand);
            intent.putExtra("price",price);

            startActivity(intent);

            //Define what to do when button is clicked
        }



        if(v==buttonDeleteItem){
            deleteItemFromSheet();
        }
    }

}