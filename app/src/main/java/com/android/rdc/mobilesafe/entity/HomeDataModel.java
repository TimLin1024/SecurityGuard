package com.android.rdc.mobilesafe.entity;

import com.android.rdc.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

public class HomeDataModel {

    private static int[] sImgId = {
            R.drawable.ic_scan_virus, R.drawable.ic_intercept, R.drawable.ic_software_manager,
            R.drawable.ic_traffic, R.drawable.ic_clean_cache, R.drawable.ic_task_manager,
            R.drawable.safe, R.drawable.atools, R.drawable.settings
    };

    private static String[] sItemName = {
            "安全扫描", "骚扰拦截", "应用管理",
            "流量统计", "缓存清理", "进程管理",
            "手机防盗", "程序锁", "设置中心"};


    public static List<HomeItem> getItemList() {
        List<HomeItem> homeItemList = new ArrayList<>();
        // TODO: 2017/11/1 0001 下面三项暂时不显示
        for (int i = 0; i < 6; i++) {
            HomeItem homeItem = new HomeItem();
            homeItem.setImgId(sImgId[i]);
            homeItem.setItemName(sItemName[i]);
            homeItemList.add(homeItem);
        }
        return homeItemList;
    }

}
