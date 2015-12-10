package knf.animeflv;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import xdroid.toaster.Toaster;

public class ADS extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private Button inter_ad1;
    private Button inter_ad2;
    private Button inter_ad3;
    private InterstitialAd mInterstitialAd1;
    private InterstitialAd mInterstitialAd2;
    private InterstitialAd mInterstitialAd3;
    private FrameLayout ad_arriba_1;
    private FrameLayout ad_arriba_2;
    private FrameLayout ad_abajo_1;
    private FrameLayout ad_abajo_2;
    private AdView ad1;
    private AdView ad2;
    private AdView ad3;
    private AdView ad4;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        Toaster.toast("Si aprecias esta app, considera hacerle click a algunos anuncios y/o botones :D");
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_ads);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Publicidad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inter_ad1 = ((Button) findViewById(R.id.pop_ad_1));
        inter_ad2 = ((Button) findViewById(R.id.pop_ad_2));
        inter_ad3 = ((Button) findViewById(R.id.pop_ad_3));
        inter_ad1.setEnabled(false);
        inter_ad2.setEnabled(false);
        inter_ad3.setEnabled(false);
        inter_ad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial1();
            }
        });
        inter_ad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial2();
            }
        });
        inter_ad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial3();
            }
        });

        mInterstitialAd1 = newInterstitialAd1();
        loadInterstitial1();
        mInterstitialAd2 = newInterstitialAd2();
        loadInterstitial2();
        mInterstitialAd3 = newInterstitialAd3();
        loadInterstitial3();

        ad_arriba_1 = (FrameLayout) findViewById(R.id.ad_arriba_1);
        ad_arriba_2 = (FrameLayout) findViewById(R.id.ad_arriba_2);
        ad_abajo_1 = (FrameLayout) findViewById(R.id.ad_abajo_1);
        ad_abajo_2 = (FrameLayout) findViewById(R.id.ad_abajo_2);

        ad1 = new AdView(this);
        ad2 = new AdView(this);
        ad3 = new AdView(this);
        ad4 = new AdView(this);

        ad1.setAdUnitId(getString(R.string.banner_ad_unit_id1));
        ad2.setAdUnitId(getString(R.string.banner_ad_unit_id2));
        ad3.setAdUnitId(getString(R.string.banner_ad_unit_id3));
        ad4.setAdUnitId(getString(R.string.banner_ad_unit_id4));
        ad_arriba_1.addView(ad1);
        ad_arriba_2.addView(ad2);
        ad_abajo_1.addView(ad3);
        ad_abajo_2.addView(ad4);
        ad1.setAdSize(AdSize.SMART_BANNER);
        ad2.setAdSize(AdSize.SMART_BANNER);
        ad3.setAdSize(AdSize.SMART_BANNER);
        ad4.setAdSize(AdSize.SMART_BANNER);
        ad1.loadAd(new AdRequest.Builder().build());
        ad2.loadAd(new AdRequest.Builder().build());
        ad3.loadAd(new AdRequest.Builder().build());
        ad4.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private InterstitialAd newInterstitialAd1() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id1));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                inter_ad1.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                inter_ad1.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel1();
            }
        });
        return interstitialAd;
    }

    private InterstitialAd newInterstitialAd2() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id2));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                inter_ad2.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                inter_ad2.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel2();
            }
        });
        return interstitialAd;
    }

    private InterstitialAd newInterstitialAd3() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id3));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                inter_ad3.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                inter_ad3.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel3();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial1() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd1 != null && mInterstitialAd1.isLoaded()) {
            mInterstitialAd1.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel1();
        }
    }

    private void showInterstitial2() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd2 != null && mInterstitialAd2.isLoaded()) {
            mInterstitialAd2.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel2();
        }
    }

    private void showInterstitial3() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd3 != null && mInterstitialAd3.isLoaded()) {
            mInterstitialAd3.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel3();
        }
    }

    private void loadInterstitial1() {
        // Disable the next level button and load the ad.
        inter_ad1.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd1.loadAd(adRequest);
    }

    private void loadInterstitial2() {
        // Disable the next level button and load the ad.
        inter_ad2.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd2.loadAd(adRequest);
    }

    private void loadInterstitial3() {
        // Disable the next level button and load the ad.
        inter_ad3.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd3.loadAd(adRequest);
    }

    private void goToNextLevel1() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd1 = newInterstitialAd1();
        loadInterstitial1();
    }

    private void goToNextLevel2() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd2 = newInterstitialAd2();
        loadInterstitial2();
    }

    private void goToNextLevel3() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd3 = newInterstitialAd3();
        loadInterstitial3();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
