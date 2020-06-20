package restaurent.billing.demo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import restaurent.billing.demo.R;


public class MainActivity extends AppCompatActivity
{


    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);


        prefs = getSharedPreferences("restaurant.billing.demo", MODE_PRIVATE);


        if (prefs.getBoolean("firstrun", true)) {

            prefs.edit().putBoolean("firstrun", false).apply();
            prefs.edit().putBoolean("hasGCM", false).apply();
        }


        if(!prefs.getBoolean("hasGCM", false))
        {

            Fragment fragment = new RegisterFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }

        else
        {
            Fragment fragment = new ListViewComplainFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }
}