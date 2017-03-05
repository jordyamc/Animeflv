package knf.animeflv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import knf.animeflv.Utils.FileUtil;

@TargetApi(21)
public class PlayerSimple extends AppCompatActivity {
    VideoView videoView;
    RelativeLayout load;
    Intent intent;
    String ops;
    String url;
    int stopPosition=0;
    Map<String, String> options;
    Handler handler = new Handler();
    Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideSystemUI();
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position");
        }
        setContentView(R.layout.player_simple);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        context = this;
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        load = (RelativeLayout) findViewById(R.id.loading);
        if (load != null) load.bringToFront();
        videoView = (VideoView) findViewById(R.id.video_simple);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Error en video", Toast.LENGTH_SHORT).show();
                Log.d("Error:", String.valueOf(what));
                finish();
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                handler.postDelayed(runnable, 3000);
                load.setVisibility(View.GONE);
                mp.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    String file = intent.getStringExtra("file");
                    if (!file.trim().equals("")) {
                        String start = file.substring(0, file.lastIndexOf("/"));
                        String eid = file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(".mp4"));
                        String[] semi = eid.split("_");
                        String aid = semi[0];
                        int num = Integer.parseInt(semi[1]);
                        File nextCap = new File(start, aid + "_" + String.valueOf(num + 1) + ".mp4");
                        Log.e("NextCap", nextCap.getAbsolutePath());
                        if (nextCap.exists()) {
                            Intent i = new Intent(PlayerSimple.this, PlayerSimple.class);
                            i.putExtra("file", nextCap.getAbsolutePath());
                            i.putExtra("title", new Parser().getTitCached(aid) + " - " + (num + 1));
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            FileUtil.init(PlayerSimple.this).setSeenState(aid + "_" + (num + 1) + "E", true);
                            finish();
                            startActivity(i);
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    finish();
                }
            }
        });
        intent = getIntent();
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
            videoView.setVideoURI(Uri.parse(url), options);
        } else {
            url = intent.getStringExtra("url");
            if (url != null) {
                Log.d("Stream", url);
                videoView.setVideoURI(Uri.parse(url));
            } else {
                String file = intent.getStringExtra("file");
                if (file != null) {
                    videoView.setVideoPath(file);
                } else {
                    Log.d("Exo Player", "No URL, No File");
                    Toast.makeText(this, "No URL, NO FILE", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView!=null){
            videoView.start();
            videoView.seekTo(stopPosition);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
        outState.putInt("position", stopPosition);
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
