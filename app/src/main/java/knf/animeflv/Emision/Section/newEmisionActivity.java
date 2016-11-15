package knf.animeflv.Emision.Section;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.ndczz.infinityloading.InfinityLoading;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.Emision.DateCompare;
import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 05/03/2016.
 */
public class newEmisionActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    LinearLayout layout;

    @Bind(R.id.emision_loading_screen)
    LinearLayout screen;
    @Bind(R.id.loader_emision)
    InfinityLoading loading;


    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static String UTCtoLocalEm(String utc) {
        String convert = "~00:00PM";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc;
        }
        return convert;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emision);
        initActivity();
        toolbar = (Toolbar) findViewById(R.id.emision_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cargando...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        viewPager = (ViewPager) findViewById(R.id.vp_emision);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.st_Emision);
        layout = (LinearLayout) findViewById(R.id.LY_dir);
        loading.setProgressColor(ThemeUtils.getAcentColor(this));
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            viewPagerTab.setBackgroundColor(getResources().getColor(android.R.color.black));
            viewPagerTab.setSelectedIndicatorColors(getResources().getColor(R.color.prim));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        }
        int corePoolSize = 60;
        int maximumPoolSize = 80;
        int keepAliveTime = 10;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
        new Loader(this).executeOnExecutor(threadPoolExecutor);
    }

    private void setTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeDarkRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeDarkNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeDarkGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeDarkVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeDarkRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeDarkMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeMorado);
            }
        }
    }

    private int getActualDayCode() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int code;
        switch (day) {
            case Calendar.MONDAY:
                code = 1;
                break;
            case Calendar.TUESDAY:
                code = 2;
                break;
            case Calendar.WEDNESDAY:
                code = 3;
                break;
            case Calendar.THURSDAY:
                code = 4;
                break;
            case Calendar.FRIDAY:
                code = 5;
                break;
            case Calendar.SATURDAY:
                code = 6;
                break;
            case Calendar.SUNDAY:
                code = 7;
                break;
            default:
                code = 0;
                break;
        }
        return code;
    }

    private void initActivity() {
        if (!isXLargeScreen(getApplicationContext())) { //Portrait
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void enterReveal() {
        screen.setVisibility(View.GONE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(screen, "alpha", 1f, 0f);
        anim.setDuration(500);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);
                anim.setDuration(500);
                anim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private class Loader extends AsyncTask<String, String, String> {
        Context context;

        public Loader(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            SyncHttpClient client = new SyncHttpClient();
            client.setTimeout(10000);
            client.setConnectTimeout(10000);
            client.setResponseTimeout(10000);
            client.get(new Parser().getBaseUrl(TaskType.NORMAL, context) + "emisionlist.php", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    List<TimeCompareModel> organizar = new ArrayList<>();
                    List<List<TimeCompareModel>> comparelists = new ArrayList<List<TimeCompareModel>>();
                    comparelists.add(EmisionChecker.getLcode1());
                    comparelists.add(EmisionChecker.getLcode2());
                    comparelists.add(EmisionChecker.getLcode3());
                    comparelists.add(EmisionChecker.getLcode4());
                    comparelists.add(EmisionChecker.getLcode5());
                    comparelists.add(EmisionChecker.getLcode6());
                    comparelists.add(EmisionChecker.getLcode7());
                    for (List<TimeCompareModel> models : comparelists) {
                        models.clear();
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    try {
                        JSONArray array = response.getJSONArray("emision");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String hora = object.getString("hour");
                            if (!hora.equals("null")) {
                                String aid = object.getString("aid");
                                editor.putInt(aid + "onday", Integer.parseInt(object.getString("daycode")));
                                editor.putString(aid + "onhour", hora);
                                organizar.add(new TimeCompareModel(aid, context));
                            }
                        }
                        editor.apply();
                        Collections.sort(organizar, new DateCompare());
                        for (TimeCompareModel compareModel : organizar) {
                            int day = preferences.getInt(compareModel.getAid() + "onday", 0);
                            if (day != 0) {
                                boolean isotherday = compareModel.getTime().contains("AM") && UTCtoLocalEm(compareModel.getTime()).contains("PM");
                                if (!isotherday) {
                                    comparelists.get(day - 1).add(compareModel);
                                } else {
                                    int daybefore;
                                    if (day - 2 <= -1) {
                                        daybefore = 7;
                                    } else {
                                        daybefore = day - 2;
                                    }
                                    comparelists.get(daybefore - 1).add(compareModel);
                                }
                            }
                        }
                        EmisionChecker.setLcode1(comparelists.get(0));
                        EmisionChecker.setLcode5(comparelists.get(4));
                        EmisionChecker.setLcode2(comparelists.get(1));
                        EmisionChecker.setLcode6(comparelists.get(5));
                        EmisionChecker.setLcode3(comparelists.get(2));
                        EmisionChecker.setLcode7(comparelists.get(6));
                        EmisionChecker.setLcode4(comparelists.get(3));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getSupportActionBar().setTitle("Emision");
                                Bundle code1 = new Bundle();
                                code1.putInt("code", 1);
                                Bundle code2 = new Bundle();
                                code2.putInt("code", 2);
                                Bundle code3 = new Bundle();
                                code3.putInt("code", 3);
                                Bundle code4 = new Bundle();
                                code4.putInt("code", 4);
                                Bundle code5 = new Bundle();
                                code5.putInt("code", 5);
                                Bundle code6 = new Bundle();
                                code6.putInt("code", 6);
                                Bundle code7 = new Bundle();
                                code7.putInt("code", 7);
                                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                                        getSupportFragmentManager(), FragmentPagerItems.with(context)
                                        .add("LUNES", newDayFragment.class, code1)
                                        .add("MARTES", newDayFragment.class, code2)
                                        .add("MIERCOLES", newDayFragment.class, code3)
                                        .add("JUEVES", newDayFragment.class, code4)
                                        .add("VIERNES", newDayFragment.class, code5)
                                        .add("SABADO", newDayFragment.class, code6)
                                        .add("DOMINGO", newDayFragment.class, code7)
                                        .create());
                                viewPager.setOffscreenPageLimit(7);
                                viewPager.setAdapter(adapter);
                                viewPagerTab.setViewPager(viewPager);
                                viewPager.setCurrentItem(Math.abs(getActualDayCode() - 1), true);
                                enterReveal();
                            }
                        });
                    } catch (Exception e) {
                        Logger.Error(newEmisionActivity.this.getClass(), e);
                        finish();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Logger.Error(newEmisionActivity.this.getClass(), throwable);
                    if (throwable instanceof java.net.SocketTimeoutException)
                        Toaster.toast("Time Out!");
                    finish();
                }
            });
            return null;
        }
    }
}
