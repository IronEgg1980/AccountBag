package yzw.ahaqth.accountbag.tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import yzw.ahaqth.accountbag.operators.SetupOperator;

public final class ToolUtils {
    public static final long ONE_DAY_MILLES = 24 * 60 * 60 * 1000;

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static String getRandomPassword(){
        int flag = new Random().nextInt(8) + 5;
        return getRandomString(flag);
    }

    public static String getHelloString(){
        GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.CHINA);
        int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
        StringBuilder builder = new StringBuilder();
        if(hour > 18 ){
            builder.append("晚上");
        }else if(hour > 12){
            builder.append("下午");
        }else if(hour > 7){
            builder.append("上午");
        }else if(hour > 4){
            builder.append("早晨");
        }else{
            builder.append("凌晨");
        }
        builder.append("好，").append(SetupOperator.getUserName());
        return builder.toString();
    }

    public static String getPinYin(String s){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<s.length();i++){
            char c = s.charAt(i);
            if(Pinyin.isChinese(c)) {
                stringBuilder.append(Pinyin.toPinyin(c));
            }else{
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    //系统剪贴板-复制:   s为内容
    public static void copy(Context context, String s) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        ClipData clipData = ClipData.newPlainText(null, s);
        // 把数据集设置（复制）到剪贴板
        clipboard.setPrimaryClip(clipData);
    }

    //系统剪贴板-获取:
    public static String getCopy(Context context) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 返回数据
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }

    public static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionCode;
    }

    /**
     * 获取当前app version name
     */
    public static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }
}
