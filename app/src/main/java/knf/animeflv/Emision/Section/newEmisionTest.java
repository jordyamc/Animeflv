package knf.animeflv.Emision.Section;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.ndczz.infinityloading.InfinityLoading;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.Application;
import knf.animeflv.Emision.DateCompare;
import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.EMISION;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TimeCompare;
import xdroid.toaster.Toaster;

public class newEmisionTest extends AppCompatActivity {
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
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emision);
        initActivity();
        Application application = (Application) getApplication();
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
            viewPagerTab.setDistributeEvenly(isXLargeScreen(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        }
        new Loader(this).check();
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
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            recreate();
        }
        return true;
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

    private Bundle getcode(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("code", i);
        return bundle;
    }

    private class Loader {
        private Context context;

        public Loader(Context context) {
            this.context = context;
        }

        public void check() {
            BaseGetter.getJson(context, new EMISION(), new BaseGetter.AsyncInterface() {
                @Override
                public void onFinish(String json) {
                    asyncStart(json);
                }
            });
        }

        private void asyncStart(final String json) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if (FileUtil.isJSONValid(json)) {
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
                        List<TimeCompareModel> code1 = new ArrayList<>();
                        List<TimeCompareModel> code2 = new ArrayList<>();
                        List<TimeCompareModel> code3 = new ArrayList<>();
                        List<TimeCompareModel> code4 = new ArrayList<>();
                        List<TimeCompareModel> code5 = new ArrayList<>();
                        List<TimeCompareModel> code6 = new ArrayList<>();
                        List<TimeCompareModel> code7 = new ArrayList<>();
                        List<List<TimeCompareModel>> codes = new ArrayList<List<TimeCompareModel>>();
                        codes.add(code1);
                        codes.add(code2);
                        codes.add(code3);
                        codes.add(code4);
                        codes.add(code5);
                        codes.add(code6);
                        codes.add(code7);
                        try {
                            JSONObject response = new JSONObject(json);
                            JSONArray array = response.getJSONArray("emision");
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray objects = array.getJSONArray(i);
                                for (int e = 0; e < objects.length(); e++) {
                                    JSONObject object = objects.getJSONObject(e);
                                    String hora = object.getString("hour");
                                    if (!hora.equals("null")) {
                                        String aid = object.getString("aid");
                                        int formatedday = TimeCompare.getFormatedDaycodeFromUTC(hora + "-" + (i + 1));
                                        boolean isotherday = i + 1 != formatedday;
                                        if (!isotherday) {
                                            codes.get(i).add(new TimeCompareModel(aid, hora, context));
                                        } else {
                                            codes.get(formatedday - 1).add(new TimeCompareModel(aid, hora, context));
                                        }
                                    }
                                }
                                Collections.sort(codes.get(i), new DateCompare());
                            }

                            EmisionChecker.setLcode1(codes.get(0));
                            EmisionChecker.setLcode2(codes.get(1));
                            EmisionChecker.setLcode3(codes.get(2));
                            EmisionChecker.setLcode4(codes.get(3));
                            EmisionChecker.setLcode5(codes.get(4));
                            EmisionChecker.setLcode6(codes.get(5));
                            EmisionChecker.setLcode7(codes.get(6));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getSupportActionBar().setTitle("Emision");
                                    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                                            getSupportFragmentManager(), FragmentPagerItems.with(context)
                                            .add("LUNES", newDayFragment.class, getcode(1))
                                            .add("MARTES", newDayFragment.class, getcode(2))
                                            .add("MIERCOLES", newDayFragment.class, getcode(3))
                                            .add("JUEVES", newDayFragment.class, getcode(4))
                                            .add("VIERNES", newDayFragment.class, getcode(5))
                                            .add("SABADO", newDayFragment.class, getcode(6))
                                            .add("DOMINGO", newDayFragment.class, getcode(7))
                                            .create());
                                    viewPager.setOffscreenPageLimit(7);
                                    viewPager.setAdapter(adapter);
                                    viewPagerTab.setViewPager(viewPager);
                                    viewPager.setCurrentItem(Math.abs(getActualDayCode() - 1), true);
                                    enterReveal();
                                }
                            });
                        } catch (Exception e) {
                            Logger.Error(newEmisionTest.this.getClass(), e);
                            Toaster.toast("Error - " + e.getCause());
                            finish();
                        }
                    } else {
                        Toaster.toast("Error al obtener Json");
                        finish();
                    }
                    return null;
                }
            }.executeOnExecutor(ExecutorManager.getExecutor());
        }
    }
}
