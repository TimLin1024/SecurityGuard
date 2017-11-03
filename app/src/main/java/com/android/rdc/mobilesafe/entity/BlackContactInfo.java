package com.android.rdc.mobilesafe.entity;

public class BlackContactInfo {

    private String mPhoneNumber;
    private String mContractName;
    private int mMode;
    private boolean mIsSelected;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getContractName() {
        return mContractName;
    }

    public void setContractName(String contractName) {
        mContractName = contractName;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public String getStringMode() {
        switch (mMode) {
            case 1:
                return "拦截来电";
            case 2:
                return "拦截短信";
            case 3:
                return "拦截来电和短信";
        }
        return "";
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}
