package com.example.compus.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compus.R;

import java.util.List;

import cn.leancloud.AVFriendshipRequest;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static cn.leancloud.AVOSCloud.getContext;

public class FriendShipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<AVFriendshipRequest> avFriendshipRequest;           //好友申请列表
    List<AVUser> avUsers;                                    //用户列表
    AVUser avUser;
    private TextView name, time;

    public FriendShipAdapter(List<AVFriendshipRequest> avFriendshipRequest, List<AVUser> user, AVUser avUser) {
        this.avFriendshipRequest = avFriendshipRequest;
        this.avUsers = user;
        this.avUser = avUser;
    }

    public void updateFriendshipRequest(List<AVFriendshipRequest> avFriendshipRequest, List<AVUser> user) {
        this.avFriendshipRequest = avFriendshipRequest;
        this.avUsers = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_friendship, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AVFriendshipRequest friendshipRequest = avFriendshipRequest.get(position);
        name = holder.itemView.findViewById(R.id.textView_FriendShipname);
        time = holder.itemView.findViewById(R.id.textView_FriendShiptime);

        name.setText(friendshipRequest.getObjectId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.new_addseek,null ,false);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("是否同意？")
                        .setView(view)
                        .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                avUser.acceptFriendshipRequest(friendshipRequest, null).subscribe(new Observer<AVFriendshipRequest>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                        Log.i("tags_friendship", "开始同意好友申请-----------------FriendShipAdapter");
                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull AVFriendshipRequest avFriendshipRequest) {
                                        Log.i("tags_friendship", "成功同意好友申请-----------------FriendShipAdapter");
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Log.i("tags_friendship", "同意好友申请成功-----------------FriendShipAdapter");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                            }
                        }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        avUser.declineFriendshipRequest(friendshipRequest).subscribe(new Observer<AVFriendshipRequest>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                Log.i("tags_friendship", "开始拒绝好友申请-----------------FriendShipAdapter");
                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull AVFriendshipRequest avFriendshipRequest) {
                                Log.i("tags_friendship", "成功拒绝好友申请-----------------FriendShipAdapter");
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.i("tags_friendship", "拒绝好友申请失败-----------------FriendShipAdapter");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return avFriendshipRequest.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
