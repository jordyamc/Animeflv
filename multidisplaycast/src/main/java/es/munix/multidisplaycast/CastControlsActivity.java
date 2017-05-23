package es.munix.multidisplaycast;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.munix.utilities.Views;

import es.munix.multidisplaycast.interfaces.CastListener;
import es.munix.multidisplaycast.interfaces.PlayStatusListener;
import es.munix.multidisplaycast.model.MediaObject;
import es.munix.multidisplaycast.utils.Format;

/**
 * Created by munix on 3/11/16.
 */

public class CastControlsActivity extends AppCompatActivity implements CastListener, PlayStatusListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView streamPositionTextView;
    private TextView streamDurationTextView;
    private ImageView pictureImageView;
    private View loader;
    private View fadeBar;
    private View positionLayer;
    private View stop;
    private View prev;
    private View next;
    private ImageView play;
    private View volume;
    private View volumeLayer;
    private SeekBar volumeBarControl;
    private SeekBar streamSeekBar;
    private MediaObject mediaObject;
    private Boolean isSeeking = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_controls);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void setViews() {
        titleTextView = (TextView) findViewById(R.id.movie_title);
        subtitleTextView = (TextView) findViewById(R.id.movie_subtitle);
        streamPositionTextView = (TextView) findViewById(R.id.stream_position);
        streamDurationTextView = (TextView) findViewById(R.id.stream_duration);
        pictureImageView = (ImageView) findViewById(R.id.movie_picture);
        loader = findViewById(R.id.loader);
        positionLayer = findViewById(R.id.positionLayer);
        fadeBar = findViewById(R.id.fadeBar);

        stop = findViewById(R.id.stop);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        play = (ImageView) findViewById(R.id.play);
        volume = findViewById(R.id.volume);
        volumeLayer = findViewById(R.id.volumeLayer);
        volumeBarControl = (SeekBar) findViewById(R.id.volumeControl);
        streamSeekBar = (SeekBar) findViewById(R.id.stream_seek_bar);

        streamSeekBar.setOnSeekBarChangeListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        volume.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CastManager.getInstance()
                .getMediaObject() == null || TextUtils.isEmpty(CastManager.getInstance()
                .getMediaObject()
                .getTitle())) {
            finish();
        } else {
            setViews();
            paintInterface();
        }
    }

    private void paintInterface() {
        mediaObject = CastManager.getInstance().getMediaObject();
        if (mediaObject == null) {
            finish();
        }
        titleTextView.setText(mediaObject.getTitle());
        subtitleTextView.setText(mediaObject.getSubtitle());
        Glide.with(this).load(mediaObject.getImage()).into(pictureImageView);
        if (!mediaObject.getIsSeekable()) {
            positionLayer.setVisibility(View.GONE);
            streamSeekBar.setVisibility(View.GONE);
        }

        if (!mediaObject.getCanChangeVolume()) {
            volume.setOnClickListener(null);
            volume.setClickable(false);
            volume.setBackgroundResource(R.drawable.shape_buttons_disabled);
        } else {
            volumeBarControl.setProgress(mediaObject.getCurrentVolume());
            volumeBarControl.setOnSeekBarChangeListener(this);
        }

        if (mediaObject.getCanFastForwart()) {
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        } else {
            prev.setOnClickListener(null);
            next.setOnClickListener(null);
        }
    }

    public void hideSeekBar() {
        if (positionLayer != null) {
            positionLayer.setVisibility(View.GONE);
        }

        if (streamSeekBar != null) {
            streamSeekBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CastManager.getInstance().setPlayStatusListener(getClass().getSimpleName(), this);
        CastManager.getInstance().setCastListener(getClass().getSimpleName(), this);
    }

    @Override
    protected void onStop() {
        CastManager.getInstance().unsetCastListener(getClass().getSimpleName());
        CastManager.getInstance().unsetPlayStatusListener(getClass().getSimpleName());
        super.onStop();
    }

    @Override
    public void isConnected() {

    }

    @Override
    public void isDisconnected() {
        finish();
    }

    @Override
    public void onPlayStatusChanged(int playStatus) {
        switch (playStatus) {
            case STATUS_PLAYING:
                if (loader.getVisibility() == View.VISIBLE) {
                    Views.disappear(loader, 300);
                }

                break;

            case STATUS_RESUME_PAUSE:
                play.setImageResource(R.drawable.ic_pause_white_36dp);
                break;

            case STATUS_FINISHED:
            case STATUS_STOPPED:
                finish();
                break;

            case STATUS_PAUSED:
                play.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                break;

            case STATUS_NOT_SUPPORT_LISTENER:
                if (loader.getVisibility() == View.VISIBLE) {
                    Views.disappear(loader, 300);
                }
                break;
        }
    }

    @Override
    public void onPositionChanged(long currentPosition) {
        if (!isSeeking) {
            streamPositionTextView.setText(Format.time(currentPosition));
            streamSeekBar.setProgress((int) currentPosition);
        }
    }

    @Override
    public void onTotalDurationObtained(long totalDuration) {
        streamSeekBar.setMax((int) totalDuration);
        if (!isSeeking) {
            streamDurationTextView.setText(Format.time(totalDuration));
        }
    }

    @Override
    public void onSuccessSeek() {
        isSeeking = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.getId() == R.id.stream_seek_bar) {
            streamPositionTextView.setText(Format.time(i));
        } else {
            float volume = (float) seekBar.getProgress() / 100.0f;
            CastManager.getInstance().setVolume(volume);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.stream_seek_bar) {
            isSeeking = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.stream_seek_bar) {
            CastManager.getInstance().seekTo(seekBar.getProgress());
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (volumeLayer != null) {
                        Views.disappear(volumeLayer, 300);
                        Views.disappear(fadeBar, 300);
                    }
                }
            }, 1000);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.stop) {
            CastManager.getInstance().stop();
        } else if (id == R.id.prev) {
            CastManager.getInstance().rewind();
        } else if (id == R.id.next) {
            CastManager.getInstance().fastForward();
        } else if (id == R.id.play) {
            CastManager.getInstance().togglePause();
        } else if (id == R.id.volume) {
            if (volumeLayer.getVisibility() != View.VISIBLE) {
                Views.appear(volumeLayer, 300);
                Views.appear(fadeBar, 300);
            } else {
                Views.disappear(volumeLayer, 300);
                Views.disappear(fadeBar, 300);
            }
        }
    }
}