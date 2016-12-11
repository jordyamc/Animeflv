package knf.animeflv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import knf.animeflv.Utils.FileUtil;
import knf.animeflv.playerSources.ResizeSurfaceView;
import knf.animeflv.playerSources.VideoControllerView;


public class Player extends AppCompatActivity implements VideoControllerView.MediaPlayerControlListener, SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    View decorView;
    Intent intent;
    String url;
    String ops;
    String TAG;
    VideoControllerView controller;
    ResizeSurfaceView mVideoSurface;
    private HashMap<String, String> options;
    private MediaPlayer mMediaPlayer;
    private int mVideoWidth;
    private int mVideoHeight;
    private View mContentView;
    private View mLoadingView;

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    @TargetApi(21)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "PLAYER INT";
        setContentView(R.layout.player);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mVideoSurface = (ResizeSurfaceView) findViewById(R.id.videoSurface);
        mContentView = findViewById(R.id.video_container);
        mLoadingView = findViewById(R.id.loading);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLoadingView.setVisibility(View.VISIBLE);
        decorView = getWindow().getDecorView();
        hideSystemUI();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        intent = getIntent();
        setMediaPlayer();
    }

    private void setMediaPlayer() {
        try {
            controller = new VideoControllerView(this);
            SurfaceHolder videoHolder = mVideoSurface.getHolder();
            videoHolder.addCallback(this);
            mVideoSurface.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    controller.toggleContollerView();
                    return false;
                }
            });
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            ops = intent.getStringExtra("ops");
            if (ops != null) {
                url = intent.getStringExtra("url");
                options = new HashMap<>();
                String[] conf = ops.split(";;;");
                for (String split : conf) {
                    String[] temp = split.split(":::");
                    options.put(temp[0], temp[1]);
                }
                mMediaPlayer.setDataSource(this, Uri.parse(url), options);
            } else {
                url = intent.getStringExtra("url");
                if (url != null) {
                    mMediaPlayer.setDataSource(this, Uri.parse(url));
                } else {
                    String file = intent.getStringExtra("file");
                    if (file != null) {
                        mMediaPlayer.setDataSource(file);
                    } else {
                        Log.d(TAG, "No URL, No File");
                        Toast.makeText(this, "No URL, NO FILE", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        controller.show();
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            mVideoSurface.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            mVideoSurface.adjustSize(getDeviceWidth(this), getDeviceHeight(this), mVideoSurface.getWidth(), mVideoSurface.getHeight());
    }

    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        resetPlayer();
    }
// End SurfaceHolder.Callback


    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //setup video controller view
        controller.setMediaPlayerControlListener(this);
        mLoadingView.setVisibility(View.GONE);
        mVideoSurface.setVisibility(View.VISIBLE);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        controller.setGestureListener(this);
        mMediaPlayer.start();
    }
// End MediaPlayer.OnPreparedListener

    /**
     * Implement VideoMediaController.MediaPlayerControl
     */
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekProgress() {
        return true;
    }


    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (null != mMediaPlayer)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if (null != mMediaPlayer)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        if (null != mMediaPlayer)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public void pause() {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void seekTo(int i) {
        if (null != mMediaPlayer) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public void start() {
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
        }
    }

    @Override
    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    @Override
    public void toggleFullScreen() {
        if (isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void exit() {
        resetPlayer();
        finish();
    }

    @Override
    public String getTopTitle() {
        return intent.getStringExtra("title");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        resetPlayer();
        String file = intent.getStringExtra("file");
        String start = file.substring(0, file.lastIndexOf("/"));
        String eid = file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(".mp4"));
        String[] semi = eid.split("_");
        String aid = semi[0];
        int num = Integer.parseInt(semi[1]);
        if (!file.trim().equals("")) {
            File nextCap = new File(start, aid + "_" + String.valueOf(num + 1) + ".mp4");
            Log.e("NextCap", nextCap.getAbsolutePath());
            if (nextCap.exists()) {
                Intent i = new Intent(this, Player.class);
                i.putExtra("file", nextCap.getAbsolutePath());
                i.putExtra("title", new Parser().getTitCached(aid) + " - " + (num + 1));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                FileUtil.init(this).setSeenState(aid + "_" + (num + 1) + "E", true);
                finish();
                startActivity(i);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
