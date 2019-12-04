package yzw.ahaqth.accountbag.operators;

import org.litepal.LitePal;

import java.util.List;

import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public final class RecordOperator {
    public static List<AccountRecord> findAll() {
        return LitePal.findAll(AccountRecord.class, true);
    }

    public static List<AccountRecord> findAll(long recordGroupId) {
        if(recordGroupId < 0)
            return findAllNotDeleted();
        return LitePal.where("isDeleted = 0 and groupId = ?", String.valueOf(recordGroupId))
                .find(AccountRecord.class, true);
    }

    public static List<AccountRecord> findAllNotDeleted() {
        return LitePal.where("isDeleted = 0")
                .find(AccountRecord.class, true);
    }

    public static List<AccountRecord> findAllDeleted() {
        return LitePal.order("deletime desc")
                .where("isDeleted = 1")
                .find(AccountRecord.class, true);
    }

    public static void clearOldDeletedRecord() {
        for (AccountRecord record : findAllDeleted()) {
            long deleTime = record.getDeleTime();
            long diffDay = (System.currentTimeMillis() - deleTime) / ToolUtils.ONE_DAY_MILLES;
            if (diffDay > 29) {
                clear(record);
            }
        }
    }

    public static AccountRecord findOne(long id) {
        return LitePal.find(AccountRecord.class, id, true);
    }

    public static void saveAll(List<AccountRecord> list) {
        LitePal.saveAll(list);
    }

    public static void save(AccountRecord record) {
        record.save();
    }

    public static boolean isExist(String recordName) {
        return LitePal.isExist(AccountRecord.class, "recordname = ?", EncryptAndDecrypt.encrypt(recordName));
    }

    public static void deleAll(List<AccountRecord> list) {
        for (AccountRecord accountRecord : list) {
            dele(accountRecord);
        }
    }

    public static void dele(AccountRecord accountRecord) {
        accountRecord.setDeleted(true);
        accountRecord.setDeleTime(System.currentTimeMillis());
        save(accountRecord);
    }

    public static void clear(AccountRecord accountRecord) {
        for (ImageRecord imageRecord : accountRecord.getImageRecords()) {
            ImageOperator.deleImageFile(imageRecord);
        }
        accountRecord.delete();
    }

    public static void clearAll(List<AccountRecord> list) {
        for (AccountRecord accountRecord : list) {
            clear(accountRecord);
        }
    }

    public static void clearAll() {
        List<AccountRecord> list = findAll();
        clearAll(list);
    }

    public static void resumeOne(AccountRecord accountRecord) {
        accountRecord.setDeleted(false);
        accountRecord.setDeleTime(1000);
        save(accountRecord);
    }

    public static void resumeAll(List<AccountRecord> list) {
        for (AccountRecord accountRecord : list) {
            resumeOne(accountRecord);
        }
    }
}
