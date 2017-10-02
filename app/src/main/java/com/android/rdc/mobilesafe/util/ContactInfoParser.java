package com.android.rdc.mobilesafe.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.android.rdc.mobilesafe.entity.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public final class ContactInfoParser {
    private ContactInfoParser() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static List<ContactInfo> getSystemContact(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        List<ContactInfo> contactInfoList = new ArrayList<>(32);
        Cursor cursor = contentResolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            String id = cursor.getString(0);
            if (id == null) {
                return null;
            }
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setId(id);

            //根据联系人的 id，查询 data 表，把这个 id 的数据取出来
            //系统 api 查询 data 表时，不是真正查询 data 表，而是查询 data 视图
            Cursor dataCursor = contentResolver.query(dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
            while (dataCursor != null && dataCursor.moveToNext()) {
                String data1 = dataCursor.getString(0);
                String mimetype = dataCursor.getString(1);
                if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    contactInfo.setName(data1);
                    HanziToPinyin.Token token = HanziToPinyin.getInstance().get(data1).get(0);
                    contactInfo.setFirstLetter(token.target == null ? "#" : token.target);
                } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    contactInfo.setPhoneNum(data1);
                }
            }
            contactInfoList.add(contactInfo);
            if (dataCursor != null) {
                dataCursor.close();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return contactInfoList;
    }

}
