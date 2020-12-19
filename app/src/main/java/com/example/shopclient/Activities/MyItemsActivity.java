package com.example.shopclient.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopclient.Adapters.ServerDialog;
import com.example.shopclient.Models.Item;
import com.example.shopclient.Adapters.LoadingDialog;
import com.example.shopclient.Adapters.MyItemAdapter;
import com.example.shopclient.R;
import com.example.shopclient.Models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyItemsActivity extends AppCompatActivity {
    String ip = "http://192.168.100.58:45455/";
    //String ip = "http://192.168.0.143:45455/";
    TextView wellcomeTextView;
    GridLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    MyItemAdapter mAdapter;
    OkHttpClient client = new OkHttpClient();
    String login_email = null;
    User[] user;
    Activity thisActivity=(Activity)this;
    final LoadingDialog loadingDialog= new LoadingDialog(MyItemsActivity.this);
    final ServerDialog serverDialog= new ServerDialog(MyItemsActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_my_items);
        wellcomeTextView = findViewById(R.id.wellcomeTextView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        login_email = "";
        if(bundle != null){
            login_email = bundle.getString("login_email");
        }
        loadingDialog.startLoadingDialog();
        getData();
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
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            login_email = bundle.getString("login_email");
        }else{
            login_email = "";
        }
        if(!login_email.equals("")){
            String url = ip+"api/users?filter="+login_email;
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    MyItemsActivity.this.runOnUiThread(new Runnable() {
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
                        MyItemsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                user = gson.fromJson(myResponse, User[].class);
                                getMyItems();
                            }
                        });
                    }else{
                        MyItemsActivity.this.runOnUiThread(new Runnable() {
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

    public void getMyItems(){
        String url = ip+"api/items/myitems/?filter="+user[0].getId();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                MyItemsActivity.this.runOnUiThread(new Runnable() {
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
                    MyItemsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Item>>(){}.getType();
                            List<Item> p = gson.fromJson(myResponse,type);
                            loadingDialog.dismissDialog();
                            mRecyclerView = findViewById(R.id.items_RecyclerView);
                            mRecyclerView.setHasFixedSize(true);
                            mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
                            mAdapter = new MyItemAdapter(p,login_email,thisActivity);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                }else{
                    MyItemsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setToastMessage(myResponse);
                        }
                    });
                }
            }
        });
    }
    public void backButtonClick(View view) {
        onBackPressed();
    }
}