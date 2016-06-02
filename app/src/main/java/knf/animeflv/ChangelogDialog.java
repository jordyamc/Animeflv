package knf.animeflv;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 04/09/2015.
 */
public class ChangelogDialog extends DialogFragment {

    public static ChangelogDialog create() {
        ChangelogDialog dialog = new ChangelogDialog();
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_webview, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("ChangeLog")
                .customView(customView, false)
                .positiveText(android.R.string.ok)
                .backgroundColor(ThemeUtils.isAmoled(getActivity()) ? ColorsRes.Prim(getActivity()) : ColorsRes.Blanco(getActivity()))
                .build();

        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getActivity().getAssets().open("log.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();
            //webView.loadData(buf.toString(), "text/html", "UTF-8");
            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.loadUrl("file:///android_asset/log.html");
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        return dialog;
    }
}
