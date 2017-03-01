package knf.animeflv;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class ADS extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ad_button_1)
    Button inter_ad1;
    @BindView(R.id.ad_button_2)
    Button inter_ad2;
    @BindView(R.id.ad_button_3)
    Button inter_ad3;
    @BindView(R.id.ad_top_1)
    NativeExpressAdView top_1;
    @BindView(R.id.ad_top_2)
    NativeExpressAdView top_2;
    @BindView(R.id.ad_bottom_1)
    NativeExpressAdView bottom_1;
    @BindView(R.id.ad_bottom_2)
    NativeExpressAdView bottom_2;
    private InterstitialAd interstitialAd_1;
    private InterstitialAd interstitialAd_2;
    private InterstitialAd interstitialAd_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);
        Toaster.toast("Si aprecias esta app, considera hacerle click a algunos anuncios y/o botones :D");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }

        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ColorsRes.Negro(this));
                getWindow().setNavigationBarColor(ColorsRes.Negro(this));
            }
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Publicidad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        startAds();
    }

    private void startAds() {
        top_1.loadAd(new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build());
        top_2.loadAd(new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build());

        bottom_1.loadAd(new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build());
        bottom_2.loadAd(new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build());

        interstitialAd_1 = new InterstitialAd(this);
        interstitialAd_2 = new InterstitialAd(this);
        interstitialAd_3 = new InterstitialAd(this);

        interstitialAd_1.setAdUnitId(getString(R.string.interstitial_ad_1));
        interstitialAd_2.setAdUnitId(getString(R.string.interstitial_ad_2));
        interstitialAd_3.setAdUnitId(getString(R.string.interstitial_ad_3));

        requestInter1();
        requestInter2();
        requestInter3();

        inter_ad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd_1.isLoaded()) {
                    interstitialAd_1.show();
                } else if (interstitialAd_1.isLoading()) {
                    Toaster.toast("El anuncio esta cargando, intenta en unos segundos");
                } else {
                    requestInter1();
                }
            }
        });

        interstitialAd_1.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestInter1();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                requestInter1();
            }

            @Override
            public void onAdOpened() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad1.setEnabled(false);
                    }
                });
            }

            @Override
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad1.setEnabled(true);
                    }
                });
            }
        });

        inter_ad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd_2.isLoaded()) {
                    interstitialAd_2.show();
                } else if (interstitialAd_2.isLoading()) {
                    Toaster.toast("El anuncio esta cargando, intenta en unos segundos");
                } else {
                    requestInter2();
                }
            }
        });

        interstitialAd_2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestInter2();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                requestInter2();
            }

            @Override
            public void onAdOpened() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad2.setEnabled(false);
                    }
                });
            }

            @Override
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad2.setEnabled(true);
                    }
                });
            }
        });

        inter_ad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd_3.isLoaded()) {
                    interstitialAd_3.show();
                } else if (interstitialAd_3.isLoading()) {
                    Toaster.toast("El anuncio esta cargando, intenta en unos segundos");
                } else {
                    requestInter3();
                }
            }
        });

        interstitialAd_3.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestInter3();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                requestInter3();
            }

            @Override
            public void onAdOpened() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad3.setEnabled(false);
                    }
                });
            }

            @Override
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inter_ad3.setEnabled(true);
                    }
                });
            }
        });

    }

    private void requestInter1() {
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build();
        interstitialAd_1.loadAd(adRequest);
    }

    private void requestInter2() {
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build();
        interstitialAd_2.loadAd(adRequest);
    }

    private void requestInter3() {
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*/.build();
        interstitialAd_3.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        interstitialAd_1.setAdListener(null);
        interstitialAd_2.setAdListener(null);
        interstitialAd_3.setAdListener(null);
        super.onDestroy();
    }
}
