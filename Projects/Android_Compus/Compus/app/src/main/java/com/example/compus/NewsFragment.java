package com.example.compus;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.compus.ViewModel.NewsViewModel;

public class NewsFragment extends Fragment {

    private NewsViewModel mViewModel;
    private Button btn_friend;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(NewsViewModel.class);
        btn_friend = view.findViewById(R.id.button_new);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        //建立文件读写连接
        SharedPreferences sp = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),FriendShip_new.class));
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}