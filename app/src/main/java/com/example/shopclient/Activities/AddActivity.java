package com.example.shopclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopclient.Adapters.LoadingDialog;
import com.example.shopclient.Adapters.ServerDialog;
import com.example.shopclient.R;
import com.example.shopclient.Models.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {
    //String ip = "http://192.168.0.143:45455/";
    String ip = "http://192.168.100.58:45455/";
    byte[] byteArray;
    String encodedImage;
    public static final int PICK_IMAGE = 1;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    EditText addressTextView,phoneTextView,descTextView,titleTextView,priceTextView;
    OkHttpClient client = new OkHttpClient();
    User[] user;
    String img = "empty";
    String img1 = "empty";
    String img2 = "empty";
    String img3 = "empty";
    String img4 = "empty";
    String img5 = "empty";
    final LoadingDialog loadingDialog= new LoadingDialog(AddActivity.this);
    final ServerDialog serverDialog= new ServerDialog(AddActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_add);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        descTextView = findViewById(R.id.descTextView);
        titleTextView = findViewById(R.id.titleTextView);
        priceTextView = findViewById(R.id.priceTextView);
        getData();
    }
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String login_email;
        if(bundle != null){
            login_email = bundle.getString("login_email");
        }else{
            login_email = "";
        }
        if(!login_email.equals("")){
            String url = "http://192.168.100.58:45455/api/users?filter="+login_email;
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    AddActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismissDialog();
                            serverDialog.startLoadingDialog();
                        }
                    });
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String myResponse = response.body().string();
                    if(response.isSuccessful()){
                        AddActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                //Type type = new TypeToken<User>(){}.getType();
                                user = gson.fromJson(myResponse, User[].class);
                            }
                        });
                    }else{
                        AddActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setToastMessage(myResponse);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String login_email = "";
        if(bundle != null){
            login_email = bundle.getString("login_email");
        }
        Intent homeintent = new Intent(getApplicationContext(), HomeActivity.class);
        homeintent.putExtra("login_email",login_email);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }
    public void setToastMessage(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.my_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        int toastDurationInMilliSeconds = 500;
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };
        toast.show();
        toastCountDown.start();
    }

    public void uploadButton(View view) {
        if(img5.equals("empty")){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }else{
            setToastMessage("All pictures selected!");
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Bitmap originBitmap = null;
            Uri selectedImage = data.getData();
            InputStream imageStream;
            try
            {
                imageStream = getContentResolver().openInputStream(selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);
            }
            catch (FileNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
            if (originBitmap != null)
            {
                if(img1.equals("empty")){
                    this.imageView1.setImageBitmap(originBitmap);
                }else if(img2.equals("empty")){
                    this.imageView2.setImageBitmap(originBitmap);
                }else if(img3.equals("empty")){
                    this.imageView3.setImageBitmap(originBitmap);
                }else if(img4.equals("empty")){
                    this.imageView4.setImageBitmap(originBitmap);
                }else if(img5.equals("empty")){
                    this.imageView5.setImageBitmap(originBitmap);
                }
                Log.w("Image Setted in", "Done Loading Image");
                if(img.equals("empty")){
                    try
                    {
                        Bitmap image = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        img1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        Bitmap resized;
                        if(image.getWidth() > 2000 || image.getHeight() > 2000 ){
                            resized = Bitmap.createScaledBitmap(image, image.getWidth()/4, image.getHeight()/4, true);
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            resized.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();
                            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        }
                        else if(image.getWidth() > 1024 || image.getHeight() > 1024 ){
                            resized = Bitmap.createScaledBitmap(image, image.getWidth()/2, image.getHeight()/2, true);
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            resized.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();
                            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        }else{
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();
                            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        }
                    }catch (Exception e)
                    {
                        Log.w("OOooooooooo","exception");
                    }
                    img = encodedImage;
                }else if(img2.equals("empty")){
                    Bitmap image = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    img2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }else if(img3.equals("empty")){
                    Bitmap image = ((BitmapDrawable) imageView3.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    img3 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }else if(img4.equals("empty")){
                    Bitmap image = ((BitmapDrawable) imageView4.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    img4 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }else if(img5.equals("empty")){
                    Bitmap image = ((BitmapDrawable) imageView5.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    img5 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                //Toast.makeText(AddActivity.this, "Conversion Done",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void addItemClick(View view) throws IOException, JSONException {
        String address = addressTextView.getText().toString();
        String phone = phoneTextView.getText().toString();
        String desc = descTextView.getText().toString();
        String title = titleTextView.getText().toString();
        String price = priceTextView.getText().toString();
        if (address.equals("") || phone.equals("") || desc.equals("") || title.equals("") || price.equals("")) {
            setToastMessage("All field required!");
        } else {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", String.valueOf(user[0].getId()))
                    .addFormDataPart("address", address)
                    .addFormDataPart("phone", phone)
                    .addFormDataPart("description", desc)
                    .addFormDataPart("title", title)
                    .addFormDataPart("price", price)
                    .addFormDataPart("img", img)
                    .addFormDataPart("img1", img1)
                    .addFormDataPart("img2", img2)
                    .addFormDataPart("img3", img3)
                    .addFormDataPart("img4", img4)
                    .addFormDataPart("img5", img5)
                    .build();
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(ip+"api/items")
                    .post(requestBody)
                    .addHeader("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIkMnkkMTAkZzZrLkwySlFCZlBmN1RTb3g3bmNpTzltcVwvemRVN2JtVC42SXN0SFZtbzZHNlFNSkZRWWRlIiwic3ViIjo0NSwiaWF0IjoxNTUwODk4NDc0LCJleHAiOjE1NTM0OTA0NzR9.tefIaPzefLftE7q0yKI8O87XXATwowEUk_XkAOOQzfw")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "7e231ef9-5236-40d1-a28f-e5986f936877")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    AddActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismissDialog();
                            serverDialog.startLoadingDialog();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    final String myResponse = response.body().string();
                    if (response.isSuccessful()) {
                        AddActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setToastMessage("Item uploaded succesfully!");
                                onBackPressed();
                            }
                        });
                    } else {
                        AddActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setToastMessage(myResponse);
                            }
                        });
                    }
                }
            });
        }
    }
    public void backButtonClick(View view) {
        onBackPressed();
    }
}