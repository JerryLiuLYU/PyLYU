package com.example.compus;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.compus.ViewModel.PersonalcenterViewModel;

import cn.leancloud.AVUser;

public class PersonalcenterFragment extends Fragment {

    private Button btn;
    private TextView tvname;
    private PersonalcenterViewModel mViewModel;

    public static PersonalcenterFragment newInstance() {
        return new PersonalcenterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.personalcenter_fragment, container, false);
        btn = view.findViewById(R.id.buttonpersonSetout);
        tvname = view.findViewById(R.id.textViewpersonSetoutname);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PersonalcenterViewModel.class);
        // TODO: Use the ViewModel

        //信息保存
//        SharedPreferences sp = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        String name = sp.getString("name","");
//        String odId = sp.getString("odId","");

        //获取用户信息   //目前只获取了名称
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {           //如果有登录账号即获取信息
            tvname.setText(currentUser.getUsername().toString());
        } else {                            //没有则退出到注册页面
            startActivity(new Intent(getActivity(),SetupActivity.class));
        }

        //登出按钮
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.logOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });


    }


}