package com.example.shopclient.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopclient.Adapters.NetworkChangeReceiver;
import com.example.shopclient.Models.Item;
import com.example.shopclient.Adapters.ItemAdapter;
import com.example.shopclient.Adapters.LoadingDialog;
import com.example.shopclient.R;
import com.example.shopclient.Adapters.ServerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    //String ip = "http://192.168.0.143:45455/";
    String ip = "http://192.168.100.58:45455/";
    public List<Item> itemsTotal = new ArrayList<>();
    public List<Item> items = new ArrayList<>();
    EditText searchText;
    GridLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    ItemAdapter mAdapter;
    OkHttpClient client = new OkHttpClient();
    boolean loading = true;
    int scrollPosition = items.size();
    int currentSize = scrollPosition;
    int nextLimit = currentSize + 10;
    String login_email = null;
    String filter = "";
    final LoadingDialog loadingDialog= new LoadingDialog(HomeActivity.this);
    final ServerDialog serverDialog= new ServerDialog(HomeActivity.this);
    Activity thisActivity=(Activity)this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_home);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            login_email = bundle.getString("login_email");
            myEdit.putString("login_email",login_email);
            myEdit.commit();
        }else{
            login_email = "";
        }
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        login_email = sh.getString("login_email", "");
        searchText = findViewById(R.id.searchEditText);
        mRecyclerView = findViewById(R.id.items_RecyclerView);
        loadingDialog.startLoadingDialog();
        init();
    }

    public void init(){
        getData();
        populateData();
        initScrollListener();
    }
    public void populateData() {
            String url2 = ip+"api/items/getfirstitems";
            Request request2 = new Request.Builder().url(url2).build();
            client.newCall(request2).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HomeActivity.this.runOnUiThread(new Runnable() {
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
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Item>>(){}.getType();
                                items = gson.fromJson(myResponse, type);
                                initAdapter();
                            }
                        });
                    }else{
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setToastMessage(myResponse);
                            }
                        });
                    }
                }
            });
    }
    private void getData(){
        String url = ip+"api/items/filtered/?filter="+filter;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HomeActivity.this.runOnUiThread(new Runnable() {
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
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Item>>(){}.getType();
                            itemsTotal = gson.fromJson(myResponse, type);
                        }
                    });
                }else{
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setToastMessage(myResponse);
                        }
                    });
                }
            }
        });
    }
    public void filterButtonClick(View view) {
        loadingDialog.startLoadingDialog();
        filter = searchText.getText().toString();
        if(filter.equals("")){
            init();
        }else{
            String url = ip+"api/items/filtered/?filter="+filter;
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HomeActivity.this.runOnUiThread(new Runnable() {
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
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Item>>(){}.getType();
                                List<Item> p = gson.fromJson(myResponse,type);
                                mRecyclerView = findViewById(R.id.items_RecyclerView);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
                                mAdapter = new ItemAdapter(p,thisActivity);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mRecyclerView.setAdapter(mAdapter);
                                loadingDialog.dismissDialog();
                                serverDialog.dismissDialog();
                            }
                        });
                    }else{
                        HomeActivity.this.runOnUiThread(new Runnable() {
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

    public void addButtonClick(View view) {
        if(!login_email.equals("")){
            Intent addIntent = new Intent(this, AddActivity.class);
            addIntent.putExtra("login_email",login_email);
            startActivity(addIntent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }else{
            Intent login = new Intent(this, LoginActivity.class);
            login.putExtra("intent","add");
            startActivity(login);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }
    }
    public void getButton(View view) {
        if(!login_email.equals("")){
            Intent Intent = new Intent(this, MyItemsActivity.class);
            Intent.putExtra("login_email",login_email);
            startActivity(Intent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }else{
            Intent login = new Intent(this,LoginActivity.class);
            login.putExtra("intent","get");
            startActivity(login);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }
    }
    public void browseButtonClick(View view) {
        if(!login_email.equals("")){
            Intent Intent = new Intent(this, HomeActivity.class);
            Intent.putExtra("login_email",login_email);
            Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(Intent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }else{
            Intent login = new Intent(this,HomeActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }
    }
    public void profileButtonClick(View view) {
        if(!login_email.equals("")){
            Intent Intent = new Intent(this, LoginedActivity.class);
            Intent.putExtra("login_email",login_email);
            startActivity(Intent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }else{
            Intent login = new Intent(this,LoginActivity.class);
            startActivity(login);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
        }
    }
    private void initAdapter() {
        loadingDialog.dismissDialog();
        mAdapter = new ItemAdapter(items,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initScrollListener() {
        loading = false;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!loading) {
                    if (mLayoutManager != null && mLayoutManager.findLastCompletelyVisibleItemPosition() == items.size() - 1) {
                        loadMore();
                        loading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        if(items.size() != itemsTotal.size()) {
            items.add(null);
            mAdapter.notifyItemInserted(items.size() - 1);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    items.remove(items.size() - 1);
                    scrollPosition = items.size();
                    mAdapter.notifyItemRemoved(scrollPosition);
                    currentSize = scrollPosition;
                    nextLimit = currentSize + 10;
                    if (itemsTotal.size() < nextLimit) {
                        nextLimit = itemsTotal.size() - 1;
                    }
                    while (currentSize - 1 < nextLimit) {
                        Item item = new Item();
                        item.setTitle(itemsTotal.get(currentSize).getTitle());
                        item.setPrice(itemsTotal.get(currentSize).getPrice());
                        item.setAddress(itemsTotal.get(currentSize).getAddress());
                        item.setArrival(itemsTotal.get(currentSize).getArrival());
                        item.setImg(itemsTotal.get(currentSize).getImg());
                        item.setImg1(itemsTotal.get(currentSize).getImg1());
                        item.setImg2(itemsTotal.get(currentSize).getImg2());
                        item.setImg3(itemsTotal.get(currentSize).getImg3());
                        item.setImg4(itemsTotal.get(currentSize).getImg4());
                        item.setImg5(itemsTotal.get(currentSize).getImg5());
                        item.setPhone(itemsTotal.get(currentSize).getPhone());
                        item.setDescription(itemsTotal.get(currentSize).getDescription());
                        item.setId(itemsTotal.get(currentSize).getId());
                        item.setUser_id(itemsTotal.get(currentSize).getUser_id());
                        items.add(item);
                        currentSize++;
                    }
                    mAdapter.notifyDataSetChanged();
                    loading = false;
                }
            }, 1000);
        }
    }
}