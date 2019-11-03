package yzw.ahaqth.accountbag.operators;

import android.text.TextUtils;

import org.litepal.LitePal;

import java.util.List;
import java.util.UUID;

import yzw.ahaqth.accountbag.modules.Setup;
import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public final class SetupOperator {
    private static String MY_PHONEID = "";
    private static final String ID = "phoneid";
    private static final String LAST_VERSION = "lastversion";
    private static final String PWD = "apppassword";
    private static final String GESTURE_PWD = "gesturepassword";
    private static final String USER_NAME = "username";

    // 获取唯一ID，恢复出厂设置会改变
    private static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    private static <T> T getSetupValue(String key, T defaultValue) {
        Setup appSetup = findOne(key);
        if (appSetup == null) {
            appSetup = new Setup(key, String.valueOf(defaultValue));
            appSetup.save();
            return defaultValue;
        }
        String value =appSetup.getValue();
        if (defaultValue instanceof Integer)
            return (T)Integer.valueOf(value);
        if (defaultValue instanceof Long)
            return (T)Long.valueOf(value);
        if (defaultValue instanceof Boolean)
            return (T)Boolean.valueOf(value);
        if (defaultValue instanceof Float)
            return (T)Float.valueOf(value);
        return (T)value;
    }

    private static void saveSetupValue(String key, Object value) {
        String stringValue = String.valueOf(value);
        Setup appSetup = findOne(key);
        if (appSetup == null) {
            appSetup = new Setup(key, stringValue);
        } else {
            appSetup.setValue(stringValue);
        }
        appSetup.save();
    }

    public static int getCount(){
        return LitePal.count(Setup.class);
    }

    public static Setup findOne(String key){
        return LitePal.where("key = ?", EncryptAndDecrypt.encrypt(key)).findFirst(Setup.class);
    }

    public static List<Setup> findAll(){
        return LitePal.findAll(Setup.class);
    }

    public static void saveAll(List<Setup> list){
        LitePal.saveAll(list);
    }

    public static String getPhoneId() {
        if (TextUtils.isEmpty(MY_PHONEID)) {
            MY_PHONEID = getSetupValue(ID,getUUID());
        }
        return MY_PHONEID;
    }

    public static void setPhoneId(String id){
        saveSetupValue(ID,id);
    }

    public static long getLastAppVersion(){
        return getSetupValue(LAST_VERSION,0L);
    }

    public static void setLastAppVersion(long version){
        saveSetupValue(LAST_VERSION,version);
    }

    public static String getUserName(){
        return getSetupValue(USER_NAME, "");
    }

    public static void saveUserName(String username){
        saveSetupValue(USER_NAME,username);
    }

    public static String getPassWord(){
        return getSetupValue(PWD, "");
    }

    public static void savePassWord(String pwd){
        saveSetupValue(PWD,pwd);
    }

    public static String getGesturePassWord(){
        return getSetupValue(GESTURE_PWD, "");
    }

    public static void saveGesturePassWord(String gesturePWD){
        saveSetupValue(GESTURE_PWD,gesturePWD);
    }

    public static void setInputPassWordMode(int mode){
        saveSetupValue("inputpasswordmode",mode);
    }

    public static int getInputPassWordMode(){
        // 1 - 输入密码；2 - 手势密码
        return getSetupValue("inputpasswordmode",1);
    }
}
