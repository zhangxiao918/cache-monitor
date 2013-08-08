
package com.bluestome.cachemonitor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.bluestome.android.cache.MemcacheClient;
import com.bluestome.android.widget.TipDialog;

import java.lang.ref.WeakReference;

public abstract class BaseActivity extends Activity implements IBaseActivity {

    private final String TAG = BaseActivity.class.getCanonicalName();
    protected MemcacheClient mCacheClient;

    protected static class MyHandler extends Handler {
        private WeakReference<BaseActivity> mActivity;

        MyHandler(BaseActivity activity) {
            mActivity = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mActivity.get();
            if (null != activity) {
                super.handleMessage(msg);
            }
        }

    }

    protected MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pre();
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
                mHandler.postDelayed(this, 1 * 1000L);
            } else {
                removeDialog(CACHE_INITING);
                Log.d(TAG, "mCacheClient init success!");
                mHandler.removeCallbacks(this);
            }
        }
    };

    private void pre() { // 详见StrictMode文档
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
    }

    /**
     * 初始化
     */
    public abstract void init();

    /**
     * 初始化视图
     */
    public abstract void initView();

    /**
     * 舒适化数据
     */
    public abstract void initData();

    @Override
    public Context getContext() {
        return this;
    }

}
