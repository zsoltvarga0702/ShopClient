package com.example.shopclient.Tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopclient.Activities.LoginActivity;
import com.example.shopclient.Adapters.LoadingDialog;
import com.example.shopclient.Adapters.ServerDialog;
import com.example.shopclient.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registerTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registerTab extends Fragment {
    //String ip = "http://192.168.0.143:45455/";
    String ip = "http://192.168.100.58:45455/";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText nameText,emailText,passwordText;
    final LoadingDialog loadingDialog= new LoadingDialog(getActivity());
    final ServerDialog serverDialog= new ServerDialog(getActivity());

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registerTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registerTab.
     */
    // TODO: Rename and change types and number of parameters
    public static registerTab newInstance(String param1, String param2) {
        registerTab fragment = new registerTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_tab, container, false);
        emailText = view.findViewById(R.id.editTextEmail);
        passwordText = view.findViewById(R.id.editTextPassword);
        nameText = view.findViewById(R.id.editTextName);
        Button button = (Button)view.findViewById(R.id.registerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if(name.equals("") || email.equals("") || password.equals("")){
                    setToastMessage("All field required!",getContext());
                }else {
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", name)
                            .addFormDataPart("email", email)
                            .addFormDataPart("password", password)
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(ip+"api/users")
                            .post(requestBody)
                            .addHeader("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIkMnkkMTAkZzZrLkwySlFCZlBmN1RTb3g3bmNpTzltcVwvemRVN2JtVC42SXN0SFZtbzZHNlFNSkZRWWRlIiwic3ViIjo0NSwiaWF0IjoxNTUwODk4NDc0LCJleHAiOjE1NTM0OTA0NzR9.tefIaPzefLftE7q0yKI8O87XXATwowEUk_XkAOOQzfw")
                            .addHeader("cache-control", "no-cache")
                            .addHeader("Postman-Token", "7e231ef9-5236-40d1-a28f-e5986f936877")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setToastMessage("Succesfully registered!",getContext());
                                        Intent loginIntent = new Intent(getContext() , LoginActivity.class);
                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(loginIntent);
                                    }
                                });
                            }else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setToastMessage(myResponse,getContext());
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });
        return view;

    }
    public void setToastMessage(String message, Context context){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.my_toast, (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(context);
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
}