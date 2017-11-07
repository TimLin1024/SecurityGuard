package com.android.rdc.mobilesafe.bean;

public class CustomEvent {
    private String mEventName;

    public CustomEvent() {
    }

    public CustomEvent(String eventName) {
        mEventName = eventName;
    }

    public String getEventName() {
        return mEventName;
    }

    public void setEventName(String eventName) {
        mEventName = eventName;
    }
}
