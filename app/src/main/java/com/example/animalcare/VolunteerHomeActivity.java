package com.example.animalcare;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

public class VolunteerHomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        subscribeToTopic();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        viewPager = findViewById(R.id.viewPagerVolunteerHome);
        viewPager.setAdapter(new BottomNavPagerAdapter(getSupportFragmentManager(),getLifecycle()));
        viewPager.setUserInputEnabled(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.navigation_rescue:
                        viewPager.setCurrentItem(0,false);
                        return true;
                    case R.id.navigation_feed:
                        viewPager.setCurrentItem(1,false);
                        return true;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(2,false);
                        return true;
                }
                return false;
            }
        });

    }

    public void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("animalhelp")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to Topic";//getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe";//getString(R.string.msg_subscribe_failed);
                        }
                        Log.i("TAGSEND",msg);
                        Toast.makeText(VolunteerHomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}