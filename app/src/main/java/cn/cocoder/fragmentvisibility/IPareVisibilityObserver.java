package cn.cocoder.fragmentvisibility;

import androidx.annotation.CallSuper;

public interface IPareVisibilityObserver {
    public void onParentFragmentHiddenChanged(boolean hidden);
}
