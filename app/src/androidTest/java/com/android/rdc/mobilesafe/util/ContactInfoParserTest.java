package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Test;

public class ContactInfoParserTest {
    private static final String TAG = "ContactInfoParserTest";

    @Test
    public void getSystemContact() throws Exception {
        Context context = InstrumentationRegistry.getContext();
        Log.d(TAG, "getSystemContact: " + ContactInfoParser.getSystemContact(context));
    }


}