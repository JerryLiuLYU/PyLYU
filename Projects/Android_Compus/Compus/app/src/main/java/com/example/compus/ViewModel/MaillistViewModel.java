package com.example.compus.ViewModel;

import android.app.Application;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVFriendship;
import cn.leancloud.AVFriendshipRequest;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.livequery.AVLiveQuery;
import cn.leancloud.livequery.AVLiveQueryEventHandler;
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MaillistViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<List<AVObject>> UserList = new MutableLiveData<List<AVObject>>();   //用户列表,为避免内存泄露，修改数据类型AVUser->AVObject
    public MutableLiveData<List<AVFriendship>> FriendList = new MutableLiveData<List<AVFriendship>>(); //好友列表

    public MaillistViewModel(@NonNull Application application) {
        super(application);

        AVUser currentUser = AVUser.getCurrentUser();
        //用户表的查询
        AVQuery<AVObject> userAVQuery = new AVQuery<>("_User");
        userAVQuery.whereEqualTo("Queryblankline", "null");
        userAVQuery.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                Log.i("tag_MailList", "提交查询用户列表请求-----MailListViewModel");
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<AVObject> list) {
                Log.i("tag_MailList", "查询用户列表成功-----MailListViewModel");
                UserList.setValue(list);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.i("tag_MailList", "查询用户列表失败-----MailListViewModel");
            }

            @Override
            public void onComplete() {

            }
        });
        //订阅用户表
        AVLiveQuery userLiveQuery = AVLiveQuery.initWithQuery(userAVQuery);
        userLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectCreated(AVObject avObject) {
                super.onObjectCreated(avObject);
                Log.i("tag_MailList", "_User表有创建动作" + avObject.get("username") + "-----MailListViewModel");
                List<AVObject> objects = UserList.getValue();
                objects.add(avObject);
                UserList.setValue(objects);
            }

            @Override
            public void onObjectDeleted(String objectId) {
                super.onObjectDeleted(objectId);
                Log.i("tag_MailList", "_User表有删除动作-----MailListViewModel");
                int i = 0;
                List<AVObject> objects = UserList.getValue();
                for (AVObject object : objects) {
                    if (object.getObjectId().equals(objectId)) {
                        objects.remove(i);
                        Log.i("tag_MailList-----" + i, objectId + "-----MailListViewModel");
                    }
                    i++;
                }
//                for (AVObject object : objects) {
//                    Log.i("tag_MailList", object.get("username")+object.getObjectId() + "-----MailListViewModel");
//                }
                UserList.setValue(objects);
            }
        });
        userLiveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.i("tag_MailList", "订阅成功用户表-----MailListViewModel");
                }
            }
        });

//        好友列表的查询
        AVQuery<AVFriendship> friendshipAVQuery = currentUser.friendshipQuery(false);
        friendshipAVQuery.whereEqualTo(AVFriendship.ATTR_FRIEND_STATUS, true);
        friendshipAVQuery.addDescendingOrder(AVObject.KEY_UPDATED_AT);
        friendshipAVQuery.findInBackground().subscribe(new Observer<List<AVFriendship>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                Log.i("tag_MailList", "提交查询好友列表请求-----MailListViewModel");
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<AVFriendship> list) {
                Log.i("tag_MailList", "查询好友列表成功-----MailListViewModel");
                FriendList.setValue(list);
//                Log.i("tag_MailList",list.get(0).getCreatedAt().toString());
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.i("tag_MailList", "提交查询好友列表失败-----MailListViewModel");
            }

            @Override
            public void onComplete() {

            }
        });
        //订阅好友列表
        AVLiveQuery friendLiveQuery = AVLiveQuery.initWithQuery(friendshipAVQuery);
        friendLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectEnter(AVObject avObject, List<String> updateKeyList) {
                super.onObjectEnter(avObject, updateKeyList);
                AVFriendship avFriendship = new AVFriendship();
                avFriendship.put("user",avObject.get("user"));
                avFriendship.put("ObjectId",avObject.getObjectId());
//                avFriendship.put("ACL",avObject.get("ACL"));
                avFriendship.put("followee",avObject.get("followee"));
                avFriendship.put("friendStatus",avObject.get("friendStatus"));
//                avFriendship.put("createdAt",avObject.getCreatedAt());
//                avFriendship.put("updatedAt",avObject.getUpdatedAt());
//                for (String s : updateKeyList) {
//                    Log.i("tag_mailList", s + "-----mailListViewModel---updateKey");
//                }
//                Log.i("tag_mailList", avObject.getObjectId() + "-----mailListViewModel");
                List<AVFriendship> avFriendships = FriendList.getValue();
                avFriendships.add(avFriendship);
                FriendList.setValue(avFriendships);
            }
        });
        friendLiveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.i("tag_MailList", "订阅好友列表成功-----MailListViewModel");
                }
            }
        });
    }
}