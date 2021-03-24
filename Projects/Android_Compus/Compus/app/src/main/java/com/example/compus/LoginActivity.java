package com.example.compus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
    Button btnsetin,btnsetup;
    TextInputEditText etName,etPsd;
    String name,psd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //控件初始化
        Init();

        //保存数据前置----------------------------------------------保存密码？
        SharedPreferences sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("age", 11);
//        name = sp.getString("name","");
//        editor.apply();
        etName.setText("11");
        etPsd.setText("1");
        //登录按钮
        btnsetin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
//                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                psd = etPsd.getText().toString();
                if (name.length()>0 && name !=null && psd.length()>0 && psd !=null) {
                    AVUser.logIn(name, psd).subscribe(new Observer<AVUser>() {
                        public void onSubscribe(Disposable disposable) {}

                        public void onNext(AVUser user) {
                            // 登录成功
                            editor.putString("name", name);
                            editor.putString("odId", user.getObjectId());
                            editor.putString("password",psd);
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        public void onError(Throwable throwable) {
                            // 登录失败（可能是密码错误）
                            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        }

                        public void onComplete() {
                        }
                    });
                }else if (name.length() == 0 || name == null){
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                }else if (psd.length() == 0 || psd == null){
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册按钮
        btnsetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SetupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //自行读取账号密码且自行登录

    }

    //控件初始化
    public void Init(){
        btnsetin = findViewById(R.id.buttonLoginSetin);
        btnsetup = findViewById(R.id.buttonLoginSetup);
        etName = findViewById(R.id.editTextLoginPersonNameLogin);
        etPsd = findViewById(R.id.editTextLoginPasswordLogin);
        name = psd = "";
    }
}