package com.example.compus.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compus.R;

import java.util.List;

import cn.leancloud.AVObject;

public class SeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private TextView Publisher,Grade,Time;
    List<AVObject> list;

    public SeekAdapter(List<AVObject> list) {
        this.list = list;
    }

    public void updateList(List<AVObject> newList){
        list = newList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_seek,parent,false);
        return new RecyclerView.ViewHolder(viewHolder) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AVObject object = list.get(position);
        Publisher = holder.itemView.findViewById(R.id.textViewholderseek);
        Grade = holder.itemView.findViewById(R.id.textViewholderseek2);
        Time = holder.itemView.findViewById(R.id.textViewholderseek3);

        Publisher.setText(object.getString("Publisher"));
        Grade.setText(object.getString("Grade"));
        Time.setText(object.getCreatedAtString().substring(0,10));
    }

    @Override
    public int getItemCount() {
        try{
            return list.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
