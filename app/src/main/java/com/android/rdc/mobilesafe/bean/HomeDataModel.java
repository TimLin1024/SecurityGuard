package com.android.rdc.mobilesafe.bean;

import com.android.rdc.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

public class HomeDataModel {

    private static int[] sImgId = {
            R.drawable.ic_scan_virus, R.drawable.ic_intercept, R.drawable.ic_software_manager,
            R.drawable.ic_traffic, R.drawable.ic_clean_cache, R.drawable.ic_task_manager,
            R.drawable.ic_lock /*,R.drawable.safe, R.drawable.settings*/
    };

    private static String[] sItemName = {
            "安全扫描", "骚扰拦截", "应用管理",
            "流量统计", "缓存清理", "进程管理",
            "程序锁", /*"手机防盗", "设置中心"*/};


    public static List<HomeItem> getItemList() {
        List<HomeItem> homeItemList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            HomeItem homeItem = new HomeItem();
            homeItem.setImgId(sImgId[i]);
            homeItem.setItemName(sItemName[i]);
            homeItemList.add(homeItem);
        }
        return homeItemList;
    }

}
