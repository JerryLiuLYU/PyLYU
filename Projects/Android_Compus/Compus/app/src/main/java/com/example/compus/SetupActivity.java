package com.example.compus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SetupActivity extends AppCompatActivity {

    TextInputEditText etname,etpsd,etmail,etphone;
    String name,psd,mail,phone;
    Button btn;
    ProgressBar BarSetup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        //控件初始化
        Init();

        //“注册且登录按钮”失效，加载bar隐藏
        btn.setEnabled(false);
        BarSetup.setVisibility(View.GONE);

        //为edittext添加失去焦点事件
        //name行
        etname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = etname.getText().toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //psd行
        etpsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                psd = etpsd.getText().toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //mail行
        etmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mail = etmail.getText().toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //phone行
        etphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = etphone.getText().toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //按钮实现
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser user = new AVUser();
                // 等同于 user.put("username", "Tom")
                user.setUsername(name);
                user.setPassword(psd);
//                user.setEmail(mail);
//                user.setMobilePhoneNumber(phone);
//                user.put("gender", "secret");

                user.signUpInBackground().subscribe(new Observer<AVUser>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onNext(AVUser user) {
                        // 注册成功
                        Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                        BarSetup.setVisibility(View.VISIBLE);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        }

                    public void onError(Throwable throwable) {
                        // 注册失败（通常是因为用户名已被使用）
                        Toast.makeText(getApplicationContext(),"注册失败,用户名已被使用",Toast.LENGTH_SHORT).show();
                    }

                    public void onComplete() {}
                });
            }
        });
    }
    //控件初始化函数
    public void Init(){
        etname = findViewById(R.id.editTextTextPersonNameSetupname);
        etpsd = findViewById(R.id.editTextTextPersonNameSetuppsd);
        etphone = findViewById(R.id.editTextTextPersonNameSetupphone);
        etmail = findViewById(R.id.editTextTextPersonNameSetupemail);
        btn = findViewById(R.id.buttonSetup);
        BarSetup = findViewById(R.id.progressBarSetup);
        name=psd=mail=phone = "";
    }

    //判断是否将button，ProgressBar启动
    public void check(){
        if(name.length() > 0 && psd.length() > 0 && mail.length() > 0 && phone.length() > 0) {
            btn.setEnabled(true);
        }else{
            btn.setEnabled(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}