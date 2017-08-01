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
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.util.Colors;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.support.SupportActivity;

import java.util.List;

import knf.animeflv.Changelog.ChangelogActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSyncro;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.State.StateActivity;
import knf.animeflv.Utils.OnlineDataHelper;
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
                .addCard(getAlphaBuilder(context).build())
                .addCard(getBetaBuilder(context).build())
                .addCard(getAppBuilder(context).build())
                .addCard(getContactBuilder(context).build())
                .addCard(getBetaTestingBuilder(context).build())
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
        info.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Estado")
                        .icon(new IconicsDrawable(context)
                                .icon(CommunityMaterial.Icon.cmd_alert)
                                .color(iconColor(context))
                                .sizeDp(18))
                        .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(context, StateActivity.class));
                            }
                        })
                        .build());
        info.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Donar")
                        .icon(new IconicsDrawable(context)
                                .icon(CommunityMaterial.Icon.cmd_gift)
                                .color(iconColor(context))
                                .sizeDp(18))
                        .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(context, DonationActivity.class));
                            }
                        })
                        .build());
        info.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Librerias")
                        .icon(new IconicsDrawable(context)
                                .icon(CommunityMaterial.Icon.cmd_library_books)
                                .color(iconColor(context))
                                .sizeDp(18))
                        .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                            @Override
                            public void onClick() {
                                ThemeUtils.Theme theme = ThemeUtils.Theme.create(AboutActivity.this);
                                new LibsBuilder()
                                        .withActivityStyle(ThemeUtils.isAmoled(AboutActivity.this) ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .withActivityTitle("Librerias")
                                        .withActivityColor(new Colors(theme.primary, theme.primary))
                                        .start(AboutActivity.this);
                            }
                        })
                        .build());
        info.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Ayuda")
                        .icon(new IconicsDrawable(context)
                                .icon(CommunityMaterial.Icon.cmd_help)
                                .color(iconColor(context))
                                .sizeDp(18))
                        .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                            @Override
                            public void onClick() {
                                ZendeskConfig.INSTANCE.init(context,
                                        context.getString(R.string.com_zendesk_sdk_url),
                                        context.getString(R.string.com_zendesk_sdk_identifier),
                                        context.getString(R.string.com_zendesk_sdk_clientIdentifier));

                                ZendeskConfig.INSTANCE.setIdentity(
                                        new AnonymousIdentity.Builder()
                                                .withNameIdentifier("User")
                                                .withEmailIdentifier(FavSyncro.getEmailHelp(context))
                                                .build()
                                );

                                new SupportActivity.Builder().show(context);
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

        addContributors(builder);

        return builder;
    }

    private MaterialAboutCard.Builder getAlphaBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Alpha Testers");

        addAlphas(builder);

        return builder;
    }

    private MaterialAboutCard.Builder getBetaBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Beta Testers");

        addBetas(builder);

        return builder;
    }

    private MaterialAboutCard.Builder getAppBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Sitios");

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Facebook")
                        .subText("/animeflv.app.jordy")
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
                        .subText("@UnbarredStream")
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
                        .subText(Parser.getNormalUrl(context))
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_web)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse(Parser.getNormalUrl(context))))
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

    private MaterialAboutCard.Builder getBetaTestingBuilder(final Context context) {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("Beta Testing");

        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Beta Tester")
                        .subText("https://betas.to/hQdpp7RG")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_beta)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://betas.to/hQdpp7RG")))
                        .build()
        );
        builder.addItem(
                new MaterialAboutActionItem.Builder()
                        .text("Grupo Beta")
                        .subText("Telegram")
                        .icon(
                                new IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_telegram)
                                        .color(iconColor(context))
                                        .sizeDp(18)
                        )
                        .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("https://t.me/joinchat/AAAAAEKOzGU7YaW4Pc14_Q")))
                        .build()
        );
        return builder;
    }

    private void addContributors(MaterialAboutCard.Builder builder) {
        List<OnlineDataHelper.PersonItem> items = OnlineDataHelper.get(this).getPersons(OnlineDataHelper.TYPE_CONTRIBUTOR);
        for (OnlineDataHelper.PersonItem item : items) {
            if (item.haveMessage > 0) {
                addMessage(item, builder, OnlineDataHelper.TYPE_CONTRIBUTOR);
            } else {
                addNoMessage(item, builder, OnlineDataHelper.TYPE_CONTRIBUTOR);
            }
        }
    }

    private void addAlphas(MaterialAboutCard.Builder builder) {
        List<OnlineDataHelper.PersonItem> items = OnlineDataHelper.get(this).getPersons(OnlineDataHelper.TYPE_ALPHA);
        for (OnlineDataHelper.PersonItem item : items) {
            if (item.haveMessage > 0) {
                addMessage(item, builder, OnlineDataHelper.TYPE_ALPHA);
            } else {
                addNoMessage(item, builder, OnlineDataHelper.TYPE_ALPHA);
            }
        }
    }

    private void addBetas(MaterialAboutCard.Builder builder) {
        List<OnlineDataHelper.PersonItem> items = OnlineDataHelper.get(this).getPersons(OnlineDataHelper.TYPE_BETA);
        for (OnlineDataHelper.PersonItem item : items) {
            if (item.haveMessage > 0) {
                addMessage(item, builder, OnlineDataHelper.TYPE_BETA);
            } else {
                addNoMessage(item, builder, OnlineDataHelper.TYPE_BETA);
            }
        }
    }

    private void addMessage(final OnlineDataHelper.PersonItem item, MaterialAboutCard.Builder builder, int type) {
        MaterialAboutActionItem.Builder action =
                new MaterialAboutActionItem.Builder()
                        .text(item.name)
                        .subText(item.description)
                        .icon(new IconicsDrawable(this)
                                .icon(type == 1 ? CommunityMaterial.Icon.cmd_account_star : GoogleMaterial.Icon.gmd_person)
                                .color(iconColor(this))
                                .sizeDp(18)
                        );
        action.setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
            @Override
            public void onClick() {
                Log.e("Beta", "Onclick");
                Toaster.toastLong(item.message);
                if (!item.alias.trim().equals(""))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + item.alias)));
            }
        });
        builder.addItem(action.build());
    }

    private void addNoMessage(final OnlineDataHelper.PersonItem item, MaterialAboutCard.Builder builder, int type) {
        MaterialAboutActionItem.Builder action =
                new MaterialAboutActionItem.Builder()
                        .text(item.name)
                        .subText(item.description)
                        .icon(new IconicsDrawable(this)
                                .icon(type == 1 ? CommunityMaterial.Icon.cmd_account_star : GoogleMaterial.Icon.gmd_person)
                                .color(iconColor(this))
                                .sizeDp(18)
                        );
        if (!item.alias.trim().equals(""))
            action.setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(this, Uri.parse("https://t.me/" + item.alias)));
        builder.addItem(action.build());
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
