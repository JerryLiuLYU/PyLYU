package com.example.compus;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.compus.Adapter.SeekAdapter;
import com.example.compus.ViewModel.SeekViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cn.leancloud.AVObject;

public class SeekFragment extends Fragment {
    private FloatingActionButton floatingButton;
    private SeekViewModel mViewModel;
//    private EditText newSeekEt;
    private SeekAdapter seekAdapter;
    private RecyclerView recycleView;

    public static SeekFragment newInstance() {
        return new SeekFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.seek_fragment, container, false);
        floatingButton = view.findViewById(R.id.floatingActionButtonSeek);
        recycleView = view.findViewById(R.id.recycleView_seek);
        mViewModel = new ViewModelProvider(getActivity()).get(SeekViewModel.class);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //本地存储文件
        SharedPreferences sp = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        seekAdapter = new SeekAdapter(mViewModel.dataListLive.getValue());
        seekAdapter.updateList(mViewModel.dataListLive.getValue());
        // setAdapter 设置适配器
        recycleView.setAdapter(seekAdapter);
        // setLayoutManager 设置RecyclerView的形式
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycleView.setLayoutManager(layoutManager);
        // addItemDecoration 相当于装饰器 可以实现分割线
        recycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        //添加监控器
        mViewModel.dataListLive.observe(getViewLifecycleOwner(), new Observer<List<AVObject>>() {
            @Override
            public void onChanged(List<AVObject> avObjects) {
//                Toast.makeText(getActivity(),"监听成功",Toast.LENGTH_SHORT).show();
                seekAdapter.updateList(avObjects);
                seekAdapter.notifyDataSetChanged();
            }
        });
        // TODO: Use the ViewModel

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view =  LayoutInflater.from(getActivity()).inflate(R.layout.new_addseek,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("添加")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText newSeekEt = view.findViewById(R.id.editTextTextPersonName);
                                final String newSeek = newSeekEt.getText().toString();
                                mViewModel.addSeek(newSeek);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });
    }


}