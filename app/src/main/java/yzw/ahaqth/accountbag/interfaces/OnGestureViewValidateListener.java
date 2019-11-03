package yzw.ahaqth.accountbag.interfaces;

public interface OnGestureViewValidateListener {
    /**
     * 单独选中元素的Id
     */
    public void onBlockSelected(int cId);

    /**
     * 是否匹配
     */
    public void onGestureEvent(boolean matched);

    /**
     * 超过尝试次数
     */
    public void onUnmatchedExceedBoundary();
}
