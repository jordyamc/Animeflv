package knf.animeflv;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.smaato.soma.AdDimension;
import com.smaato.soma.BannerView;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.interstitial.Interstitial;
import com.smaato.soma.interstitial.InterstitialAdListener;
import com.smaato.soma.video.VASTAdListener;
import com.smaato.soma.video.Video;

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

public class ADS extends AppCompatActivity implements InterstitialAdListener, VASTAdListener {
    // Remove the below line after defining your own ad unit ID.
    private Button inter_ad1;
    private Button inter_ad2;
    private Button inter_ad3;
    private Video mInterstitialAd1;
    private Interstitial mInterstitialAd2;
    private Interstitial mInterstitialAd3;
    private FrameLayout ad_arriba_1;
    private FrameLayout ad_arriba_2;
    private FrameLayout ad_abajo_1;
    private FrameLayout ad_abajo_2;
    private BannerView ad1;
    private BannerView ad2;
    private BannerView ad3;
    private BannerView ad4;

    Toolbar toolbar;

    int ad1_id;
    int ad2_id;
    int ad3_id;
    int ad4_id;

    int inter1_id;
    int inter2_id;
    int inter3_id;

    int publisher_id;

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

        ad1_id = 130070017;
        ad2_id = 130070020;
        ad3_id = 130070022;
        ad4_id = 130070023;

        inter1_id = 130070018;
        inter2_id = 130070019;
        inter3_id = 130070021;

        publisher_id = 1100015447;

        Debugger.setDebugMode(Debugger.Level_0);

        mInterstitialAd1 = new Video(this);
        mInterstitialAd2 = new Interstitial(this);
        mInterstitialAd3 = new Interstitial(this);

        //mInterstitialAd1.setInterstitialAdListener(this);
        mInterstitialAd2.setInterstitialAdListener(this);
        mInterstitialAd3.setInterstitialAdListener(this);

        loadInterstitial1();
        loadInterstitial2();
        loadInterstitial3();

        ad_arriba_1 = (FrameLayout) findViewById(R.id.ad_arriba_1);
        ad_arriba_2 = (FrameLayout) findViewById(R.id.ad_arriba_2);
        ad_abajo_1 = (FrameLayout) findViewById(R.id.ad_abajo_1);
        ad_abajo_2 = (FrameLayout) findViewById(R.id.ad_abajo_2);

        ad1 = new BannerView(this);
        ad2 = new BannerView(this);
        ad3 = new BannerView(this);
        ad4 = new BannerView(this);

        ad1.getAdSettings().setPublisherId(publisher_id);
        ad2.getAdSettings().setPublisherId(publisher_id);
        ad3.getAdSettings().setPublisherId(publisher_id);
        ad4.getAdSettings().setPublisherId(publisher_id);

        ad1.getAdSettings().setAdspaceId(ad1_id);
        ad2.getAdSettings().setAdspaceId(ad2_id);
        ad3.getAdSettings().setAdspaceId(ad3_id);
        ad4.getAdSettings().setAdspaceId(ad4_id);

        ad1.getAdSettings().setAdDimension(AdDimension.DEFAULT);
        ad2.getAdSettings().setAdDimension(AdDimension.DEFAULT);
        ad3.getAdSettings().setAdDimension(AdDimension.DEFAULT);
        ad4.getAdSettings().setAdDimension(AdDimension.DEFAULT);

        ad_arriba_1.addView(ad1, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150));
        ad_arriba_2.addView(ad2, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150));
        ad_abajo_1.addView(ad3, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150));
        ad_abajo_2.addView(ad4, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150));

        ad1.asyncLoadNewBanner();
        ad2.asyncLoadNewBanner();
        ad3.asyncLoadNewBanner();
        ad4.asyncLoadNewBanner();
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

    @Override
    public void onReadyToShow() {

    }

    @Override
    public void onWillShow() {

    }

    @Override
    public void onWillOpenLandingPage() {

    }

    @Override
    public void onWillClose() {
        goToNextLevel1();
        goToNextLevel2();
        goToNextLevel3();
    }

    @Override
    public void onFailedToLoadAd() {

    }

    private void showInterstitial1() {
        mInterstitialAd1.show();
    }

    private void showInterstitial2() {
        mInterstitialAd2.show();
    }

    private void showInterstitial3() {
        mInterstitialAd3.show();
    }

    private void loadInterstitial1() {
        mInterstitialAd1.getAdSettings().setPublisherId(publisher_id);
        mInterstitialAd1.getAdSettings().setAdspaceId(inter1_id);
        mInterstitialAd1.asyncLoadNewBanner();
    }

    private void loadInterstitial2() {
        mInterstitialAd2.getAdSettings().setPublisherId(publisher_id);
        mInterstitialAd2.getAdSettings().setAdspaceId(inter2_id);
        mInterstitialAd2.asyncLoadNewBanner();
    }

    private void loadInterstitial3() {
        mInterstitialAd3.getAdSettings().setPublisherId(publisher_id);
        mInterstitialAd3.getAdSettings().setAdspaceId(inter3_id);
        mInterstitialAd3.asyncLoadNewBanner();
    }

    private void goToNextLevel1() {
        //mInterstitialAd1 = new Interstitial(this);
        loadInterstitial1();
    }

    private void goToNextLevel2() {
        mInterstitialAd2 = new Interstitial(this);
        loadInterstitial2();
    }

    private void goToNextLevel3() {
        mInterstitialAd3 = new Interstitial(this);
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
