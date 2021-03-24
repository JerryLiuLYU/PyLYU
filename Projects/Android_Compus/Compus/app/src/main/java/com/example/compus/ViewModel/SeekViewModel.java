package com.example.compus.ViewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.livequery.AVLiveQuery;
import cn.leancloud.livequery.AVLiveQueryEventHandler;
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SeekViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<AVObject>> _dateListLive = new MutableLiveData<List<AVObject>>();
    public LiveData<List<AVObject>> dataListLive = _dateListLive;

    public SeekViewModel(@NonNull Application application) {
        super(application);

        //连接seek数据库数据类型
        AVQuery<AVObject> query = new AVQuery<>("Seek");
        //查询条件
        query.whereEqualTo("Queryblankline", "null");
        //获取数据
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> Seeks) {
                // Seeks 是包含满足条件的 Seek 对象的数组
                _dateListLive.setValue(Seeks);
//                Toast.makeText(getApplication(),"已经获取到值",Toast.LENGTH_SHORT).show();
            }
            public void onError(Throwable throwable) {
                Toast.makeText(getApplication(),"获取失败，请检查网络设置",Toast.LENGTH_SHORT).show();
            }
            public void onComplete() {}
        });
        //开启liveQuery服务
        AVLiveQuery liveQuery = AVLiveQuery.initWithQuery(query);
        liveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectCreated(AVObject avObject) {
                super.onObjectCreated(avObject);
//                Toast.makeText(getApplication(),"取值成功",Toast.LENGTH_SHORT).show();
                List<AVObject> list = _dateListLive.getValue();
                list.add(avObject);
                _dateListLive.setValue(list);
            }

            @Override
            public void onObjectDeleted(String objectId) {
                super.onObjectDeleted(objectId);
//                Toast.makeText(getApplication(),"返回的"+objectId,Toast.LENGTH_LONG).show();
                List<AVObject> list = _dateListLive.getValue();
                int i = 0;
//                AVObject avObject = new AVObject();
                for (AVObject object:list) {
//                    Toast.makeText(getApplication(),object.get("objectId").toString(),Toast.LENGTH_LONG).show();
                    Log.i("objectId","返回的"+objectId);
                    Log.i("objectId","list:"+object.get("objectId").toString());
                    if (object.get("objectId").equals(objectId) ){
                        list.remove(i);
//                        Log.i("objectId",String.valueOf(i));
//                        avObject = object;
//                        Toast.makeText(getApplication(),avObject.get("objectId").toString(),Toast.LENGTH_LONG).show();
                    }
                    i++;
                }
                for (AVObject object:list){
                    Log.i("objectId","返回的"+object.get("objectId"));
                }
                _dateListLive.setValue(list);
            }
        });
        liveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
//                    Toast.makeText(getApplication(),"订阅成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //添加
    public void addSeek(String newString){
        AVObject avObject = new AVObject("Seek");
        avObject.put("Publisher",newString);
        avObject.saveInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull AVObject avObject) {
//                Toast.makeText(getApplication(),"添加Seek成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Toast.makeText(getApplication(),"添加失败，请检查网络"+e.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}