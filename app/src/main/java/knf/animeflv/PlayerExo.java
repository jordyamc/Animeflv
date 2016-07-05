package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.chunk.Chunk;
import com.google.android.exoplayer.chunk.ChunkOperationHolder;
import com.google.android.exoplayer.chunk.ChunkSource;
import com.google.android.exoplayer.chunk.MediaChunk;
import com.google.android.exoplayer.dash.DashChunkSource;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.upstream.UriDataSource;
import com.google.android.exoplayer.util.PlayerControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jordy on 29/06/2016.
 */

public class PlayerExo extends AppCompatActivity implements ExoPlayer.Listener,SurfaceHolder.Callback {
    @Bind(R.id.surface_view)SurfaceView surfaceView;
    @Bind(R.id.root)View root;
    private ExoPlayer player;
    private Intent intent;
    private String ops;
    private String url;
    private HashMap<String,String> options;
    private PlayerControl playerControl;
    private MediaController mediaController;
    private boolean prepared=false;

    private String user_agent="Mozilla/5.0(Linux; U; Android 2.2; en-gb; LG-P500 Build/FRF91) AppleWebKit/533.0 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_exo);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event);
            }
        });
        mediaController = new KeyCompatibleMediaController(this);
        mediaController.setAnchorView(root);
        player = ExoPlayer.Factory.newInstance(2, ExoPlayer.Factory.DEFAULT_MIN_BUFFER_MS, ExoPlayer.Factory.DEFAULT_MIN_REBUFFER_MS);
        Allocator allocator = new DefaultAllocator(ExoPlayer.Factory.DEFAULT_MIN_BUFFER_MS);
        DataSource dataSource = new DefaultHttpDataSource(user_agent,null);
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
        } else {
            url = intent.getStringExtra("url");
            if (url != null) {
                Log.d("Stream", url);
            } else {
                String file = intent.getStringExtra("file");
                if (file == null) {
                    Log.d("Exo Player", "No URL, No File");
                    Toast.makeText(this, "No URL, NO FILE", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                Uri.parse(url), dataSource, allocator, 500,new Mp4Extractor());
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
                this, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                sampleSource, MediaCodecSelector.DEFAULT);
        player.prepare(videoRenderer, audioRenderer);
        surfaceView.getHolder().addCallback(this);
        player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceView.getHolder().getSurface());
        playerControl = new PlayerControl(player);
        mediaController.setMediaPlayer(playerControl);
        mediaController.setEnabled(true);
        player.addListener(this);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        Log.d("ExoPlayer State",text);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void toggleControlsVisibility()  {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show(0);
    }

    private static final class KeyCompatibleMediaController extends MediaController {

        private MediaController.MediaPlayerControl playerControl;

        public KeyCompatibleMediaController(Context context) {
            super(context);
        }

        @Override
        public void setMediaPlayer(MediaController.MediaPlayerControl playerControl) {
            super.setMediaPlayer(playerControl);
            this.playerControl = playerControl;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
                    show();
                }
                return true;
            } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                    || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                    show();
                }
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }
}
