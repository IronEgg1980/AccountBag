package yzw.ahaqth.accountbag.operators;

import org.litepal.LitePal;

import java.util.List;

import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public final class RecordOperator {
    public static List<AccountRecord> findAll(){
        return LitePal.order("sortindex desc,recordtime").find(AccountRecord.class,true);
    }

    public static AccountRecord findOne(long id){
        return LitePal.find(AccountRecord.class,id,true);
    }


    public static void saveAll(List<AccountRecord> list){
        LitePal.saveAll(list);
    }

    public static void save(AccountRecord record){
        record.save();
    }

    public static boolean isExist(String recordName){
        return LitePal.isExist(AccountRecord.class,"recordname = ?", EncryptAndDecrypt.encrypt(recordName));
    }

    public static void dele(List<AccountRecord> list){
        for(AccountRecord accountRecord : list){
            dele(accountRecord);
        }
    }

    public static void dele(AccountRecord accountRecord){
        accountRecord.delete();
    }

    public static void clearAll(){
        List<AccountRecord> list = findAll();
        dele(list);
    }
}
