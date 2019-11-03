package yzw.ahaqth.accountbag.modules;

import org.litepal.crud.LitePalSupport;

import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public class Setup extends LitePalSupport {
    private String key;
    private String value;

    public Setup(String key,String value){
        this.key = EncryptAndDecrypt.encrypt(key);
        this.value = EncryptAndDecrypt.encrypt(value);
    }

    public long getId(){
        return getBaseObjId();
    }

    public String getKey() {
        return EncryptAndDecrypt.decrypt(key);
    }

    public void setKey(String key) {
        this.key = EncryptAndDecrypt.encrypt(key);
    }

    public String getValue() {
        return EncryptAndDecrypt.decrypt(value);
    }

    public void setValue(String value) {
        this.value = EncryptAndDecrypt.encrypt(value);
    }
}
