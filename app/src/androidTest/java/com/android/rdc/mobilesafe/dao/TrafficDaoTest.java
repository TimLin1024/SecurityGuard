package com.android.rdc.mobilesafe.dao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.rdc.mobilesafe.util.DateUtil;

import org.junit.Test;

import java.util.Date;

public class TrafficDaoTest {

    Context mContext = InstrumentationRegistry.getTargetContext();
    TrafficDao mTrafficDao = new TrafficDao(mContext);
    private static final String TAG = "TrafficDaoTest";

    @Test
    public void insertTodayTraffic() throws Exception {

        mTrafficDao.insertTodayTraffic(829018902);
    }

    @Test
    public void getTrafficByDate() throws Exception {
        Log.d(TAG, "getTrafficByDate: " + mTrafficDao.getTrafficByDate(DateUtil.formatDate(new Date())));
    }

    @Test
    public void updateTodayTraffic() throws Exception {
        mTrafficDao.updateTodayTraffic(2121);
    }

}