package knf.animeflv.SDControl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.WindowManager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import java.io.OutputStream;

import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.eNums.SDStatus;
import xdroid.toaster.Toaster;

public class SDManager extends IntroActivity {
    public static final int REQUEST_CODE = 88775;
    private int r = -1;

    private SDSearcher searcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setSkipEnabled(false);
        setFinishEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, getBackColor()));
        }
        searcher = new SDSearcher();
        addSlide(new FragmentSlide.Builder()
                .fragment(searcher)
                .background(getBackColor())
                .backgroundDark(getBackColor())
                .build());
        setCustomResult(FileUtil.haveSDPermission(this) ? SDSearcher.SD_SELECTED : SDSearcher.SD_NO_SELECTED);
    }

    @ColorRes
    private int getBackColor() {
        return ThemeUtils.isAmoled(this) ? R.color.prim : R.color.nmain;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDSearcher.GRANT_WRITE_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            if (havePermission(data)) {
                if (FileUtil.checkIfSDCardRoot(data.getData()) == SDStatus.OK) {
                    Log.d("Permission granted", "OK");
                    setCustomResult(SDSearcher.SD_SELECTED);
                    searcher.resetResponse();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(SDManager.this).edit().putString("SDPath", "null").commit();
                    Toaster.toast(FileUtil.checkIfSDCardRoot(data.getData()).getErrorMessage());
                }
            } else {
                PreferenceManager.getDefaultSharedPreferences(SDManager.this).edit().putString("SDPath", "null").commit();
                Toaster.toast("Error al obtener permisos");
            }
        }
    }

    public void setCustomResult(int result) {
        SDResultContainer.setResult(result);
    }


    @TargetApi(19)
    private boolean havePermission(Intent data) {
        try {
            Uri treeUri = data.getData();
            final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, treeUri.toString()).apply();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            DocumentFile newFile = pickedDir.createFile("text/plain", "Prueba");
            OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
            out.write("Prueba".getBytes());
            out.close();
            newFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
