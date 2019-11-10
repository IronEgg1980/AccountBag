package yzw.ahaqth.accountbag.modules;

import android.text.TextUtils;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public class AccountRecord extends LitePalSupport {
    private String recordName;
    private String accountName;
    private String accountPWD;
    private String describe;
    private long groupId;
    private int sortIndex;
    private long recordTime;
    private long modifyTime;
    private boolean isDeleted;
    private long deleTime;
    private List<TextRecord> textRecords = new ArrayList<>();
    private List<ImageRecord> imageRecords = new ArrayList<>();

    @Column(ignore = true)
    public boolean isSeleted = false;
    @Column(ignore = true)
    public boolean isMultiMode = false;

    public AccountRecord(){
        super();
        this.isDeleted = false;
        this.isMultiMode = false;
        this.sortIndex = -1;
        this.groupId = -1;
    }

    public long getId() {
        return getBaseObjId();
    }

    public void saveExtraData() {
        int n = textRecords.size();
        for (int i = n - 1; i >= 0; i--) {
            TextRecord textRecord = textRecords.get(i);
            if (textRecord.isDeleted) {
                textRecord.delete();
                textRecords.remove(i);
            }
        }
        n = imageRecords.size();
        for (int i = n - 1; i >= 0; i--) {
            ImageRecord imageRecord = imageRecords.get(i);
            if (imageRecord.isDeleted) {
                ImageOperator.deleImageFile(imageRecord);
                imageRecord.delete();
                imageRecords.remove(i);
            }else if(!imageRecord.isSaved()){
                ImageOperator.saveImageFile(imageRecord);
            }
        }
        LitePal.saveAll(textRecords);
        LitePal.saveAll(imageRecords);
    }

    public int getTotalTextCount(){
        int c = getExtraTextCount() + 3;
        if(TextUtils.isEmpty(getAccountName())){
            c--;
        }
        if(TextUtils.isEmpty(getAccountPWD()))
            c--;
        if(TextUtils.isEmpty(getDescribe()))
            c--;
        return c;
    }

    public int getExtraTextCount() {
        return textRecords.size();
    }

    public int getExtraImageCount() {
        return imageRecords.size();
    }

    public void setTextRecords(List<TextRecord> list) {
        this.textRecords.addAll(list);
    }

    public void setImageRecords(List<ImageRecord> list) {
        this.imageRecords.addAll(list);
    }

    @Override
    public boolean save() {
        saveExtraData();
        return super.save();
    }

    public String getRecordName() {
        return EncryptAndDecrypt.decrypt(recordName);
    }

    public void setRecordName(String recordName) {
        this.recordName = EncryptAndDecrypt.encrypt(recordName);
    }

    public String getAccountName() {
        return EncryptAndDecrypt.decrypt(accountName);
    }

    public void setAccountName(String accountName) {
        this.accountName = EncryptAndDecrypt.encrypt(accountName);
    }

    public String getAccountPWD() {
        return EncryptAndDecrypt.decrypt(accountPWD);
    }

    public void setAccountPWD(String accountPWD) {
        this.accountPWD = EncryptAndDecrypt.encrypt(accountPWD);
    }

    public String getDescribe() {
        return EncryptAndDecrypt.decrypt(describe);
    }

    public void setDescribe(String describe) {
        this.describe = EncryptAndDecrypt.encrypt(describe);
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getDeleTime() {
        return deleTime;
    }

    public void setDeleTime(long deleTime) {
        this.deleTime = deleTime;
    }

    public List<TextRecord> getTextRecords() {
        return textRecords;
    }

    public void addTextRecord(TextRecord textRecord) {
        this.textRecords.add(textRecord);
    }

    public void delTextRecord(int position) {
        if (position > this.textRecords.size() - 1)
            return;
        TextRecord textRecord = this.textRecords.get(position);
        textRecord.delete();
        this.textRecords.remove(position);
    }

    public List<ImageRecord> getImageRecords() {
        return imageRecords;
    }

    public void addImageRecord(ImageRecord imageRecord) {
        this.imageRecords.add(imageRecord);
    }

    public void delImageRecord(int position) {
        if (position > this.imageRecords.size() - 1)
            return;
        ImageRecord imageRecord = this.imageRecords.get(position);
        ImageOperator.deleImageFile(imageRecord);
        imageRecord.delete();
        this.imageRecords.remove(position);
    }

//    public boolean isExpand() {
//        return isExpand;
//    }
//
//    public void setExpand(boolean expand) {
//        isExpand = expand;
//    }
//
//    public boolean isShowPWD() {
//        return isShowPWD;
//    }
//
//    public void setShowPWD(boolean showPWD) {
//        isShowPWD = showPWD;
//    }
}
