package com.example.shopclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopclient.Adapters.LoadingDialog;
import com.example.shopclient.Adapters.ServerDialog;
import com.example.shopclient.R;
import com.example.shopclient.Models.SelectedItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenedActivity extends AppCompatActivity {
    //String ip = "http://192.168.0.143:45455/";
    String ip = "http://192.168.100.58:45455/";
    public SelectedItem selectedItem;
    OkHttpClient client = new OkHttpClient();
    TextView title,price,desc,address,date,phone,name,email;
    ImageView img,img1,img2,img3,img4,img5;
    final LoadingDialog loadingDialog= new LoadingDialog(OpenedActivity.this);
    final ServerDialog serverDialog= new ServerDialog(OpenedActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_opened);
        loadingDialog.startLoadingDialog();
        title = findViewById(R.id.titleTextView);
        price = findViewById(R.id.priceTextView);
        desc = findViewById(R.id.descTextView);
        address = findViewById(R.id.AddressTextView);
        date = findViewById(R.id.dateTextView);
        img = findViewById(R.id.item1ImageView);
        img1 = findViewById(R.id.item11imageView);
        img2 = findViewById(R.id.item2ImageView);
        img3 = findViewById(R.id.item3ImageView);
        img4 = findViewById(R.id.item4ImageView);
        img5 = findViewById(R.id.item5ImageView);
        phone = findViewById(R.id.telefonTextView);
        name = findViewById(R.id.nameTextView);
        email = findViewById(R.id.emailTextView);

        int id = -1;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            id = bundle.getInt("id");
        }

        String url = ip + "api/items" + "/" + id;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                OpenedActivity.this.runOnUiThread(new Runnable() {
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
                if (response.isSuccessful()) {
                    OpenedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type type = new TypeToken<SelectedItem>() {}.getType();
                            selectedItem = gson.fromJson(myResponse, type);
                            setDatas();
                            loadingDialog.dismissDialog();
                        }
                    });
                } else {
                    OpenedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setToastMessage(myResponse);
                        }
                    });
                }
            }
        });
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

    public void setDatas(){
        title.setText(selectedItem.getTitle());
        price.setText(selectedItem.getPrice());
        desc.setText(selectedItem.getDescription());
        address.setText(selectedItem.getAddress());
        String[] date1 = selectedItem.getArrival().split("T");
        String[] date2 = date1[0].split("-");
        String month = "";
        switch (date2[1]){
            case "1":month = "January";
            case "2":month = "February";
            case "3":month = "March";
            case "4":month = "April";
            case "5":month = "May";
            case "6":month = "June";
            case "7":month = "July";
            case "8":month = "August";
            case "9":month = "September";
            case "10":month = "October";
            case "11":month = "November";
            case "12":month = "December";
        }
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");
        Date getdate = new Date(System.currentTimeMillis());
        String formatteddate = formatter.format(getdate);
        String[] getdatesp = formatteddate.toString().split("T");
        if(getdatesp[0].equals(date1[0])){
            date.setText("Today at "+date1[1].split(":")[0]+":"+date1[1].split(":")[1]);
        }else{
            date.setText(date2[2] + " "+month+ " "+date2[0]);
        }
        phone.setText(selectedItem.getPhone());
        name.setText(selectedItem.getName());
        email.setText(selectedItem.getEmail());
        if(!selectedItem.getImg1().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg1(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
            img1.setImageBitmap(decodebitmap);
        }
        if(!selectedItem.getImg2().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg2(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img2.setImageBitmap(decodebitmap);
        }
        if(!selectedItem.getImg3().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg3(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img3.setImageBitmap(decodebitmap);
        }
        if(!selectedItem.getImg4().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg4(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img4.setImageBitmap(decodebitmap);
        }
        if(!selectedItem.getImg5().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg5(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img5.setImageBitmap(decodebitmap);
        }
    }

    public void smsButtonClick(View view) {
        if(!phone.getText().equals(null)){
            Intent i = new Intent(Intent.ACTION_SENDTO);
            String p = "sms:" + phone.getText();
            i.setData(Uri.parse(p));
            startActivity(i);
        }else{
            setToastMessage("Phone number not exists!");
        }
    }

    public void callButtonClick(View view) {
        if(!phone.getText().equals(null)){
            Intent i = new Intent(Intent.ACTION_DIAL);
            String p = "tel:" + phone.getText();
            i.setData(Uri.parse(p));
            startActivity(i);
        }else{
            setToastMessage("Phone number not exists!");
        }
    }

    public void backButtonClick(View view) {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void img1ButtonClick(View view) {
        if(!selectedItem.getImg1().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg1(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
        }
    }
    public void img2ButtonClick(View view) {
        if(!selectedItem.getImg2().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg2(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
        }
    }
    public void img3ButtonClick(View view) {
        if(!selectedItem.getImg3().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg3(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
        }
    }
    public void img4ButtonClick(View view) {
        if(!selectedItem.getImg4().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg4(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
        }
    }
    public void img5ButtonClick(View view) {
        if(!selectedItem.getImg5().equals("empty")){
            byte[] decodeString = Base64.decode(selectedItem.getImg5(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            img.setImageBitmap(decodebitmap);
        }
    }
}
