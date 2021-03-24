package com.example.compus;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.compus.Adapter.FriendShipAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cn.leancloud.AVFriendshipRequest;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class FriendShip_new extends Activity {

    RecyclerView recyclerView;
    FloatingActionButton btn;
    final int position = -1;
    List<AVFriendshipRequest> friendshipRequestList;
    private FriendShipAdapter shipAdapter;

    public List<AVUser> getUserList() {
        return userList;
    }

    public void setUserList(List<AVUser> userList) {
        this.userList = userList;
    }

    List<AVUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ship_new);
        Init();
        AVUser avUser = AVUser.getCurrentUser();
        //获取好友申请列表
        if (avUser != null) {
            //有账户正在登录，执行逻辑
            //获取好友申请列表
            avUser.friendshipRequestQuery(AVFriendshipRequest.STATUS_PENDING, true, true)
                    .findInBackground().subscribe(new Observer<List<AVFriendshipRequest>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    Log.i("tags_newFriendShip", "开始查询");
                }

                @Override
                public void onNext(@NonNull List<AVFriendshipRequest> avFriendshipRequests) {
                    Log.i("tags_newFriendShip", "已经获取到查询结果");
                    friendshipRequestList = avFriendshipRequests;
//                    Log.i("tags_newFriendShip", avFriendshipRequests.get(0).getObjectId());
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

            //是否订阅好友申请列表？

            shipAdapter = new FriendShipAdapter(friendshipRequestList,getUserList(),avUser);
            shipAdapter.updateFriendshipRequest(friendshipRequestList,getUserList());
            recyclerView.setAdapter(shipAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            shipAdapter.notifyDataSetChanged();
        } else {
        }
        //Adapter点击事件
//        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.new_addseek, null);

        //刷新界面
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag_FriendShip", "刷新界面");
                onCreate(null);
            }
        });
    }

    //获取用户列表
    private void getUserList(AVUser avUser) {
        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("Queryblankline", "null");
        query.findInBackground().subscribe(new Observer<List<AVUser>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull List<AVUser> userList) {
                setUserList(userList);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    //控件初始化
    public void Init() {
        recyclerView = findViewById(R.id.RecycleView_newFriendship);
        btn = findViewById(R.id.floatingActionButton_newFriendShip);
    }

}