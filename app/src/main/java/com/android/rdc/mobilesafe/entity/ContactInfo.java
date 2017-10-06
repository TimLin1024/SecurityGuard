package com.android.rdc.mobilesafe.entity;

import android.support.annotation.NonNull;

import com.android.rdc.mobilesafe.ui.widget.BaseTagBean;

public class ContactInfo extends BaseTagBean implements Comparable<ContactInfo> {
    private String mId;
    private String mName;
    private String mPhoneNum;
    private String mFirstLetter;
    private boolean mIsChecked;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        mPhoneNum = phoneNum;
    }

    public String getFirstLetter() {
        return mFirstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        mFirstLetter = firstLetter;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoneNum='" + mPhoneNum + '\'' +
                ", mFirstLetter='" + mFirstLetter + '\'' +
                '}';
    }


    @Override
    public int compareTo(@NonNull ContactInfo o) {
        if (mFirstLetter == null || o.getFirstLetter() == null) {
            return 1;
        }

        if (mFirstLetter.startsWith("#") && !o.getFirstLetter().startsWith("#")) {
            return 1;
        } else if (!mFirstLetter.startsWith("#") && o.getFirstLetter().startsWith("#")) {
            return -1;
        }
        return mFirstLetter.compareToIgnoreCase(o.getFirstLetter());
    }
}
