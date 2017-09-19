package com.android.rdc.mobilesafe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Utils {

    private MD5Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 计算文件的 md5 码
     * */
    public static String getFileMD5(String path) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fileInputStream.read(buffer, 0, len)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            byte[] result = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : result) {
                int num = b & 0xff;
                String hex = Integer.toHexString(num);
                if (hex.length() == 1) {
                    stringBuilder.append("0").append(hex);
                } else {
                    stringBuilder.append(hex);
                }
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * md5加密的算法
     *
     * @param text
     * @return
     */
    public static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //can't reach
            return "";
        }
    }
}
