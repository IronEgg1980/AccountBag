package yzw.ahaqth.accountbag.modules;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public class TextRecord extends LitePalSupport {
    private String key;
    private String content;
    @Column(ignore = true)
    public boolean isShowValue;

    public String getKey() {
        return EncryptAndDecrypt.decrypt(key);
    }

    public void setKey(String key) {
        this.key = EncryptAndDecrypt.encrypt(key);
    }

    public String getContent() {
        return EncryptAndDecrypt.decrypt(content);
    }

    public void setContent(String content) {
        this.content = EncryptAndDecrypt.encrypt(content);
    }
}
