package yzw.ahaqth.accountbag.modules;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public class AccountRecord extends LitePalSupport {
    private String recordName;
    private String accountName;
    private String accountPWD;
    private List<TextRecord> textRecords = new ArrayList<>();
    private List<ImageRecord> imageRecords = new ArrayList<>();
    @Column(ignore = true)
    private boolean isExpand;
    @Column(ignore = true)
    private boolean isShowPWD;

    public long getId(){
        return getBaseObjId();
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

    public List<TextRecord> getTextRecords() {
        return textRecords;
    }

    public void setTextRecord(TextRecord textRecord) {
        this.textRecords.add(textRecord);
    }

    public List<ImageRecord> getImageRecords() {
        return imageRecords;
    }

    public void setImageRecord(ImageRecord imageRecord) {
        this.imageRecords.add(imageRecord);
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public boolean isShowPWD() {
        return isShowPWD;
    }

    public void setShowPWD(boolean showPWD) {
        isShowPWD = showPWD;
    }
}
