
package com.bluestome.cachemonitor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bluestome.android.utils.DateUtils;
import com.bluestome.android.widget.ToastUtil;
import com.bluestome.cachemonitor.R;
import com.bluestome.cachemonitor.domain.CacheStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 启动界面
 * 
 * @author bluestome
 */
public class LauncherActivity extends BaseActivity {

    private static final String TAG = LauncherActivity.class.getCanonicalName();
    private TextView contentTextView;
    private List<CacheStatus> list = new ArrayList<CacheStatus>(15);
    private static int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void init() {
        Log.e(TAG, "设置网络连接信息");
        // 详见StrictMode文档
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());

    }

    /**
     * 获取缓存中的基本信息
     */
    private Runnable mGetCacheStats = new Runnable() {
        public void run() {
            showDialog(LOADING_CACHE_PARAMS);
            CacheStatus status = null;
            if (null != mCacheClient) {
                count = 0;
                mHandler.removeCallbacks(mStaffRunnable);
                contentTextView.setText("");
                list.clear();
                Map map = mCacheClient.getStats();
                Iterator it = map.keySet().iterator();
                while (null != it && it.hasNext()) {
                    String key = (String) it.next();
                    contentTextView.setText(key + "\t[\t" + DateUtils.getNow() + "\t]\r\n\r\n");
                    Map mMap = (Map) map.get(key);
                    Iterator mIt = mMap.keySet().iterator();
                    while (null != mIt && mIt.hasNext()) {
                        status = new CacheStatus();
                        String mKey = (String) mIt.next();
                        String mValue = (String) mMap.get(mKey);
                        status.setName(mKey);
                        status.setValue(mValue);
                        list.add(status);

                    }
                }
                Collections.sort(list, new Comparator<CacheStatus>() {
                    @Override
                    public int compare(CacheStatus lhs, CacheStatus rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                for (CacheStatus stats : list) {
                    String content = null;
                    content = stats.getName().toUpperCase(Locale.CHINA) + " : " + stats.getValue()
                            + " \r\n";
                    String oldTxt = contentTextView.getText().toString();
                    contentTextView.setText(oldTxt + content);
                }
                removeDialog(LOADING_CACHE_PARAMS);
                mHandler.postDelayed(this, 2 * 60 * 1000L);
                mHandler.post(mStaffRunnable);
            }
        }
    };

    /**
     * 一些提示信息
     */
    private Runnable mStaffRunnable = new Runnable() {
        public void run() {
            mHandler.postDelayed(this, 10 * 1000L);
            String key = "cache_monitor_staff_" + count;
            long saveTime = System.currentTimeMillis() + 1000 * 10;
            mCacheClient.add(key, System.currentTimeMillis(), new Date(saveTime));
            mCacheClient.get(key);
            ToastUtil.resultNotify(getContext(), Color.parseColor("#fdd600"), "10秒刷新一次["
                    + (count++)
                    + "]");
        }
    };

    @Override
    public void initViews() {
        setContentView(R.layout.activity_launch);
        contentTextView = (TextView) findViewById(R.id.content_id);
        contentTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        mHandler.postDelayed(mStaffRunnable, 5 * 1000L);
        mHandler.postDelayed(mGetCacheStats, 3 * 1000L);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mGetCacheStats);
        mHandler.removeCallbacks(mStaffRunnable);
        finish();
    }

    @Override
    public void registerDestorySelfBroadcast() {
        // TODO Auto-generated method stub

    }

    @Override
    public void unRegisterDestorySelfBroadcast() {
        // TODO Auto-generated method stub

    }

    @Override
    public void next() {
        // TODO Auto-generated method stub
        initDatas();
    }

}
