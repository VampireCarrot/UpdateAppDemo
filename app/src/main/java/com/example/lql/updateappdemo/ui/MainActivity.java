package com.example.lql.updateappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.lql.updateappdemo.R;
import com.example.lql.updateappdemo.UpdateAppUtils;
import com.example.lql.updateappdemo.message.EventMessage;
import com.example.lql.updateappdemo.utils.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    Button checkVersion;
    boolean IsDownLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //        //注册事件
        EventBus.getDefault().register(this);
        checkVersion = (Button) findViewById(R.id.checkVersion_btn);
        checkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注意这里正常情况下，下载完成之后，安装了新的安装包，IsDownLoad就变成false了，
                //因为这里下载的不是这个apk,所以会有这个问题，以后会放一个apk在线上，
                // 改成下载当前项目的apk,就不会这样了
                if (IsDownLoad) {
                    T.shortToast(MainActivity.this, "正在更新，请稍后...");
                    return;
                }
                //升级app
                getData();
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_ly, HomeFragment.getInstance()).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitappEvent(EventMessage messageEvent) {
        if (messageEvent.getMessageType() == EventMessage.Exitapp) {
            T.shortToast(MainActivity.this, "去处理退出app的方法");
        } else if (messageEvent.getMessageType() == EventMessage.CheckApp) {
            IsDownLoad = messageEvent.isDownLoading();
        }
    }


    private void getData() {
        String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
        String Version_name = "1.1";//版本名称
        String info = "模拟下载，使用QQApk";  //更新说明
        int Forced = 1;// 1：强制更新   0：不是
        int Version_no = 2;//版本号
        UpdateAppUtils.UpdateApp(MainActivity.this, null, Version_no, Version_name, info,
                url, Forced == 1 ? true : false, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从设置界面返回,就继续判断权限
        if (requestCode == UpdateAppUtils.REQUEST_PERMISSION_SDCARD_SETTING) {
            getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UpdateAppUtils.onActRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }
}
