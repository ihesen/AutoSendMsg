package com.ihesen.autosendmsg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EMAIL = "email";

    private EditText mEdtEmail;
    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setLinstener();
    }

    private void findView() {
        mEdtEmail = (EditText) findViewById(R.id.et_email);
        mBtnStart = (Button) findViewById(R.id.btn_start);
    }

    private void setLinstener() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEdtEmail.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    getSharedPreferences(EMAIL, MODE_PRIVATE).edit().putString(EMAIL, email).commit();
                    Toast.makeText(MainActivity.this, "开启成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "接受信息邮箱地址不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
