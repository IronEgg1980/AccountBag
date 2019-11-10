package yzw.ahaqth.accountbag.modules;

import android.support.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

public class RecordGroup extends LitePalSupport {
    private String groupName;
    private int sortIndex;

    public RecordGroup(){

    }
    public RecordGroup(String name){
        this.groupName = name;
    }

    public long getId(){
        return getBaseObjId();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @NonNull
    @Override
    public String toString() {
        return this.groupName;
    }
}
