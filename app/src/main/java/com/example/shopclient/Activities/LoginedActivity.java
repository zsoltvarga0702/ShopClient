package com.example.shopclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginedActivity extends AppCompatActivity {
    //String ip = "http://192.168.0.143:45455/";
    String ip = "http://192.168.100.58:45455/";
    String login_email;
    OkHttpClient client = new OkHttpClient();
    User[] user;
    TextView numadsTextView;
    TextView nameTextView;
    TextView emailTextView;
    final LoadingDialog loadingDialog= new LoadingDialog(LoginedActivity.this);
    final ServerDialog serverDialog= new ServerDialog(LoginedActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_logined);
        numadsTextView = findViewById(R.id.numadsTextView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        loadingDialog.startLoadingDialog();
        getData();
    }
    public void setToastMessage(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.my_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
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

    public void backButtonClick(View view) {
        onBackPressed();
    }

    public void logoutButtonClick(View view) {
        setToastMessage("You logged out!");
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.clear();
        myEdit.commit();
        Intent loginintent = new Intent(getApplicationContext(), LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void removeButtonClick(View view) {
        String url2 = ip+"api/users/"+user[0].getId();
        Request request2 = new Request.Builder().url(url2).delete().build();
        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LoginedActivity.this.runOnUiThread(new Runnable() {
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
                    LoginedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setToastMessage("User successfully deleted!");
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.clear();
                            myEdit.commit();
                            Intent loginintent = new Intent(getApplicationContext(),LoginActivity.class);
                            loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginintent);
                        }
                    });
                }else{
                    LoginedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), myResponse, Toast.LENGTH_SHORT).show();;
                        }
                    });
                }
            }
        });
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
                    LoginedActivity.this.runOnUiThread(new Runnable() {
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
                        LoginedActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                user = gson.fromJson(myResponse, User[].class);
                                getMyItems();
                            }
                        });
                    }else{
                        LoginedActivity.this.runOnUiThread(new Runnable() {
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
    public void getMyItems(){
        String url = ip+"api/items/myitems/?filter="+user[0].getId();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LoginedActivity.this.runOnUiThread(new Runnable() {
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
                    LoginedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismissDialog();
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Item>>(){}.getType();
                            List<Item> p = gson.fromJson(myResponse,type);
                            nameTextView.setText(user[0].getName());
                            emailTextView.setText(user[0].getEmail());
                            numadsTextView.setText(p.size()+"");
                        }
                    });
                }else{
                    LoginedActivity.this.runOnUiThread(new Runnable() {
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