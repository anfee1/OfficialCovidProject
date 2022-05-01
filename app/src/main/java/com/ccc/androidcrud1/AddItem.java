package com.ccc.androidcrud1;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




public class AddItem extends AppCompatActivity implements View.OnClickListener {

    TextView usernameText;
    EditText editTextName,editTextVaccine,editTextLastShot,editTextEmail;
     ImageView imageViewUserImage;
    Button buttonAddItem,buttonAddImage;
    Bitmap rbitmap;
    String userImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_item);
        String myname = getIntent().getStringExtra("name_key");

        usernameText = findViewById(R.id.textView5);
        usernameText.setText(myname);

        editTextName = (EditText)findViewById(R.id.et_full_name);
        editTextVaccine = (EditText)findViewById(R.id.et_vaccine_type);
        editTextLastShot = (EditText)findViewById(R.id.et_date);

        buttonAddItem = (Button)findViewById(R.id.btn_update_item);
        buttonAddItem.setOnClickListener(this);

        imageViewUserImage=(ImageView)findViewById(R.id.iv_uphoto);

        buttonAddImage = (Button) findViewById(R.id.btn_image);
        buttonAddImage.setOnClickListener(this);

    }

    //This is the part where data is transfered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String name = editTextName.getText().toString().trim();
        final String username = usernameText.getText().toString().trim();
        final String vaccine = editTextVaccine.getText().toString().trim();
        final String lastShot = editTextLastShot.getText().toString().trim();





        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilities.webAppUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("name_key", usernameText.getText().toString().trim());
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
                parmas.put("action","addItem");
                parmas.put("itemName",name);
                parmas.put("username",username);
                parmas.put("brand",vaccine);
                parmas.put("price",lastShot);
                parmas.put("image",userImage);
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


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                rbitmap = getResizedBitmap(bitmap,250);//Setting the Bitmap to ImageView
                userImage = getStringImage(rbitmap);
                imageViewUserImage.setImageBitmap(rbitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            addItemToSheet();
        }
        if(v == buttonAddImage){
            showFileChooser();
        }
    }
}
