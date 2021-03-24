package com.example.compus;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compus.Adapter.MailListAdapter;
import com.example.compus.ViewModel.MaillistViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVFriendship;
import cn.leancloud.AVFriendshipRequest;
import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MaillistFragment extends Fragment {

    private MaillistViewModel mViewModel;
    private TextInputEditText editText;
    private RecyclerView recyclerView;
    private MailListAdapter mailListAdapter;
    private Button btnFind;
    private String nameText, nameObjectId;

    public static MaillistFragment newInstance() {
        return new MaillistFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maillist_fragment, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(MaillistViewModel.class);
        btnFind = view.findViewById(R.id.buttonmaillist);
        editText = view.findViewById(R.id.TextInputEditText_maillist);
        recyclerView = view.findViewById(R.id.recycleView_maillist);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        //设置btn未打开，搜索功能失效
        btnFind.setEnabled(false);

        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            mailListAdapter = new MailListAdapter(mViewModel.UserList.getValue(), mViewModel.FriendList.getValue());
            mailListAdapter.updateList(mViewModel.UserList.getValue(), mViewModel.FriendList.getValue());
            recyclerView.setAdapter(mailListAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            mViewModel.UserList.observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<List<AVObject>>() {
                @Override
                public void onChanged(List<AVObject> list) {
                    mailListAdapter.updateList(list, mViewModel.FriendList.getValue());
                    mailListAdapter.notifyDataSetChanged();
                }
            });
            mViewModel.FriendList.observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<List<AVFriendship>>() {
                @Override
                public void onChanged(List<AVFriendship> list) {
                    mailListAdapter.updateList(mViewModel.UserList.getValue(), list);
                    mailListAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Log.i("tag_mailList", "连接失败，请检查网络设置-----MailListFragment");
        }
        //添加输入监控，检查输入，打开搜索功能
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameText = editText.getText().toString();
                check(nameText);
            }
        });

        //按钮事件，--------------------------------搜索并且申请加为好友功能
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取好友ID
                for (AVObject avUser : mViewModel.UserList.getValue()) {
                    if (avUser.get("username").equals(nameText)) {
                        nameObjectId = avUser.getObjectId();
                        Log.i("tag_maillist", "好友昵称" + avUser.get("username"));
                        Toast.makeText(getContext(),"好友昵称" + avUser.get("username")+"已发送好友请求",Toast.LENGTH_SHORT).show();
                    }
                }
                //建立申请信息
                if (nameObjectId != null) {
                    try {
                        AVUser friend = AVUser.createWithoutData(AVUser.class, nameObjectId);
                        currentUser.applyFriendshipInBackground(friend, null).subscribe(new Observer<AVFriendshipRequest>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                Log.i("tag_maillist", "开始申请好友");
                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull AVFriendshipRequest avFriendshipRequest) {
                                Log.i("tag_maillist", "申请好友提交成功，检查_Followee表和_FriendshipRequest表");
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.i("tag_maillist", "申请好友提交失败，检查网络设置");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    } catch (AVException e) {
                        e.printStackTrace();
                        Log.i("tag_maillist", "创建申请信息失败");
                    }
                } else {
                    Log.i("tag_maillist", "没有此账户");
                }
            }
        });
    }

    //检查输入
    public void check(String string) {
        if (string.length() > 0) {
            Log.i("tag_mailList", "string.length()=" + string.length());
            btnFind.setEnabled(true);
        } else {
            btnFind.setEnabled(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}