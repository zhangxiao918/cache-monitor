
package com.bluestome.cachemonitor.activity;

import android.app.Dialog;
import android.os.Bundle;

import com.bluestome.android.activity.BaseActivity;
import com.bluestome.android.activity.IActivityInitialization;
import com.bluestome.android.cache.memcache.MemcacheClient;
import com.bluestome.android.widget.TipDialog;

public abstract class CacheBaseActivity extends BaseActivity implements IActivityInitialization {

    private final String TAG = CacheBaseActivity.class.getCanonicalName();
    protected MemcacheClient mCacheClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
        showDialog(CACHE_INITING);
        mHandler.post(initCacheRunnable);
    }

    final int LOADING = 1000;
    final int CACHE_INITING = 1001;
    final int LOADING_CACHE_PARAMS = 1002;

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case LOADING:
                dialog = new TipDialog(getContext(), "数据载入中...");
                return dialog;
            case CACHE_INITING:
                dialog = new TipDialog(getContext(), "缓存初始化中");
                return dialog;
            case LOADING_CACHE_PARAMS:
                dialog = new TipDialog(getContext(), "载入缓存数据");
                return dialog;
        }
        return super.onCreateDialog(id);
    }

    /**
     * 初始化缓存
     */
    private Runnable initCacheRunnable = new Runnable() {

        @Override
        public void run() {
            if (null == mCacheClient) {
                mCacheClient = MemcacheClient.getInstance(getContext());
                mHandler.postDelayed(this, mCacheClient.getSocketConnectTO() * 1000L);
            } else {
                removeDialog(CACHE_INITING);
                mHandler.removeCallbacks(initCacheRunnable);
                next();
            }
        }
    };

    /**
     * 注册接收销毁当前ACTIVITY的广播
     */
    public abstract void registerDestorySelfBroadcast();

    /**
     * 反注册销毁当前ACTIVITY的广播
     */
    public abstract void unRegisterDestorySelfBroadcast();

    /**
     * 下一步
     */
    public abstract void next();

    /**
     * 基本参数初始化
     */
    @Override
    public abstract void init();

    /**
     * 视图组件初始化
     */
    @Override
    public abstract void initViews();

    /**
     * 数据初始化
     */
    @Override
    public abstract void initDatas();

}
