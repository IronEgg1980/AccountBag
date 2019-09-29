package yzw.ahaqth.accountbag.interfaces;

import android.support.annotation.Nullable;

public interface ItemClickListener<T> {
    void click(int position, @Nullable T...values);
}
