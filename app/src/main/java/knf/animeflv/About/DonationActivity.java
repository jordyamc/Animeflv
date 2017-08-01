package knf.animeflv.About;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import xdroid.toaster.Toaster;

public class DonationActivity extends AppCompatActivity {
    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static final String PAYPAL_EMAIL = "jordyamc@hotmail.com";
    private static final String PATREON_LINK = "https://www.patreon.com/animeflvapp";
    private static final String BITCOIN_WALLET = "1AVXBGokw8gjEY5E3PV8B93E4ZT5APrzq7";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.paypal_donate)
    Button paypal;
    @BindView(R.id.patreon_donate)
    Button patreon;
    @BindView(R.id.bitcoin_donate)
    Button bitcoin;
    private CustomTabsSession session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donations);
        ButterKnife.bind(this);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        Drawable drawable = getResources().getDrawable(R.drawable.clear);
        drawable.setColorFilter(theme.toolbarNavigation, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("Donaciones");
        toolbar.setBackgroundColor(theme.primary);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        toolbar.getRootView().setBackgroundColor(theme.isDark ? ColorsRes.Negro(this) : ColorsRes.Blanco(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChromeTab(PaypalUri(), Color.parseColor("#003087"));
                TrackingHelper.action(DonationActivity.this, "Donation", TrackingHelper.ACTION_DONATE_PAYPAL);
            }
        });
        patreon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChromeTab(PatreonUri(), Color.parseColor("#F96854"));
                TrackingHelper.action(DonationActivity.this, "Donation", TrackingHelper.ACTION_DONATE_PATREON);
            }
        });
        bitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBitcoin();
                TrackingHelper.action(DonationActivity.this, "Donation", TrackingHelper.ACTION_DONATE_BITCOIN);
            }
        });
    }

    public Uri PaypalUri() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
        uriBuilder.appendQueryParameter("cmd", "_donations");

        uriBuilder.appendQueryParameter("business", PAYPAL_EMAIL);
        uriBuilder.appendQueryParameter("lc", "US");
        uriBuilder.appendQueryParameter("item_name", "Donacion");
        uriBuilder.appendQueryParameter("no_note", "1");
        uriBuilder.appendQueryParameter("no_shipping", "1");
        uriBuilder.appendQueryParameter("currency_code", "USD");
        return uriBuilder.build();
    }

    public Uri PatreonUri() {
        return Uri.parse(PATREON_LINK);
    }

    public void openBitcoin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("bitcoin:" + BITCOIN_WALLET));
        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            startActivity(intent);
        }
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(BITCOIN_WALLET, BITCOIN_WALLET);
        clipboard.setPrimaryClip(clip);
        Toaster.toast("Direccion de bitcoin copiada");
    }

    private void warmUpTabs() {
        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                customTabsClient.warmup(0);
                session = customTabsClient.newSession(null);
                if (session != null) {
                    session.mayLaunchUrl(PaypalUri(), null, null);
                    session.mayLaunchUrl(PatreonUri(), null, null);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                session = null;
            }
        });
    }

    private void openChromeTab(Uri uri, @ColorInt int color) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
        builder.setToolbarColor(color);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, uri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        warmUpTabs();
        TrackingHelper.track(this, TrackingHelper.DONATE);
    }
}
