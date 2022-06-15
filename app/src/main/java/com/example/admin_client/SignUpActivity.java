package com.example.admin_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface SignUpService {
    @POST("signup/id-check")
    Call<ResponseBody> checkId(@Body JsonObject body);

    @POST("signup/phone-check")
    Call<ResponseBody> checkPhone(@Body JsonObject body);

    @POST("signup/phone-check/verify")
    Call<ResponseBody> phoneVerify(@Body JsonObject body);

    @POST("signup")
    Call<ResponseBody> signUp(@Body JsonObject body);

}

public class SignUpActivity extends AppCompatActivity {
    String TAG = SignUpActivity.class.getSimpleName();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    SignUpService service = retrofit.create(SignUpService.class);
    JsonParser jsonParser = new JsonParser();

    Button phone_verify_btn, code_verify_btn, id_verify_btn, signup_btn;
    EditText phone_number, code_verify_input, id_input, pwd_input, pwd_check_input, name_input;
    TextView code_verify_txt, id_verify_txt, pwd_verify_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phone_verify_btn = findViewById(R.id.verify_button);
        phone_number = findViewById(R.id.phone_number_input);
        phone_verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCheck(phone_number.getText().toString());
            }
        });

        code_verify_btn = findViewById(R.id.code_verify_button);
        code_verify_input = findViewById(R.id.code_verify_input);
        code_verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneVerify(code_verify_input.getText().toString(), phone_number.getText().toString());
            }
        });

        id_verify_btn = findViewById(R.id.id_verify_button);
        id_input = findViewById(R.id.id_input);
        id_verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheck(id_input.getText().toString());
            }
        });

        pwd_input = findViewById(R.id.pwd_input);
        pwd_check_input = findViewById(R.id.pwd_check_input);
        pwd_verify_txt = findViewById(R.id.pwd_check_state);
        pwd_check_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "pwd test");
                if (pwd_input.getText().toString().equals(pwd_check_input.getText().toString())) {
                    pwd_verify_txt.setText("O");
                } else {
                    pwd_verify_txt.setText("X");
                }
                Log.d(TAG, pwd_verify_txt.getText().toString());
            }
        });

        signup_btn = findViewById(R.id.signup_process_button);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code_verify_txt = findViewById(R.id.code_verify_txt);
                id_verify_txt = findViewById(R.id.id_verify_txt);

                if (code_verify_txt.getText().equals("인증됐습니다.") && id_verify_txt.getText().equals("사용할 수 있는 이메일입니다.")) {
                    if (pwd_verify_txt.getText().toString().equals("O")) {
                        signUp(id_input.getText().toString(), pwd_input.getText().toString());
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "아이디와 전화번호를 확인해주세요.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    void phoneCheck(String phone_number) {
        JsonObject body = new JsonObject();
        body.addProperty("phone_number", phone_number);

        Call<ResponseBody> call = service.checkPhone(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject res =  (JsonObject) jsonParser.parse(response.body().string());
                        Log.d(TAG, res.get("msg").getAsString());
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

    void phoneVerify(String code, String phone_number) {
        code_verify_txt = findViewById(R.id.code_verify_txt);

        JsonObject body = new JsonObject();
        body.addProperty("phone_number", phone_number);
        body.addProperty("code", code);

        Call<ResponseBody> call = service.phoneVerify(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonObject res = (JsonObject) jsonParser.parse(response.body().string());
                    if (res.get("phone_valid").getAsBoolean()) {
                        code_verify_txt.setText("인증됐습니다.");
                    } else {
                        code_verify_txt.setText("다시 시도해주세요.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void idCheck(String id) {
        id_verify_txt = findViewById(R.id.id_verify_txt);

        JsonObject body = new JsonObject();
        body.addProperty("id", id);

        Call<ResponseBody> call = service.checkId(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject res =  (JsonObject) jsonParser.parse(response.body().string());
                        if (res.get("is_valid").getAsBoolean()) {
                            id_verify_txt.setText("사용할 수 있는 이메일입니다.");
                        } else {
                            id_verify_txt.setText("사용할 수 없는 이메일입니다.");
                        }
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

    void signUp(String id, String pwd) {
        id_input = findViewById(R.id.id_input);
        pwd_input = findViewById(R.id.pwd_input);
        name_input = findViewById(R.id.name_input);
        phone_number = findViewById(R.id.phone_number_input);

        JsonObject body = new JsonObject();
        body.addProperty("id", id);
        body.addProperty("password", pwd);
        body.addProperty("nickname", name_input.getText().toString());
        body.addProperty("phone_number", phone_number.getText().toString());

        Call<ResponseBody> call = service.signUp(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject res =  (JsonObject) jsonParser.parse(response.body().string());
                        Log.d(TAG, res.get("id").getAsString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
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