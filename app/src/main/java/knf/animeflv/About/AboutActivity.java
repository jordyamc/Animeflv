package knf.animeflv.About;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.R.id;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import knf.animeflv.Changelog.ChangelogActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class AboutActivity extends MaterialAboutActivity {

    private boolean antispam = false;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            antispam = false;
            Log.e("AntiSpam", "Disabled");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (ThemeUtils.isAmoled(this)) {
            setTheme(R.style.AboutDark);
        } else {
            setTheme(R.style.AboutLight);
        }
        super.onCreate(savedInstanceState);
        Toolbar toolbar = ((Toolbar) findViewById(id.mal_toolbar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ThemeUtils.isAmoled(this))
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
    }

    @Override
    protected MaterialAboutList getMaterialAboutList(final Context context) {

        return new MaterialAboutList.Builder()
                .addCard(getInfoBuilder(context).build())
                .addCard(getAuthorBuilder(context).build())
                .addCard(getContributorsBuilder(context).build())
                .addCard(getBetaBuilder(context).build())
                .addCard(getAppBuilder(context).build())
                .addCard(getContactBuilder(context).build())
                .build();
    }

    private MaterialAboutCard.Builder getInfoBuilder(final Context context) {
        MaterialAboutCard.Builder info = new MaterialAboutCard.Builder();
        info.addItem(
                new MaterialAboutTitleItem.Builder()
                        .text("AnimeFlvApp")
                        .icon(ThemeUtils.isAmoled(context) ? R.mipmap.ic_launcher_dark : R.mipmap.ic_launcher)
                        .build()
        );
        try {
            info.addItem(ConvenienceBuilder.createVersionActionItem(context,
                    new IconicsDrawable(context)
                            .icon(GoogleMaterial.Icon.gmd_info_outline)
                            .color(iconColor(context))
                            .sizeDp(18),
                    "Versión",
                    true));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        info.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Changelog")
                        .icon(new IconicsDrawable(context)
                                .icon(CommunityMaterial.Icon.cmd_history)
                                .color(iconColor(context))
                                .sizeDp(18))
                        .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(context, ChangelogActivity.class));
                            }
                        })
                        .build());
        return info;
    }

    private MaterialAboutCard.Builder getAuthorBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Autor");
        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Jordy Mendoza")
                        .subText("México")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(GoogleMaterial.Icon.gmd_person)
                                        .color(iconColor(context))
                                        .sizeDp(18)

                        )
                        .build()
        );

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Telegram")
                .subText("@UnbarredStream")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_telegram)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/UnbarredStream")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Proyecto en GitHub")
                .subText("@jordyamc")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://github.com/jordyamc/Animeflv")))
                .build());
        return builder;
    }

    private MaterialAboutCard.Builder getContributorsBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Contribuidores");

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Bowen_gfla")
                .subText("Buscador de imagenes")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .build());

        return builder;
    }

    private MaterialAboutCard.Builder getBetaBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Beta Testers");

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Guerra1337")
                .subText("Miembro super activo")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/guerra1337")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Oaxaca")
                .subText("Miembro super activo")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (!antispam) {
                            antispam = true;
                            handler.postDelayed(runnable, 10000);
                            Toaster.toastLong("Un Doujishin de nicoxmaki ntr podria invocarlo");
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Oaxacasempai")));
                        } else {
                            Log.e("AntiSpam", "Enabled");
                        }
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Richi Kurono (azu-nya)")
                .subText("Miembro super activo")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/Richikurono")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Tatsuya Shiba")
                .subText("Miembro activo")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/Onii_sama")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Sam")
                .subText("Miembro activo")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Toaster.toastLong("El ntr lo mato");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Sam_7u7")));
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Ryuk")
                .subText("Miembro regular")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/Naturedead")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Heiden")
                .subText("Miembro poco regular")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Toaster.toastLong("The dick only makes it better");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/CarajoEstoEsDificil")));
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Macd")
                .subText("Miembro poco regular")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/Macd89")))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Chico")
                .subText("Miembro muy poco regular")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Toaster.toastLong("Las lolis son vida las lolis son amor");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Chico_loli")));
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("INF3R")
                .subText("Miembro muy poco regular")
                .icon(new IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(iconColor(context))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/INF3R")))
                .build());

        return builder;
    }

    private MaterialAboutCard.Builder getAppBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Sitios");

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Facebook")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_facebook_box)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, facebookUri()))
                        .build()
        );

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Discord")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_discord)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://discordapp.com/invite/6hzpua6")))
                        .build()
        );

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Página Web")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_web)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://jordyamc.github.io/Animeflv/")))
                        .build()
        );

        return builder;
    }

    private MaterialAboutCard.Builder getContactBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Contacto");

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Correo")
                        .subText("animeflvapp@hotmail.com")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_email)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(context, "animeflvapp@hotmail.com", "Contacto - AnimeflvApp", "Eviar correo"))
                        .build()
        );

        return builder;
    }

    private Uri facebookUri() {
        String facebookUrl = "https://www.facebook.com/animeflv.app.jordy";
        Uri uri;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(facebookUrl);
        }
        return uri;
    }

    @ColorInt
    private int iconColor(Context context) {
        return ThemeUtils.isAmoled(context) ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d");
    }

    @Override
    protected CharSequence getActivityTitle() {
        return "Sobre la app";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
