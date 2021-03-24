package com.example.compus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.compus.Adapter.UserMailAdapter;

import java.util.Arrays;
import java.util.List;

import cn.leancloud.AVUser;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMConversationEventHandler;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.AVIMMessageHandler;
import cn.leancloud.im.v2.AVIMMessageManager;
import cn.leancloud.im.v2.Conversation;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback;
import cn.leancloud.im.v2.callback.AVIMMessagesQueryCallback;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class UserMailActivity extends AppCompatActivity {

    private String ObjectIdRe;
    private String ObjectIdAc;
    private String ObjectNameAc;
    private UserMailAdapter userMailAdapter;
    EditText editText;
    Button button;
    RecyclerView recyclerView;
    TextView textView;
    private AVIMConversation conversation;    //对话
    private List<AVIMMessage> messageList;      //聊天记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mail);
        Init();
//打开文件操作
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
//获取会话信息
        Intent intent = this.getIntent();
        ObjectIdRe = intent.getStringExtra("conversionObjectIdRe");
        ObjectIdAc = intent.getStringExtra("conversationObjectIdAc");
        ObjectNameAc = intent.getStringExtra("conversationObjectNameAc");
        textView.setText(ObjectNameAc);
//创建会话
        AVUser.logIn(getUserName(sharedPreferences), getPassword(sharedPreferences)).subscribe(new Observer<AVUser>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull AVUser avUser) {
                AVIMClient client = AVIMClient.getInstance(avUser);
                client.open(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient client, AVIMException e) {
                        client.createConversation(Arrays.asList(ObjectIdAc), ObjectNameAc, null, false, true,
                                new AVIMConversationCreatedCallback() {
                                    @Override
                                    public void done(AVIMConversation conversation, AVIMException e) {
                                        Log.i("tag_UserMail", "获取到会话框---------------UserMailActivity");
                                        setConversation(conversation);
                                        findingMessagesList(getConversation());
                                    }
                                });
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
//创建Adapter
        userMailAdapter = new UserMailAdapter(getMessageList(),ObjectIdRe);
        userMailAdapter.UpdateAdapter(getMessageList(),ObjectIdRe);
        recyclerView.setAdapter(userMailAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

//发送消息，获取聊天记录
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() != 0) {
                    AVIMTextMessage msg = new AVIMTextMessage();
                    msg.setText(editText.getText().toString());
                    getConversation().sendMessage(msg, new AVIMConversationCallback() {
                        @Override
                        public void done(AVIMException e) {
                            if (e == null) {
                                Log.i("tag_UserMail", editText.getText().toString() + "发送成功");
                                findingMessagesList(getConversation());
                                userMailAdapter.UpdateAdapter(getMessageList(),ObjectIdRe);
                                userMailAdapter.notifyDataSetChanged();
                                editText.setText("");
                            }
                        }
                    });

                }
            }
        });
//被拉进会话提示
        AVIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());
        //会话消息提示
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());

    }

    public String getUserName(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("name", null);
    }

    public String getPassword(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("password", null);
    }

    public String getObjectId(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("odId", null);
    }

    public List<AVIMMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<AVIMMessage> messageList) {
        this.messageList = messageList;
    }

    public void setConversation(AVIMConversation conversation) {
        this.conversation = conversation;
    }

    public AVIMConversation getConversation() {
        return conversation;
    }

    //控件初始化
    public void Init() {
        editText = findViewById(R.id.editTextTextPersonName2);
        button = findViewById(R.id.buttonUserMail);
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.textView);
    }

    //被邀请到对话
    public class CustomConversationEventHandler extends AVIMConversationEventHandler {
        @Override
        public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {

        }

        @Override
        public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {

        }

        @Override
        public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {

        }

        @Override
        public void onInvited(AVIMClient client, AVIMConversation conversation, String invitedBy) {
            // 当前 clientId（Jerry）被邀请到对话，执行此处逻辑
            Log.i("tag_UserMail", "被邀请到对话");
        }
    }

    //获取聊天信息
    public class CustomMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            super.onMessage(message, conversation, client);
            if (message instanceof AVIMTextMessage) {
                Log.d("tag_UserMail", ((AVIMTextMessage) message).getText());
                findingMessagesList(getConversation());
                userMailAdapter.UpdateAdapter(getMessageList(),ObjectIdRe);
                userMailAdapter.notifyDataSetChanged();
            }
        }
    }

    public void findingMessagesList(AVIMConversation conversation) {
        //查询聊天记录函数
        conversation.queryMessages(10, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> messages, AVIMException e) {
                if (e == null) {
                    Log.i("tag_UserMail", "成功返回");
                    Log.d("tag_UserMail", String.valueOf(messages.size()));
                    if (messages.size() - 10 > 0) {
                        setMessageList(messages.subList(messages.size() - 10, messages.size()));
                        Log.d("tag_UserMail", ((AVIMTextMessage) messages.get(messages.size() - 1)).getText() + "空行");
                        Log.i("tag_UserMail", messages.get(messages.size() - 1).toString());
                    } else {
                        setMessageList(messages.subList(0, messages.size()));
                        Log.d("tag_UserMail", ((AVIMTextMessage) messages.get(messages.size() - 1)).getText() + "空行");
                        Log.i("tag_UserMail", (messages.get(messages.size() - 1)).getFrom());
                    }
//                    AVIMMessage oldestMessage = messages.get(0);
//                    conversation.queryMessages(oldestMessage.getMessageId(), oldestMessage.getTimestamp(), 20,
//                            new AVIMMessagesQueryCallback() {
//                                @Override
//                                public void done(List<AVIMMessage> messagesInPage, AVIMException e) {
//                                    if (e == null){
//                                        Log.d("tag_UserMail",messagesInPage.size()+"messages");
//                                        setMessageList(messagesInPage);
////                                        Log.d("tag_UserMail",messagesInPage.get(0).toString()+"空行");
//                                    }else {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
                }
            }
        });
    }



}