package com.android.rdc.mobilesafe.ch01.entitiy;

import com.android.rdc.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

public class HomeDataModel {
    private static int[] sImgId = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.trojan, R.drawable.sysoptimize, R.drawable.taskmanager,
            R.drawable.netmanager, R.drawable.atools, R.drawable.settings};

    private static String[] sItemName = {
            "手机防盗", "通讯卫士", "软件管家",
            "手机杀毒", "缓存清理", "进程管理",
            "流量统计", "高级工具", "设置中心"};


    public static List<HomeItem> getItemList() {
        List<HomeItem> homeItemList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            HomeItem homeItem = new HomeItem();
            homeItem.setImgId(sImgId[i]);
            homeItem.setItemName(sItemName[i]);
            homeItemList.add(homeItem);
        }
        return homeItemList;
    }

}
