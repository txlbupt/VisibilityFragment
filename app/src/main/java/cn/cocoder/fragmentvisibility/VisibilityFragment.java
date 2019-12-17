package cn.cocoder.fragmentvisibility;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Stack;

public class VisibilityFragment extends Fragment implements IPareVisibilityObserver {

    public static final int FRAGMENT_HIDDEN_STATE = 0x01;

    public static final int USER_INVISIBLE_STATE = 0x02;

    public static final int PARENT_INVISIBLE_STATE = 0x04;

    public static final int LIFE_CIRCLE_PAUSE_STATE = 0x08;

    protected MutableLiveData<Integer> mFragmentVisibleState = new MutableLiveData<>();

    @Override
    public void onPause() {
        super.onPause();
        Log.i("FragmentVisibility", "onPause");
        int value = mFragmentVisibleState.getValue() == null ? LIFE_CIRCLE_PAUSE_STATE : mFragmentVisibleState.getValue();
        mFragmentVisibleState.setValue(value | LIFE_CIRCLE_PAUSE_STATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FragmentVisibility", "onResume");
        int value = mFragmentVisibleState.getValue() == null ? LIFE_CIRCLE_PAUSE_STATE : mFragmentVisibleState.getValue();
        if (getParentHiddenState()) {
            value = value | PARENT_INVISIBLE_STATE;
        } else {
            value = value & ~PARENT_INVISIBLE_STATE;
        }
        if (!getUserVisibleHint()) {
            value = value | USER_INVISIBLE_STATE;
        } else {
            value = value & ~USER_INVISIBLE_STATE;
        }
        if (isHidden()) {
            value = value | FRAGMENT_HIDDEN_STATE;
        } else {
            value = value & ~FRAGMENT_HIDDEN_STATE;
        }
        mFragmentVisibleState.setValue(value & ~LIFE_CIRCLE_PAUSE_STATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FragmentVisibility", "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FragmentVisibility", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("FragmentVisibility", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("FragmentVisibility", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("FragmentVisibility", "onDetach");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("FragmentVisibility", "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("FragmentVisibility", "onDestroyView");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("FragmentVisibility", "onActivityCreated");
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Integer value = mFragmentVisibleState.getValue();

        if (value == null) {
            return;
        }
        if (!isVisibleToUser) {
            mFragmentVisibleState.setValue(value | USER_INVISIBLE_STATE);
        } else {
            mFragmentVisibleState.setValue(value & ~USER_INVISIBLE_STATE);
        }
        notifyChildHiddenChange(value != 0);
    }

    @Override
    @CallSuper
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Integer value = mFragmentVisibleState.getValue();
        if (value == null) {
            return;
        }
        if (hidden) {
            mFragmentVisibleState.setValue(value | FRAGMENT_HIDDEN_STATE);
        } else {
            mFragmentVisibleState.setValue(value & ~FRAGMENT_HIDDEN_STATE);
        }
        notifyChildHiddenChange(value != 0);
    }

    /**
     * 获取父Fragment的显示状态（hide/show）,从而得知自己的显示状态
     * @return
     */
    public boolean getParentHiddenState() {
        Fragment fragment = getParentFragment();
        Stack<Fragment> stack = new Stack<>();
        while (fragment != null) {
            stack.push(fragment);
            fragment = fragment.getParentFragment();
        }
        boolean isHidden = false;
        while (!stack.empty()) {
            fragment = stack.pop();
            isHidden = fragment.isHidden() || !fragment.getUserVisibleHint();
            if (isHidden) {
                break;
            }
        }
        return isHidden;
    }

    /**
     *
     * @param hidden
     */
    @CallSuper
    @Override
    public void onParentFragmentHiddenChanged(boolean hidden) {
        int value = mFragmentVisibleState.getValue() == null ? LIFE_CIRCLE_PAUSE_STATE : mFragmentVisibleState.getValue();
        if (hidden) {
            mFragmentVisibleState.setValue(value | PARENT_INVISIBLE_STATE);
        } else {
            mFragmentVisibleState.setValue(value & ~PARENT_INVISIBLE_STATE);
        }
        notifyChildHiddenChange(mFragmentVisibleState.getValue() != 0);
    }

    private void notifyChildHiddenChange(boolean hidden) {
        if (isDetached() || !isAdded()) {
            return;
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.isEmpty()) {
            return;
        }
        for (Fragment fragment : fragments) {
            if (!(fragment instanceof IPareVisibilityObserver)) {
                continue;
            }
            ((IPareVisibilityObserver) fragment).onParentFragmentHiddenChanged(hidden);
        }
    }
}
