package com.android.rdc.mobilesafe.entity;

public class BlackContactInfo {

    private String mPhoneNumber;
    private String mContractName;
    private int mMode;

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
                return "电话拦截";
            case 2:
                return "短信拦截";
            case 3:
                return "电话、短信拦截";
        }
        return "";
    }
}
