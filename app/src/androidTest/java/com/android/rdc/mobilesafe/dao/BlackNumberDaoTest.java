package com.android.rdc.mobilesafe.dao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.rdc.mobilesafe.bean.BlackContactInfo;

import org.junit.Test;

import java.util.List;

public class BlackNumberDaoTest {
    Context mContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void add() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        BlackNumberDao blackNumberDao = new BlackNumberDao(appContext);
        for (int i = 0; i < 30; i++) {
            BlackContactInfo contactInfo = new BlackContactInfo();
            contactInfo.setContractName("  ");
            contactInfo.setMode(1);
            contactInfo.setPhoneNumber("7612878192");
            blackNumberDao.add(contactInfo);
        }
    }

    @Test
    public void delete() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        BlackNumberDao blackNumberDao = new BlackNumberDao(appContext);
        for (int i = 0; i < 30; i++) {
            BlackContactInfo contactInfo = new BlackContactInfo();
            contactInfo.setContractName("  ");
            contactInfo.setMode(1);
            contactInfo.setPhoneNumber(String.valueOf(i));
            blackNumberDao.add(contactInfo);
        }
        BlackContactInfo contactInfo = new BlackContactInfo();
        contactInfo.setPhoneNumber("10");
        blackNumberDao.delete(contactInfo);

    }

    @Test
    public void getPagesBlackNumber() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        BlackNumberDao dao = new BlackNumberDao(appContext);
        List<BlackContactInfo> list = dao.getPagesBlackNumber(2, 5);
        for (int i = 0; i < list.size(); i++) {
            Log.i("TestBlackNumberDao", list.get(i).getPhoneNumber());
        }
    }

    @Test
    public void isNumberExist() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean isExist = dao.isNumberExist(1350000000832141234l + "");
        if (isExist) {
            Log.i("TestBlackNumberDao", "该号码存在");
        } else {
            Log.i("TestBlackNumberDao", "该号码不存在");
        }
    }

    @Test
    public void getBlackContractMode() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        int mode = dao.getBlackContactMode(4 + "");
        Log.i("TestBlackNumberDao", mode + "");
    }

    @Test
    public void getTotalNumber() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        int total = dao.getTotalNumber();
        Log.i("TestBlackNumberDao", "总数量：" + total);
    }

}