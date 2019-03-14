package com.example.personalmoney;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.personalmoney.Fragment.FirstFundFragment;
import com.example.personalmoney.Fragment.SecondHouseFragment;
import com.example.personalmoney.Fragment.ThirdSalaryFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> list;
    private myFragAdapter myFragAdapter;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
//                case R.id.navigation_dashboard:
//                    viewPager.setCurrentItem(1);
//                    return true;
//                case R.id.navigation_notifications:
//                    viewPager.setCurrentItem(2);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /**初始化FragmentList**/
        list = new ArrayList<>();
//        list.add(new FirstFundFragment());
//        list.add(new SecondHouseFragment());
        list.add(new ThirdSalaryFragment());

        /**viewpager初始化**/
        myFragAdapter = new myFragAdapter(getSupportFragmentManager(), list);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(myFragAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            { }

            @Override
            public void onPageSelected(int position) {
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

}
