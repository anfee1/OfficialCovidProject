package com.ccc.androidcrud1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LogIn extends AppCompatActivity{
    EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        Button loggy = findViewById(R.id.loginbtn);

        //admin and admin

        loggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLogin();
            }
        });
    }

    private void registerLogin() {

        final ProgressDialog loading = ProgressDialog.show(this, "Checking Credentials", "Please wait");

        final String user = username.getText().toString().trim();
        final String pass = password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilities.webAppUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if(response.equals("Wrong Password")){
                            Toast.makeText(LogIn.this, response + " try again.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LogIn.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LogIn.this, response, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("name_key", user);
                            startActivity(intent);
                        }
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
                parmas.put("action", "getLogin");
                parmas.put("username", user);
                parmas.put("password", pass);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }
}
