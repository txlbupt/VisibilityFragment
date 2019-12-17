package cn.cocoder.fragmentvisibility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ParentFragment fragmentVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentVisibility = new ParentFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, fragmentVisibility);
        fragmentTransaction.commit();

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (fragmentVisibility.isDetached()) {
                    fragmentTransaction.hide(fragmentVisibility);
                } else {
                    fragmentTransaction.show(fragmentVisibility);
                }
                fragmentTransaction.commit();
            }
        });
    }
}
