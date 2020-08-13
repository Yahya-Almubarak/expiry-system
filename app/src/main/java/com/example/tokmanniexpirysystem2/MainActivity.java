package com.example.tokmanniexpirysystem2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.tokmanniexpirysystem2.alarm.AlarmHelper;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.fragments.AddUserFragment;
import com.example.tokmanniexpirysystem2.fragments.AddProductFragment;
import com.example.tokmanniexpirysystem2.fragments.HistoryFragment;
import com.example.tokmanniexpirysystem2.fragments.ProductsFragment;
import com.example.tokmanniexpirysystem2.fragments.UsersFragment;
import com.example.tokmanniexpirysystem2.utilities.Authintication;
import com.example.tokmanniexpirysystem2.utilities.HistoryLogger;
import com.example.tokmanniexpirysystem2.utilities.Role;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public User user;
    public AlarmHelper alarmHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Authintication.user == null) {
            user = (User) getIntent().getExtras().getBundle("user").get("user");
            Authintication.userName = getIntent().getExtras().getString("username");
            Authintication.role = getIntent().getExtras().getString("role");
            Authintication.user = user;
        }
        HistoryLogger.clearHistory(this.getApplicationContext());



        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefUserAuthinticationOn = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_USER_AUTHENTICATION, true );
        if(!prefUserAuthinticationOn) {
            navView.getMenu().findItem(R.id.navigation_add_user).setVisible(false);
            navView.getMenu().findItem(R.id.navigation_users).setVisible(false);
        }
        else if(Authintication.role.equals(Role.ADMIN.toString()) && Authintication.userName.equals("admin")) {
            navView.getMenu().findItem(R.id.navigation_add_user).setIcon(R.drawable.icons8_edit_profile_24);
            navView.getMenu().findItem(R.id.navigation_add_user).setTitle(R.string.nav_edit_user_button_title);
        }
        else if(Authintication.role.equals(Role.ADMIN.toString())) {
            navView.getMenu().findItem(R.id.navigation_add_user).setIcon(R.drawable.ic_add_user_24dp);
        } else {
            navView.getMenu().findItem(R.id.navigation_add_user).setIcon(R.drawable.icons8_edit_profile_24);
            navView.getMenu().findItem(R.id.navigation_add_user).setTitle(R.string.nav_edit_user_button_title);
            navView.getMenu().findItem(R.id.navigation_users).setVisible(false);
        }


        navView.setOnNavigationItemSelectedListener(listener);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new ProductsFragment();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
        AlarmHelper.getInstance(this).cancelAlarm();
        AlarmHelper.getInstance(this).setAlarm();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
            break;
            case R.id.action_signout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.  navigation_products:
                    fragment = new ProductsFragment();
                    break;
                case R.id.navigation_edit_product:
                    fragment = new AddProductFragment();
                    break;
                case R.id.navigation_users:
                    fragment = new UsersFragment();
                    break;
                case R.id.navigation_add_user:
                    fragment = new AddUserFragment();
                    break;
                case R.id.navigation_history:
                    fragment = new HistoryFragment();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();
            return true;
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
