package com.android.rdc.mobilesafe.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.rdc.mobilesafe.entity.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public final class ContactInfoParser {
    private ContactInfoParser() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    // TODO: 2017/11/1 0001 7.0 小米 查询缓慢，换一种方式
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
                    contactInfo.setFirstLetter(token.target == null ? "#" : token.target.substring(0, 1));
                    contactInfo.setTag(token.target == null ? "#" : token.target.substring(0, 1));
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
                    Log.v("woider", "Name:" + name + "\tPhone:" + number);
                    ContactInfo contactInfo = new ContactInfo();
                    contactInfo.setName(name);
                    contactInfo.setPhoneNum(number);
                    // TODO: 2017/11/1 0001 转换耗时吗？
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
