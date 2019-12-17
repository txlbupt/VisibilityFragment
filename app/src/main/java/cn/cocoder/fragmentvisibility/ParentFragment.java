package cn.cocoder.fragmentvisibility;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

public class ParentFragment extends VisibilityFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mFragmentVisibleState.hasObservers()) {
            return;
        }
        mFragmentVisibleState.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i("FragmentVisibility", "ParentFragment visibility:" + integer);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, null);
        Log.i("FragmentVisibility", "onCreateView");
        ChildFragment childFragment = new ChildFragment();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add( R.id.child_container, childFragment);
        fragmentTransaction.commit();
        return view;
    }

}
