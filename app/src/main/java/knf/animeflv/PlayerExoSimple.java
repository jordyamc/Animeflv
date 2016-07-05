package knf.animeflv;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.playerSources.EMVideoViewSpecial;
import knf.animeflv.playerSources.VideoControlsEsp;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 29/06/2016.
 */

public class PlayerExoSimple extends AppCompatActivity implements OnPreparedListener,OnErrorListener,OnCompletionListener{
    @Bind(R.id.video_view)EMVideoView videoView;
    Intent intent;
    String ops;
    String url;
    HashMap<String,String> options;
    VideoControlsEsp controls;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_player_simple);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        intent = getIntent();
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);
        controls = new VideoControlsEsp(this);
        videoView.setControls(controls);
        controls.setTitle(intent.getStringExtra("title"));
        controls.setOnFinishListener(new VideoControlsEsp.Finish() {
            @Override
            public void onArrowPressed() {
                finish();
            }
        });
        controls.setVideoView(videoView);
        ops = intent.getStringExtra("ops");
        if (ops != null) {
            url = intent.getStringExtra("url");
            options = new HashMap<>();
            String[] conf = ops.split(";;;");
            for (String split : conf) {
                String[] temp = split.split(":::");
                options.put(temp[0], temp[1]);
            }
            Log.d("Stream w ops", url);
        } else {
            url = intent.getStringExtra("url");
            if (url != null) {
                Log.d("Stream", url);
                videoView.setVideoURI(Uri.parse(url));
            } else {
                String file = intent.getStringExtra("file");
                if (file == null) {
                    Log.d("Exo Player", "No URL, No File");
                    Toast.makeText(this, "No URL, NO FILE", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    videoView.setVideoPath(file);
                }
            }
        }

    }

    @Override
    public void onPrepared() {
        videoView.start();
    }

    @Override
    public boolean onError() {
        Toaster.toast("Error en video!!!");
        onCompletion();
        return false;
    }

    @Override
    public void onCompletion() {
        if (videoView!=null){
            videoView.release();
            videoView=null;
        }
        finish();
    }
}
