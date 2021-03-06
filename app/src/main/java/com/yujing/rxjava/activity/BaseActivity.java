package com.yujing.rxjava.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.blankj.rxbus.RxBus;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.yujing.rxjava.contract.OnResponse;
import com.yujing.rxjava.contract.RxBusMessage;
import com.yujing.utils.YPermissions;
import com.yujing.utils.YShow;
import com.yujing.utils.YToast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 基础activity
 *
 * @author yujing 2019年5月21日16:30:16
 */
@SuppressWarnings("unused")
public abstract class BaseActivity<B extends ViewDataBinding> extends RxAppCompatActivity implements OnResponse {
    public B binding;
    public CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer contentViewId = getContentLayoutId();
        if (contentViewId != null) {
            binding = DataBindingUtil.setContentView(this, contentViewId);
        }
        RxBus.getDefault().subscribeSticky(this, AndroidSchedulers.mainThread(), defaultCallback);
        YPermissions.requestAll(this);
        initData();
    }

    /**
     * RxBus回调
     *
     * @param rxBusMessage 回调内容
     */
    public abstract void onEvent(RxBusMessage<?> rxBusMessage);

    /**
     * 绑定layout
     */
    protected abstract Integer getContentLayoutId();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 注册默认RxBus
     */
    RxBus.Callback defaultCallback = new RxBus.Callback<RxBusMessage<?>>() {

        @Override
        public void onEvent(RxBusMessage<?> rxBusMessage) {
            BaseActivity.this.onEvent(rxBusMessage);
        }
    };

    /**
     * 添加订阅
     */
    public void addDisposable(Disposable mDisposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mDisposable);
    }

    /**
     * 取消所有订阅
     */
    public void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


    // 此处为android 6.0以上动态授权的回调，用户自行实现。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /**
     * 跳转
     */
    protected void startActivity(Class<?> classActivity) {
        Intent intent = new Intent();
        intent.setClass(this, classActivity);
        startActivity(intent);
    }

    /**
     * 跳转
     */
    protected void startActivity(Class<?> classActivity, int resultCode) {
        startActivityForResult(classActivity, resultCode);
    }

    /**
     * 跳转
     */
    protected void startActivityForResult(Class<?> classActivity, int resultCode) {
        Intent intent = new Intent();
        intent.setClass(this, classActivity);
        startActivityForResult(intent, resultCode);
    }

    /**
     * 显示toast
     */
    protected void show(String str) {
        if (str == null || str.isEmpty())
            return;
        YToast.show(getApplicationContext(), str);
    }

    /**
     * 窗口焦点改变监听
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        System.gc();// 系统自动回收
        clearDisposable();
        RxBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 延时关闭YShow
     */
    protected void delayedShowFinish() {
        delayedShowFinish(5);
    }

    /**
     * 延时关闭YShow
     */
    protected void delayedShowFinish(int time) {
        // 该例子 = 延迟2s后，发送一个long类型数值
        addDisposable(Observable.timer(time, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> YShow.finish()));
    }

    @Override
    public void finish() {
        super.finish();
        YShow.finish();
    }
}
