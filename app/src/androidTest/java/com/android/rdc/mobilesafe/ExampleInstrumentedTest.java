package com.android.rdc.mobilesafe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.bean.BlackContactInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
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
}
