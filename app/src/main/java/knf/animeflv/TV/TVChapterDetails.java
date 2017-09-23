package knf.animeflv.TV;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.zippy.zippyHelper;
import xdroid.toaster.Toaster;

public class TVChapterDetails extends AestheticActivity {
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.chapter)
    TextView chapter;

    @BindView(R.id.info)
    Button info;
    @BindView(R.id.stream)
    Button stream;
    @BindView(R.id.download)
    Button download;

    @BindView(R.id.details)
    CardView details;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_chap_info);
        ButterKnife.bind(this);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        if (Aesthetic.isFirstTime())
            Aesthetic.get()
                    .isDark(theme.isDark)
                    .colorWindowBackgroundRes(android.R.color.transparent)
                    .textColorPrimaryRes(theme.isDark ? android.R.color.primary_text_dark : android.R.color.primary_text_light)
                    .textColorPrimaryInverseRes(theme.isDark ? android.R.color.primary_text_light : android.R.color.primary_text_dark)
                    .textColorSecondaryRes(theme.isDark ? android.R.color.secondary_text_dark : android.R.color.secondary_text_light)
                    .textColorSecondaryInverseRes(theme.isDark ? android.R.color.secondary_text_light : android.R.color.secondary_text_dark)
                    .colorAccent(theme.accent).apply();
        details.setCardBackgroundColor(theme.accent);
        new CacheManager().portada(this, getIntent().getStringExtra("aid"), img);
        title.setText(getIntent().getStringExtra("title"));
        chapter.setText("Capítulo " + getIntent().getStringExtra("chapter"));
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TVChapterDetails.this, TVAnimeInfo.class));
            }
        });
    }

    public void onDownload(final View view) {
        final String eid = getIntent().getStringExtra("aid") + "_" + getIntent().getStringExtra("chapter") + "E";
        DownloadGetter.search(this, eid, new DownloadGetter.ActionsInterfaceDeep() {
            @Override
            public boolean isStream() {
                return view.getId() == R.id.stream;
            }

            @Override
            public void onStartDownload() {
                finish();
            }

            @Override
            public void onStartZippy(final String url) {
                zippyHelper.calculate(url, new zippyHelper.OnZippyResult() {
                    @Override
                    public void onSuccess(zippyHelper.zippyObject object) {
                        ManageDownload.chooseDownDir(TVChapterDetails.this, eid, object.download_url, object.cookieConstructor);
                        finish();
                    }

                    @Override
                    public void onError() {
                        Toaster.toast("Error al obtener link, reintentando en modo nativo");
                        setUpWeb(eid, url, view.getId() == R.id.stream);
                    }
                });
            }

            @Override
            public void onCancelDownload() {

            }

            @Override
            public void onStartCasting() {

            }

            @Override
            public void onLogError(Exception e) {
                Log.e("TVDetails", e.getMessage());
            }
        });
    }

    private void setUpWeb(final String eid, final String orgurl, final boolean isStreaming) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final WebView web = new WebView(TVChapterDetails.this);
                web.getSettings().setJavaScriptEnabled(true);
                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (!url.contains("jordyamc"))
                            Log.e("URL Loaded", url);
                        if (url.contains("zippyshare.com") || url.contains("blank")) {
                            web.loadUrl("javascript:(" +
                                    "function(){" +
                                    "var down=document.getElementById('dlbutton').href;" +
                                    "location.replace(down);" +
                                    "})()");
                        }
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                web.setDownloadListener(new DownloadListener() {
                    public void onDownloadStart(String url, String userAgent,
                                                String contentDisposition, String mimetype,
                                                long contentLength) {
                        Log.e("Start Download", url);
                        String fileName = url.substring(url.lastIndexOf("/") + 1);
                        String eid = fileName.replace(".mp4", "") + "E";
                        if (!isStreaming) {
                            if (!FileUtil.init(TVChapterDetails.this).ExistAnime(eid)) {
                                CookieManager cookieManager = CookieManager.getInstance();
                                String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                                CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), orgurl);
                                ManageDownload.chooseDownDir(TVChapterDetails.this, eid, url, constructor);
                                web.loadUrl("about:blank");
                            } else {
                                web.loadUrl("about:blank");
                            }
                        }
                        if (isStreaming) {
                            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(TVChapterDetails.this).getString("t_streaming", "0"));
                            CookieManager cookieManager = CookieManager.getInstance();
                            String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                            CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), orgurl);
                            web.loadUrl("about:blank");
                            if (type == 1) {
                                StreamManager.mx(TVChapterDetails.this).Stream(eid, url, constructor);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    StreamManager.internal(TVChapterDetails.this).Stream(eid, url, constructor);
                                } else {
                                    if (FileUtil.init(TVChapterDetails.this).isMXinstalled()) {
                                        Toaster.toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                        StreamManager.mx(TVChapterDetails.this).Stream(eid, url, constructor);
                                    } else {
                                        Toaster.toast("No hay reproductor adecuado disponible");
                                    }
                                }
                            }
                        }
                        finish();
                    }
                });
                web.loadUrl(orgurl);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
