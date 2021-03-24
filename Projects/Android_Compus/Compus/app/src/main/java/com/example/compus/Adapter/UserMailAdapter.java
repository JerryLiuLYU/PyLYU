package com.example.compus.Adapter;

import android.content.Context;
import android.text.style.UpdateAppearance;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compus.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.AVIMMessageManager;
import cn.leancloud.im.v2.messages.AVIMTextMessage;

public class UserMailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<AVIMMessage> messageList = null;
    String ObjectIdRe;

    public UserMailAdapter(List<AVIMMessage> list, String ObjectIdRe) {
        this.messageList = list;
        this.ObjectIdRe = ObjectIdRe;
    }

    public void UpdateAdapter(List<AVIMMessage> list, String ObjectIdRe){
        this.messageList = list;
        this.ObjectIdRe = ObjectIdRe;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE.TYPE_FRIEND_MSG.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.otherpeopleemail, parent, false);
            return new ToMegHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypeopleemail, parent, false);
            return new FromMegHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AVIMMessage message = messageList.get(position);
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE.TYPE_FRIEND_MSG.ordinal()){
//            ((ToMegHolder)holder).textView.setText(((AVIMTextMessage)message).getText());
            ((ToMegHolder)holder).textView.setText(((AVIMTextMessage) messageList.get(position)).getText());
        }else {
//            ((FromMegHolder)holder).textView.setText(((AVIMTextMessage)message).getText());
            ((FromMegHolder)holder).textView.setText(((AVIMTextMessage) messageList.get(position)).getText());
        }
    }

    @Override
    public int getItemCount() {
        try {
            return messageList.size();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //定义一个枚举，分别表示接受消息和发送消息
    public enum ITEM_TYPE {
        TYPE_FRIEND_MSG,
        TYPE_ME_MSG
    }

    @Override
    public int getItemViewType(int position) {
        if (ObjectIdRe.equals(messageList.get(position).getFrom())) {
            return ITEM_TYPE.TYPE_ME_MSG.ordinal();
        } else {
            return ITEM_TYPE.TYPE_FRIEND_MSG.ordinal();
        }
    }

    //发送消息的ViewHolder
    public static class FromMegHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public FromMegHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_mypeople);
        }
    }

    public static class ToMegHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ToMegHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_otherpeople);
        }
    }

}
