package com.android.rdc.mobilesafe.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.android.rdc.mobilesafe.bean.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public final class ContactInfoParser {
    private ContactInfoParser() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static List<ContactInfo> readContacts(Context context) {
        Cursor cursor = null;
        List<ContactInfo> contactInfoList = null;
        try { //获取内容提供器
            ContentResolver resolver = context.getApplicationContext().getContentResolver(); //查询联系人数据
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null, null); //遍历联系人列表

            contactInfoList = new ArrayList<>(50);//一般都会有 50+ 联系人
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //获取联系人姓名
                    String name = cursor.getString(
                            cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    String number = cursor.getString(
                            cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactInfo contactInfo = new ContactInfo();
                    contactInfo.setName(name);
                    contactInfo.setPhoneNum(number);
                    HanziToPinyin.Token token = HanziToPinyin.getInstance().get(name).get(0);
                    contactInfo.setFirstLetter(token.target == null ? "#" : token.target.substring(0, 1));
                    contactInfo.setTag(token.target == null ? "#" : token.target.substring(0, 1));
                    contactInfoList.add(contactInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactInfoList;
    }


}
