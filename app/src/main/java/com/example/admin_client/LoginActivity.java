package com.example.admin_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface LoginService {
    @POST("login")
    Call<ResponseBody> login(@Body JsonObject body);
}

public class LoginActivity extends AppCompatActivity {
    String TAG = LoginActivity.class.getSimpleName();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    LoginService service = retrofit.create(LoginService.class);
    JsonParser jsonParser = new JsonParser();

    Button login_button;
    EditText id_input, pwd_input;
    CheckBox auto_login_box;

    String store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = findViewById(R.id.login_process_button);
        auto_login_box = findViewById(R.id.auto_login_check);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("auto_login", auto_login_box.isChecked()).apply();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });
    }

    void login() {
        id_input = findViewById(R.id.login_id_input);
        pwd_input = findViewById(R.id.login_pwd_input);
        JsonObject body = new JsonObject();
        body.addProperty("id", id_input.getText().toString());
        body.addProperty("password", pwd_input.getText().toString());

        Call<ResponseBody> call = service.login(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject res =  (JsonObject) jsonParser.parse(response.body().string());


                        String access_token = res.get("access_token").getAsString();
                        if (access_token.equals("id_false")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "아이디를 확인해주세요.", Toast.LENGTH_LONG);
                            toast.show();
                        } else if (access_token.equals("pwd_false")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            String refresh_token = res.get("refresh_token").getAsString();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("access_token", access_token).apply();
                            editor.putString("refresh_token", refresh_token).apply();

                            if (res.get("storeId") != null) {
                                String storeId = res.get("storeId").getAsString();
                                editor.putString("storeId", storeId).apply();
                            }
                        }
                        Log.d(TAG, res.get("access_token").getAsString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Log.d(TAG, "Fail " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: e " + t.getMessage());
            }
        });
    }
}