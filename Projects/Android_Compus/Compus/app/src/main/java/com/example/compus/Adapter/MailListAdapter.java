package com.example.compus.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compus.R;
import com.example.compus.UserMailActivity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.leancloud.AVFriendship;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static cn.leancloud.AVOSCloud.getContext;

public class MailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context = getContext();
    private TextView textView;
    private List<AVObject> UserList;
    private List<AVFriendship> friendshipList;
    SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
    final SharedPreferences.Editor editor = sharedPreferences.edit();

    public MailListAdapter(List<AVObject> list, List<AVFriendship> shipList) {
        this.UserList = list;
        this.friendshipList = shipList;
//        Log.i("tag_mailList",list.getValue().get(0).get("username").toString()+"-----mailListAdapter");
    }

    public void updateList(List<AVObject> list, List<AVFriendship> shipList) {
        this.UserList = list;
        this.friendshipList = shipList;
//        Log.i("tag_mailList",userList.get(0).getUsername().toString()+"-----mailListAdapter");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_maillist, parent, false);
        return new RecyclerView.ViewHolder(viewHolder) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AVFriendship avObject = friendshipList.get(position);
        textView = holder.itemView.findViewById(R.id.textViewholdermaillist);
        Log.i("tag_mailList", avObject.get("followee").toString() + "-----mailListAdapter****************");
        String[] strings = avObject.get("followee").toString().split("[= : , \" } {]");
        int j = 0;
        String userName = null;
        String ObjectID = null;
        for (AVObject user : UserList) {
            Log.i("tag_mailList", "第" + j + "轮******************************************************");
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].equals(user.getObjectId())) {//9//24
                    textView.setText(user.get("username").toString());
                    userName = user.get("username").toString();
                    ObjectID = user.getObjectId();
                    Log.i("tag_mailList", user.get("username").toString() + "++++++++++++++++++++++++++");
                }
                Log.i("tag_mailList", strings[i].toString() + "-----mailListAdapter----------" + i);
            }
//            for (String string : strings) {
//                Log.i("tag_mailList",string+"-----mailListAdapter---------"+i);
//                i++;
//            }
        }
//        textView.setText(avObject.getObjectId().toString());
        String finalUserName = userName;
        String finalObjectID = ObjectID;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), UserMailActivity.class);
                View view = LayoutInflater.from(getContext()).inflate(R.layout.new_addseek, null, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(finalUserName)
                        .setView(view)
                        .setPositiveButton("发起会话", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AVUser.logIn(getUserName(), getPassword()).subscribe(new Observer<AVUser>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull AVUser avUser) {
                                        AVIMClient client = AVIMClient.getInstance(avUser);
                                        client.open(new AVIMClientCallback() {
                                            @Override
                                            public void done(AVIMClient client, AVIMException e) {
                                                Log.i("tag_mailList", "登录即时通讯服务器-----mailListAdapter----------");
                                                client.createConversation(Arrays.asList(finalObjectID), finalUserName, null, false, true,
                                                        new AVIMConversationCreatedCallback() {
                                                            @Override
                                                            public void done(AVIMConversation conversation, AVIMException e) {
                                                                if (e == null) {
                                                                    Log.i("tag_mailList", "对话创建成功-----mailListAdapter");
                                                                    //intent不能传递conversion类，将聊天室姓名传递，通过名字新建,传递本账户ID与好友ID
                                                                    intent.putExtra("conversionObjectIdRe", getObjectId());
                                                                    intent.putExtra("conversationObjectIdAc",finalObjectID);
                                                                    intent.putExtra("conversationObjectNameAc",finalUserName);
                                                                    v.getContext().startActivity(intent);
//                                                                    AVIMTextMessage msg = new AVIMTextMessage();
//                                                                    msg.setText("杨康是个二傻子");
//                                                                    conversation.sendMessage(msg, new AVIMConversationCallback() {
//                                                                        @Override
//                                                                        public void done(AVIMException e) {
//                                                                            if (e==null) {
//                                                                                Log.i("tag_mailList", "消息发送成功-----mailListAdapter");
//                                                                            }
//                                                                        }
//                                                                    });
                                                                } else {
                                                                    Toast.makeText(v.getContext(), "请检查网络设置", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
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

    public String getUserName() {
        return sharedPreferences.getString("name", null);
    }

    public String getPassword() {
        return sharedPreferences.getString("password", null);
    }

    public String getObjectId() {
        return sharedPreferences.getString("odId", null);
    }

    @Override
    public int getItemCount() {
        try {
            return friendshipList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}
