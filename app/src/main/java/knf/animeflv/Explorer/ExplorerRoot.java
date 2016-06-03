package knf.animeflv.Explorer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Explorer.Fragments.DirectoryFragment;
import knf.animeflv.Explorer.Fragments.VideoFilesFragment;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;


public class ExplorerRoot extends AppCompatActivity implements ExplorerInterfaces {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fileDir)
    TextView textView;
    private boolean isDirectory = true;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_root);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (ThemeUtils.isAmoled(this)) {
            textView.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            textView.setTextColor(ColorsRes.Holo_Dark(this));
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Explorador");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
        replaceFragment(new DirectoryFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.root, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (!isDirectory) {
            getSupportActionBar().setTitle("Explorador");
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            //replaceFragment(new DirectoryFragment());
            getSupportFragmentManager().popBackStack();
            isDirectory = true;
        } else {
            finish();
        }
    }

    @Override
    public void OnDirectoryClicked(File file, String name) {
        isDirectory = false;
        textView.setText(file.getAbsolutePath());
        getSupportActionBar().setTitle(name);
        VideoFilesFragment fragment = new VideoFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", file.getAbsolutePath());
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    @Override
    public void OnFileClicked(File file) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }
}
