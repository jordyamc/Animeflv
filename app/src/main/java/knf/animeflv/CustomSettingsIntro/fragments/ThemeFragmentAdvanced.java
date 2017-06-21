package knf.animeflv.CustomSettingsIntro.fragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.github.zawadz88.activitychooser.MaterialActivityChooserBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.ThemeHolder;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import xdroid.toaster.Toaster;

public class ThemeFragmentAdvanced extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    public static final String ACTION_THEME_CHANGE = "knf.animeflv.ACTION_THEME_CHANGE";
    private final int DURATION = 500;
    private final int MODE_NORMAL = 0;
    private final int MODE_PREVIEW = 1;
    private final int MODE_PREVIEW_FROM_FILE = 2;
    private final int MODE_LOADED = 3;
    @BindView(R.id.color_choose)
    RelativeLayout color_choose;
    @BindView(R.id.original)
    AppCompatButton button;
    @BindView(R.id.dark_theme)
    SwitchCompat switchCompat;
    @BindView(R.id.accent)
    ImageView accent;
    @BindView(R.id.linear)
    LinearLayout linearLayout;
    @BindView(R.id.status_bar)
    View status;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.card1)
    CardView card1;
    @BindView(R.id.card2)
    CardView card2;
    @BindView(R.id.card3)
    CardView card3;
    @BindView(R.id.tit1)
    TextView title1;
    @BindView(R.id.cap1)
    TextView cap1;
    @BindView(R.id.a_button_1)
    ImageButton action1;
    @BindView(R.id.tit2)
    TextView title2;
    @BindView(R.id.cap2)
    TextView cap2;
    @BindView(R.id.a_button_2)
    ImageButton action2;
    @BindView(R.id.tit3)
    TextView title3;
    @BindView(R.id.cap3)
    TextView cap3;
    @BindView(R.id.a_button_3)
    ImageButton action3;
    @BindView(R.id.sync)
    ImageView sync;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.apply)
    FloatingActionButton button_apply;
    CardView l_card;
    RelativeLayout l_background;
    Toolbar l_toolbar;
    private int MODE;
    private Uri current;
    private String name;

    @ColorInt
    public static int getDefault(Context context, String key) {
        boolean isDarkMode = ThemeUtils.isAmoled(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (key) {
            case "accentColor":
                return ThemeUtils.getAcentColor(context);
            case "theme_status":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Dark(context));
            case "theme_toolbar":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Prim(context));
            case "theme_card_normal":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Prim(context) : ColorsRes.Blanco(context));
            case "color_favs":
                return ColorsRes.in_favs(context);
            case "color_new":
                return ColorsRes.in_new(context);
            case "theme_background":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Blanco(context));
            case "theme_background_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Blanco(context));
            case "theme_card_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Blanco(context));
            case "theme_toolbar_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(context) : ColorsRes.Prim(context));
            case "theme_card_text":
                return preferences.getInt(key, isDarkMode ? ColorsRes.SecondaryTextDark(context) : ColorsRes.SecondaryTextLight(context));
            case "theme_toolbar_text":
                return preferences.getInt(key, ThemeUtils.isTablet(context) ? (isDarkMode ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));
            case "theme_icon_filter":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Holo_Dark(context) : ColorsRes.Holo_Light(context));
            case "theme_toolbar_navigation":
                return preferences.getInt(key, ThemeUtils.isTablet(context) ? (isDarkMode ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));
            default:
                return ColorsRes.Prim(context);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeDark(this);
        super.onCreate(savedInstanceState);
        setContentView(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false) ? R.layout.theme_fragment_advanced_force : R.layout.theme_fragment_advanced);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#4527A0"));
            getWindow().setNavigationBarColor(Color.parseColor("#4527A0"));
            status.setBackgroundColor(getDefault("theme_status"));
            status.setClickable(true);
        }
        button_apply.hide();
        ThemeHolder.isDark = ThemeUtils.isAmoled(getA());
        ThemeHolder.accentColor = ThemeUtils.getAcentColor(getA());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.menu_main_sample);
        toolbar.setTitleTextColor(getDefault("theme_toolbar_text"));
        toolbar.setTitle("Recientes");
        toolbar.setBackgroundColor(getDefault("theme_toolbar"));
        ThemeUtils.setNavigationColor(toolbar, getDefault("theme_toolbar_navigation"));
        toolbar.setTag(getDefault("theme_toolbar_navigation"));
        button_apply.setBackgroundColor(ThemeUtils.getAcentColor(getA()));
        linearLayout.setBackgroundColor(getDefault("theme_background"));
        switchCompat.setChecked(ThemeUtils.isAmoled(getA()));
        accent.setColorFilter(ThemeUtils.getAcentColor(getA()));
        card1.setCardBackgroundColor(getDefault("theme_card_normal"));
        card2.setCardBackgroundColor(getDefault("color_favs"));
        card3.setCardBackgroundColor(getDefault("color_new"));
        title1.setTextColor(getDefault("theme_card_text"));
        title2.setTextColor(getDefault("theme_card_text"));
        title3.setTextColor(getDefault("theme_card_text"));
        cap1.setTextColor(ThemeUtils.getAcentColor(getA()));
        cap2.setTextColor(ThemeUtils.getAcentColor(getA()));
        cap3.setTextColor(ThemeUtils.getAcentColor(getA()));
        action1.setColorFilter(getDefault("theme_icon_filter"));
        action1.setTag(getDefault("theme_icon_filter"));
        action2.setColorFilter(getDefault("theme_icon_filter"));
        action2.setTag(getDefault("theme_icon_filter"));
        action3.setColorFilter(getDefault("theme_icon_filter"));
        action3.setTag(getDefault("theme_icon_filter"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDefault();
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = getThemeName();
                if (name == null) {
                    showSaveDialog(name, true);
                } else {
                    save(name, true);
                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                ThemeHolder.isDark = checked;
                ThemeUtils.setAmoled(getA(), checked);
                onColorChange();
            }
        });
        color_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog("accentColor", ColorType.DEFAULT);
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog("theme_status", ColorType.CUSTOM);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolbarPopup(view);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNavigationPopup(view);
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardPopup("theme_card_normal", false, view);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardPopup("color_favs", true, view);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardPopup("color_new", true, view);
            }
        });
        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog("theme_icon_filter", ColorType.CUSTOM);
            }
        });
        action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog("theme_icon_filter", ColorType.CUSTOM);
            }
        });
        action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog("theme_icon_filter", ColorType.CUSTOM);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog("theme_background", ColorType.CUSTOM);
            }
        });
        status.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setDefault("theme_status");
                return true;
            }
        });
        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDefaultToolbarPopup(view);
                return true;
            }
        });
        card1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDefaultCardPopup("theme_card_normal", view);
                return true;
            }
        });
        card2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDefaultCardPopup("color_favs", view);
                return true;
            }
        });
        card3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDefaultCardPopup("color_new", view);
                return true;
            }
        });
        action1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setDefault("theme_icon_filter");
                return true;
            }
        });
        action2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setDefault("theme_icon_filter");
                return true;
            }
        });
        action3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setDefault("theme_icon_filter");
                return true;
            }
        });
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setDefault("theme_background");
                return true;
            }
        });
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getA())
                        .content("¿Aplicar tema " + name.replace(".aflvtheme", "") + "?")
                        .positiveText("Aplicar")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                try {
                                    load(FileUtil.getStringFromFile(getA(), current));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toaster.toast("Error al aplicar tema!!!");
                                    onBackPressed();
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                onBackPressed();
                            }
                        }).build().show();
            }
        });
        try {
            l_toolbar = findViewById(R.id.l_toolbar);
            l_background = findViewById(R.id.l_background);
            l_card = findViewById(R.id.l_card);
            l_card.setCardBackgroundColor(getDefault("theme_toolbar"));
            l_toolbar.setBackgroundColor(getDefault("theme_toolbar_tablet"));
            l_background.setBackgroundColor(getDefault("theme_background_tablet"));
            l_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showColorDialog("theme_toolbar", ColorType.CUSTOM);
                }
            });
            l_toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showColorDialog("theme_toolbar_tablet", ColorType.CUSTOM);
                }
            });
            l_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showColorDialog("theme_background_tablet", ColorType.CUSTOM);
                }
            });
            l_toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setDefault("theme_toolbar_tablet");
                    return true;
                }
            });
            l_background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setDefault("theme_background_tablet");
                    return true;
                }
            });
            l_card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setDefault("theme_toolbar");
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e("Theme", "No tablet");
        }
        checkFile();
    }

    private void checkFile() {
        if (getIntent().getData() == null) {
            MODE = MODE_NORMAL;
        } else {
            MODE = MODE_PREVIEW_FROM_FILE;
            switchCompat.setEnabled(false);
            button.setEnabled(false);
            current = getIntent().getData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(FileUtil.getStringFromFile(getA(), current));
                        name = object.getString("name");
                        onColorPreview(object.getJSONArray("theme"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Theme Json", "Uri: " + getIntent().getData().toString() + "\nFile: " + current.getPath() + "\n\n" + FileUtil.getStringFromFile(getA(), current));
                        Toaster.toast("Error al cargar tema!!!");
                        finish();
                    }
                }
            }, 1000);
        }
    }

    private void load(JSONArray array) throws JSONException {
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("key").equals("is_amoled")) {
                preferences.putBoolean(object.getString("key"), object.getBoolean("value"));
            } else {
                preferences.putInt(object.getString("key"), object.getInt("value"));
            }
        }
        preferences.commit();
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        ThemeHolder.isDark = theme.isDark;
        ThemeHolder.accentColor = theme.accent;
        ThemeHolder.old_accentColor = -1;
        onColorChange();
        button_apply.hide();
        setThemeName(name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
                switchCompat.setEnabled(true);
            }
        });
        MODE = MODE_LOADED;
        Toaster.toast("Tema aplicado!");
    }

    private void load(String raw) throws JSONException {
        load(new JSONObject(raw).getJSONArray("theme"));
    }

    private void showColorDialog(final String key, ColorType colorType) {
        if (MODE != MODE_PREVIEW && MODE != MODE_PREVIEW_FROM_FILE)
            if (colorType == ColorType.DEFAULT) {
                int[] colorl = new int[]{
                        ColorsRes.Naranja(this),
                        ColorsRes.Rojo(this),
                        ColorsRes.Gris(this),
                        ColorsRes.Verde(this),
                        ColorsRes.Rosa(this),
                        ColorsRes.Morado(this)
                };
                new ColorChooserDialog.Builder(this, R.string.color_chooser)
                        .theme(Theme.DARK)
                        .customColors(colorl, null)
                        .dynamicButtonColor(true)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .doneButton(android.R.string.ok)
                        .cancelButton(R.string.back)
                        .preselect(PreferenceManager.getDefaultSharedPreferences(this).getInt("accentColor", ColorsRes.Naranja(this)))
                        .accentMode(true)
                        .tag(ColorChooserDialog.TAG_ACCENT)
                        .build().show(this);
            } else {
                new ColorChooserDialog.Builder(this, R.string.color_chooser)
                        .theme(Theme.DARK)
                        .dynamicButtonColor(true)
                        .allowUserColorInput(true)
                        .allowUserColorInputAlpha(colorType == ColorType.CUSTOM_ARGB)
                        .preselect(getDefault(key))
                        .doneButton(R.string.done)
                        .cancelButton(R.string.cancel)
                        .presetsButton(R.string.presents)
                        .customButton(R.string.custom)
                        .backButton(R.string.back)
                        .accentMode(false)
                        .tag(key)
                        .build().show(this);
            }
    }

    @ColorInt
    private int getDefault(String key) {
        boolean isDarkMode = ThemeUtils.isAmoled(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (key) {
            case "theme_status":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Dark(this));
            case "theme_toolbar":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Prim(this));
            case "theme_card_normal":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Prim(this) : ColorsRes.Blanco(this));
            case "color_favs":
                return ColorsRes.in_favs(this);
            case "color_new":
                return ColorsRes.in_new(this);
            case "theme_background":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Blanco(this));
            case "theme_background_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Blanco(this));
            case "theme_card_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Blanco(this));
            case "theme_toolbar_tablet":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Negro(this) : ColorsRes.Prim(this));
            case "theme_card_text":
                return preferences.getInt(key, isDarkMode ? ColorsRes.SecondaryTextDark(getA()) : ColorsRes.SecondaryTextLight(getA()));
            case "theme_toolbar_text":
                return preferences.getInt(key, ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()));
            case "theme_icon_filter":
                return preferences.getInt(key, isDarkMode ? ColorsRes.Holo_Dark(getA()) : ColorsRes.Holo_Light(getA()));
            case "theme_toolbar_navigation":
                return preferences.getInt(key, ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()));
            default:
                return ColorsRes.Prim(this);
        }
    }

    private void setDefault(final String key) {
        if (MODE != MODE_PREVIEW && MODE != MODE_PREVIEW_FROM_FILE)
            new MaterialDialog.Builder(this)
                    .content("¿Desea regresar al color predeterminado?")
                    .theme(ThemeHolder.isDark ? Theme.DARK : Theme.LIGHT)
                    .positiveText("si")
                    .negativeText("cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            boolean isDarkMode = ThemeUtils.isAmoled(getA());
                            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getA()).edit();
                            switch (key) {
                                case "theme_status":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Dark(getA()));
                                    break;
                                case "theme_toolbar":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA()));
                                    break;
                                case "theme_card_normal":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA()));
                                    break;
                                case "color_favs":
                                    preferences.putInt(key, Color.argb(100, 26, 206, 246));
                                    break;
                                case "color_new":
                                    preferences.putInt(key, Color.argb(100, 253, 250, 93));
                                    break;
                                case "theme_background":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()));
                                    break;
                                case "theme_background_tablet":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()));
                                    break;
                                case "theme_card_tablet":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()));
                                    break;
                                case "theme_toolbar_tablet":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA()));
                                    break;
                                case "theme_card_text":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.SecondaryTextDark(getA()) : ColorsRes.SecondaryTextLight(getA()));
                                    break;
                                case "theme_toolbar_text":
                                    preferences.putInt(key, ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()));
                                    break;
                                case "theme_icon_filter":
                                    preferences.putInt(key, isDarkMode ? ColorsRes.Holo_Dark(getA()) : ColorsRes.Holo_Light(getA()));
                                    break;
                                case "theme_toolbar_navigation":
                                    preferences.putInt(key, ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()));
                                    break;
                            }
                            preferences.apply();
                            onColorChange();
                        }
                    }).build().show();
    }

    private void setAllDefault() {
        if (MODE != MODE_PREVIEW && MODE != MODE_PREVIEW_FROM_FILE)
            new MaterialDialog.Builder(this)
                    .content("¿Desea aplicar el tema original " + (ThemeHolder.isDark ? "obscuro" : "claro") + "?")
                    .positiveText("continuar")
                    .negativeText("cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            boolean isDarkMode = ThemeUtils.isAmoled(getA());
                            PreferenceManager.getDefaultSharedPreferences(getA()).edit()
                                    .putInt("theme_status", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Dark(getA()))
                                    .putInt("theme_toolbar", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA()))
                                    .putInt("theme_card_normal", isDarkMode ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA()))
                                    .putInt("color_favs", Color.argb(100, 26, 206, 246))
                                    .putInt("color_new", Color.argb(100, 253, 250, 93))
                                    .putInt("theme_background", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()))
                                    .putInt("theme_background_tablet", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()))
                                    .putInt("theme_card_tablet", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()))
                                    .putInt("theme_toolbar_tablet", isDarkMode ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA()))
                                    .putInt("theme_toolbar_navigation", ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()))
                                    .putInt("theme_icon_filter", isDarkMode ? ColorsRes.Holo_Dark(getA()) : ColorsRes.Holo_Light(getA()))
                                    .putInt("theme_toolbar_text", ThemeUtils.isTablet(getA()) ? (isDarkMode ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(getA()))
                                    .putInt("theme_card_text", isDarkMode ? ColorsRes.SecondaryTextDark(getA()) : ColorsRes.SecondaryTextLight(getA()))
                                    .apply();
                            onColorChange();
                        }
                    }).build().show();
    }

    private Activity getA() {
        return this;
    }

    public void onColorPreview(JSONArray array) throws JSONException {
        ThemeUtils.Theme theme = ThemeUtils.Theme.fromJson(this, array);
        ValueAnimator animV = ValueAnimator.ofInt(((ColorDrawable) status.getBackground()).getColor(), theme.primaryDark);
        animV.setEvaluator(new ArgbEvaluator());
        animV.setDuration(DURATION);
        animV.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status != null)
                            status.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animV.start();

        ValueAnimator animM = ValueAnimator.ofInt(((ColorDrawable) toolbar.getBackground()).getColor(), theme.primary);
        animM.setEvaluator(new ArgbEvaluator());
        animM.setDuration(DURATION);
        animM.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                        if (l_card != null)
                            l_card.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animM.start();

        ValueAnimator animTN = ValueAnimator.ofInt((int) toolbar.getTag(), theme.toolbarNavigation);
        animTN.setEvaluator(new ArgbEvaluator());
        animTN.setDuration(DURATION);
        animTN.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            ThemeUtils.setNavigationColor(toolbar, (Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animTN.start();

        toolbar.setTag(theme.toolbarNavigation);

        ValueAnimator animTT = ValueAnimator.ofInt(getToolbarTitleColor(), theme.textColorToolbar);
        animTT.setEvaluator(new ArgbEvaluator());
        animTT.setDuration(DURATION);
        animTT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            toolbar.setTitleTextColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animTT.start();

        if (l_toolbar != null) {
            ValueAnimator animLT = ValueAnimator.ofInt(((ColorDrawable) l_toolbar.getBackground()).getColor(), theme.tablet_toolbar);
            animLT.setEvaluator(new ArgbEvaluator());
            animLT.setDuration(DURATION);
            animLT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    getA().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (l_toolbar != null)
                                l_toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                }
            });
            animLT.start();
        }

        if (l_background != null) {
            ValueAnimator animLB = ValueAnimator.ofInt(((ColorDrawable) l_background.getBackground()).getColor(), theme.tablet_background);
            animLB.setEvaluator(new ArgbEvaluator());
            animLB.setDuration(DURATION);
            animLB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    getA().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (l_background != null)
                                l_background.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                }
            });
            animLB.start();
        }

        ValueAnimator animB = ValueAnimator.ofInt(((ColorDrawable) linearLayout.getBackground()).getColor(), theme.background);
        animB.setEvaluator(new ArgbEvaluator());
        animB.setDuration(DURATION);
        animB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayout != null)
                            linearLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animB.start();

        ValueAnimator animC = ValueAnimator.ofInt(card1.getCardBackgroundColor().getDefaultColor(), theme.card_normal);
        animC.setEvaluator(new ArgbEvaluator());
        animC.setDuration(DURATION);
        animC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card1 != null)
                            card1.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animC.start();

        ValueAnimator animF = ValueAnimator.ofInt(card2.getCardBackgroundColor().getDefaultColor(), theme.card_fav);
        animF.setEvaluator(new ArgbEvaluator());
        animF.setDuration(DURATION);
        animF.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card2 != null)
                            card2.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animF.start();

        ValueAnimator animN = ValueAnimator.ofInt(card3.getCardBackgroundColor().getDefaultColor(), theme.card_new);
        animN.setEvaluator(new ArgbEvaluator());
        animN.setDuration(DURATION);
        animN.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card3 != null)
                            card3.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animN.start();

        ValueAnimator animT = ValueAnimator.ofInt(title1.getCurrentTextColor(), theme.textColorCard);
        animT.setEvaluator(new ArgbEvaluator());
        animT.setDuration(DURATION);
        animT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (title1 != null && title2 != null && title3 != null) {
                            title1.setTextColor((Integer) animation.getAnimatedValue());
                            title2.setTextColor((Integer) animation.getAnimatedValue());
                            title3.setTextColor((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animT.start();

        ValueAnimator animI = ValueAnimator.ofInt((int) action1.getTag(), theme.iconFilter);
        animI.setEvaluator(new ArgbEvaluator());
        animI.setDuration(DURATION);
        animI.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (action1 != null && action2 != null && action3 != null) {
                            action1.clearColorFilter();
                            action2.clearColorFilter();
                            action3.clearColorFilter();
                            action1.setColorFilter((Integer) animation.getAnimatedValue());
                            action2.setColorFilter((Integer) animation.getAnimatedValue());
                            action3.setColorFilter((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animI.start();

        int def_filter = theme.iconFilter;
        action1.setColorFilter(def_filter);
        action2.setColorFilter(def_filter);
        action3.setColorFilter(def_filter);

        ValueAnimator animS = ValueAnimator.ofInt(ThemeHolder.accentColor, theme.accent);
        animS.setEvaluator(new ArgbEvaluator());
        animS.setDuration(DURATION);
        animS.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cap1 != null && cap2 != null && cap3 != null && accent != null && button != null && switchCompat != null && button_apply != null) {
                            cap1.setTextColor((Integer) animation.getAnimatedValue());
                            cap2.setTextColor((Integer) animation.getAnimatedValue());
                            cap3.setTextColor((Integer) animation.getAnimatedValue());
                            accent.setColorFilter((Integer) animation.getAnimatedValue());
                            button.setSupportBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));
                            switchCompat.setThumbTintList(getThumbColor((Integer) animation.getAnimatedValue()));
                            switchCompat.setTrackTintList(getTrackColor((Integer) animation.getAnimatedValue()));
                            //button_apply.setBackgroundColor((Integer) animation.getAnimatedValue());
                            button_apply.setBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));
                        }
                    }
                });
            }

        });
        animS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ThemeHolder.old_accentColor = ThemeHolder.accentColor;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animS.start();
        button_apply.show();
    }

    public void onColorChange() {
        ValueAnimator animV = ValueAnimator.ofInt(((ColorDrawable) status.getBackground()).getColor(), getDefault("theme_status"));
        animV.setEvaluator(new ArgbEvaluator());
        animV.setDuration(DURATION);
        animV.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status != null)
                            status.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animV.start();

        ValueAnimator animM = ValueAnimator.ofInt(((ColorDrawable) toolbar.getBackground()).getColor(), getDefault("theme_toolbar"));
        animM.setEvaluator(new ArgbEvaluator());
        animM.setDuration(DURATION);
        animM.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                        if (l_card != null)
                            l_card.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animM.start();

        ValueAnimator animTN = ValueAnimator.ofInt((int) toolbar.getTag(), getDefault("theme_toolbar_navigation"));
        animTN.setEvaluator(new ArgbEvaluator());
        animTN.setDuration(DURATION);
        animTN.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            ThemeUtils.setNavigationColor(toolbar, (Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animTN.start();

        toolbar.setTag(getDefault("theme_toolbar_navigation"));

        ValueAnimator animTT = ValueAnimator.ofInt(getToolbarTitleColor(), getDefault("theme_toolbar_text"));
        animTT.setEvaluator(new ArgbEvaluator());
        animTT.setDuration(DURATION);
        animTT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            toolbar.setTitleTextColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animTT.start();

        if (l_toolbar != null) {
            ValueAnimator animLT = ValueAnimator.ofInt(((ColorDrawable) l_toolbar.getBackground()).getColor(), getDefault("theme_toolbar_tablet"));
            animLT.setEvaluator(new ArgbEvaluator());
            animLT.setDuration(DURATION);
            animLT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    getA().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (l_toolbar != null)
                                l_toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                }
            });
            animLT.start();
        }

        if (l_background != null) {
            ValueAnimator animLB = ValueAnimator.ofInt(((ColorDrawable) l_background.getBackground()).getColor(), getDefault("theme_background_tablet"));
            animLB.setEvaluator(new ArgbEvaluator());
            animLB.setDuration(DURATION);
            animLB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    getA().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (l_background != null)
                                l_background.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                }
            });
            animLB.start();
        }

        ValueAnimator animB = ValueAnimator.ofInt(((ColorDrawable) linearLayout.getBackground()).getColor(), getDefault("theme_background"));
        animB.setEvaluator(new ArgbEvaluator());
        animB.setDuration(DURATION);
        animB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayout != null)
                            linearLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animB.start();

        ValueAnimator animC = ValueAnimator.ofInt(card1.getCardBackgroundColor().getDefaultColor(), getDefault("theme_card_normal"));
        animC.setEvaluator(new ArgbEvaluator());
        animC.setDuration(DURATION);
        animC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card1 != null)
                            card1.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animC.start();

        ValueAnimator animF = ValueAnimator.ofInt(card2.getCardBackgroundColor().getDefaultColor(), getDefault("color_favs"));
        animF.setEvaluator(new ArgbEvaluator());
        animF.setDuration(DURATION);
        animF.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card2 != null)
                            card2.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animF.start();

        ValueAnimator animN = ValueAnimator.ofInt(card3.getCardBackgroundColor().getDefaultColor(), getDefault("color_new"));
        animN.setEvaluator(new ArgbEvaluator());
        animN.setDuration(DURATION);
        animN.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card3 != null)
                            card3.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animN.start();

        ValueAnimator animT = ValueAnimator.ofInt(title1.getCurrentTextColor(), getDefault("theme_card_text"));
        animT.setEvaluator(new ArgbEvaluator());
        animT.setDuration(DURATION);
        animT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (title1 != null && title2 != null && title3 != null) {
                            title1.setTextColor((Integer) animation.getAnimatedValue());
                            title2.setTextColor((Integer) animation.getAnimatedValue());
                            title3.setTextColor((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animT.start();

        ValueAnimator animI = ValueAnimator.ofInt((int) action1.getTag(), getDefault("theme_icon_filter"));
        animI.setEvaluator(new ArgbEvaluator());
        animI.setDuration(DURATION);
        animI.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (action1 != null && action2 != null && action3 != null) {
                            action1.clearColorFilter();
                            action2.clearColorFilter();
                            action3.clearColorFilter();
                            action1.setColorFilter((Integer) animation.getAnimatedValue());
                            action2.setColorFilter((Integer) animation.getAnimatedValue());
                            action3.setColorFilter((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animI.start();

        int def_filter = getDefault("theme_icon_filter");
        action1.setColorFilter(def_filter);
        action2.setColorFilter(def_filter);
        action3.setColorFilter(def_filter);

        ValueAnimator animS = ValueAnimator.ofInt((ThemeHolder.old_accentColor == -1 ? ThemeUtils.getAcentColor(getA()) : ThemeHolder.old_accentColor), ThemeHolder.accentColor);
        animS.setEvaluator(new ArgbEvaluator());
        animS.setDuration(DURATION);
        animS.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cap1 != null && cap2 != null && cap3 != null && accent != null && button != null && switchCompat != null && button_apply != null) {
                            cap1.setTextColor((Integer) animation.getAnimatedValue());
                            cap2.setTextColor((Integer) animation.getAnimatedValue());
                            cap3.setTextColor((Integer) animation.getAnimatedValue());
                            accent.setColorFilter((Integer) animation.getAnimatedValue());
                            button.setSupportBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));
                            switchCompat.setThumbTintList(getThumbColor((Integer) animation.getAnimatedValue()));
                            switchCompat.setTrackTintList(getTrackColor((Integer) animation.getAnimatedValue()));
                            //button_apply.setBackgroundColor((Integer) animation.getAnimatedValue());
                            button_apply.setBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));
                        }
                    }
                });
            }

        });
        animS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ThemeHolder.old_accentColor = ThemeHolder.accentColor;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animS.start();
        setResult(1506);
        sendBroadcast(new Intent(ACTION_THEME_CHANGE));
    }

    private int getToolbarTitleColor() {
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                return ((TextView) child).getCurrentTextColor();
            }
        }
        return ColorsRes.Blanco(this);
    }

    private ColorStateList getTrackColor(int color) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        int color10 = Color.argb(26, 0, 0, 0);
        int color30 = Color.argb(78, Color.red(color), Color.green(color), Color.blue(color));

        // Disabled state
        states[i] = new int[]{-android.R.attr.state_checked};
        colors[i] = color10;
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = color30;
        i++;

        // Default enabled state
        states[i] = new int[0];
        colors[i] = color30;
        i++;

        return new ColorStateList(states, colors);
    }

    private ColorStateList getThumbColor(int color) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        // Disabled state
        states[i] = new int[]{-android.R.attr.state_checked};
        colors[i] = Color.WHITE;
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = color;
        i++;

        // Default enabled state
        states[i] = new int[0];
        colors[i] = color;
        i++;

        return new ColorStateList(states, colors);
    }

    @Nullable
    private String getThemeName() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("curr_theme_name", null);
    }

    private void setThemeName(String name) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("curr_theme_name", name.replace(".aflvtheme", "")).apply();
    }

    private void save(final String name, final boolean send) {
        final File file = new File(Keys.Dirs.CACHE_THEMES, name + ".aflvtheme");
        Keys.Dirs.CACHE_THEMES.mkdirs();
        if (file.exists() && !send) {
            new MaterialDialog.Builder(this)
                    .content("El tema " + name + " ya existe, ¿desea sobreescribirlo?")
                    .positiveText("si")
                    .negativeText("cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            try {
                                JSONObject object = ThemeUtils.Theme.create(getA()).toJson();
                                object.put("name", name);
                                FileUtil.writeToFile(object.toString(), file);
                                setThemeName(name);
                                Toaster.toast("Tema guardado");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toaster.toast("Error al guardar tema!!!");
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            showSaveDialog(name);
                        }
                    }).build().show();
        } else {
            try {
                JSONObject object = ThemeUtils.Theme.create(getA()).toJson();
                object.put("name", name);
                FileUtil.writeToFile(object.toString(), file);
                setThemeName(name);
                if (!send) {
                    Toaster.toast("Tema guardado");
                } else {
                    Uri uri = new FileUtil(getA()).getUriForFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setDataAndType(uri, "application/aflvtheme");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        startActivity(Intent.createChooser(intent, "Enviar tema"));
                    } else {
                        new MaterialActivityChooserBuilder(this)
                                .withIntent(intent)
                                .withTitle("Enviar Tema")
                                .show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toaster.toast("Error al guardar tema!!!");
            }
        }
    }

    private void showSaveDialog(@Nullable String current) {
        showSaveDialog(current, false);
    }

    private void showSaveDialog(@Nullable String current, final boolean send) {
        new MaterialDialog.Builder(this)
                .title("Nombre")
                .titleGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .autoDismiss(false)
                .inputRange(3, 15)
                .input(null, current, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        if (charSequence.toString().matches("[a-zA-Z0-9 ]*") && charSequence.length() <= 15) {
                            save(charSequence.toString(), send);
                            materialDialog.dismiss();
                        } else {
                            Toaster.toast("El nombre debe tener menos de 15 caracteres y no tener signos");
                        }
                    }
                })
                .negativeText("cancelar")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                }).build().show();
    }

    public void showLoadThemesDialog() {
        final List<File> files = filterThemesFiles(Arrays.asList(Keys.Dirs.CACHE_THEMES.listFiles()));
        if (files.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title("Temas")
                    .titleGravity(GravityEnum.CENTER)
                    .items(getThemesNamesList(files))
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            MODE = MODE_PREVIEW;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switchCompat.setEnabled(false);
                                    button.setEnabled(false);
                                }
                            });
                            current = Uri.fromFile(new File(Keys.Dirs.CACHE_THEMES, files.get(i).getName()));
                            try {
                                JSONObject object = new JSONObject(FileUtil.getStringFromFile(getA(), current));
                                name = object.getString("name");
                                onColorPreview(object.getJSONArray("theme"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toaster.toast("Error al cargar tema!!!");
                                finish();
                            }
                            return true;
                        }
                    }).build().show();
        } else {
            Toaster.toast("No hay temas guardados");
        }
    }

    private List<File> filterThemesFiles(List<File> files) {
        List<File> filtered = new ArrayList<>();
        for (File file : files) {
            try {
                new JSONObject(FileUtil.getStringFromFile(file)).getString("name");
                filtered.add(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filtered;
    }

    private List<String> getThemesNamesList(List<File> files) {
        List<String> names = new ArrayList<>();
        for (File file : files) {
            try {
                JSONObject object = new JSONObject(FileUtil.getStringFromFile(file));
                names.add(object.getString("name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return names;
    }

    public void showPopup(View v) {
        if (MODE != MODE_PREVIEW && MODE != MODE_PREVIEW_FROM_FILE) {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_theme, popup.getMenu());
            File[] files = Keys.Dirs.CACHE_THEMES.listFiles();
            if (files == null || files.length == 0)
                popup.getMenu().removeItem(R.id.load);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.save:
                            showSaveDialog(getThemeName());
                            break;
                        case R.id.load:
                            showLoadThemesDialog();
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }
    }

    public void showDefaultToolbarPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_theme_toolbar, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.text:
                        setDefault("theme_toolbar_text");
                        break;
                    case R.id.color:
                        setDefault("theme_toolbar");
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void showToolbarPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_theme_toolbar, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.text:
                        showColorDialog("theme_toolbar_text", ColorType.CUSTOM);
                        break;
                    case R.id.color:
                        showColorDialog("theme_toolbar", ColorType.CUSTOM);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void showNavigationPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_theme_navigationr, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.color:
                        showColorDialog("theme_toolbar_navigation", ColorType.CUSTOM);
                        break;
                    case R.id.undo:
                        setDefault("theme_toolbar_navigation");
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void showDefaultCardPopup(final String key, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_theme_toolbar, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.text:
                        setDefault("theme_card_text");
                        break;
                    case R.id.color:
                        setDefault(key);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void showCardPopup(final String key, final boolean alpha, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_theme_toolbar, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.text:
                        showColorDialog("theme_card_text", ColorType.CUSTOM);
                        break;
                    case R.id.color:
                        showColorDialog(key, alpha ? ColorType.CUSTOM_ARGB : ColorType.CUSTOM);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        if (colorChooserDialog.tag().equals(ColorChooserDialog.TAG_ACCENT)) {
            ThemeHolder.old_accentColor = ThemeHolder.accentColor;
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("accentColor", i).apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("accentColor", i).apply();
            ThemeHolder.accentColor = i;
            onColorChange();
        } else {
            PreferenceManager.getDefaultSharedPreferences(getA()).edit().putInt(colorChooserDialog.tag(), i).apply();
            onColorChange();
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingHelper.track(this, TrackingHelper.THEME);
    }

    @Override
    public void onBackPressed() {
        if (MODE == MODE_PREVIEW_FROM_FILE) {
            super.onBackPressed();
        } else if (MODE == MODE_PREVIEW) {
            MODE = MODE_NORMAL;
            onColorChange();
            button_apply.hide();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(true);
                    switchCompat.setEnabled(true);
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    private enum ColorType {
        DEFAULT(0),
        CUSTOM(1),
        CUSTOM_ARGB(2);
        int value;

        ColorType(int value) {
            this.value = value;
        }
    }
}
