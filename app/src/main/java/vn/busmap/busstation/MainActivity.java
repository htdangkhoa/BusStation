package vn.busmap.busstation;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.busmap.busstation.Fragments.HomeFragment;
import vn.busmap.busstation.Utils.Services;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static LinearLayout btnBack, actionBar;
    public static TextView actionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        Services.Navigate(this, getSupportFragmentManager(), new HomeFragment(), null, false, null, Services.NO_ANIMATION);
    }

    private void initialize() {
        actionBar = (LinearLayout) findViewById(R.id.actionBar);
        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        actionTitle = (TextView) findViewById(R.id.actionTitle);

        btnBack.setOnClickListener(this);

        StatusBarUtil.setTranslucentForImageViewInFragment(this, null);
    }

    public void backFunction() {
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        if (fragmentCount <= 0) {
            System.exit(0);
        } else {
            String CURRENT_TAG = getSupportFragmentManager().getBackStackEntryAt(fragmentCount - 1).getName();
            Log.i("CURRENT_TAG", String.valueOf(CURRENT_TAG));
            if (CURRENT_TAG != null) {
                getSupportFragmentManager().popBackStack();
            } else {
                Intent homeIntent= new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backFunction();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnBack: {
                backFunction();
                break;
            }
        }
    }
}
