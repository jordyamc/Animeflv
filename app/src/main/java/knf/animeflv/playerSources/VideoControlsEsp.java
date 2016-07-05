package knf.animeflv.playerSources;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;


public class VideoControlsEsp extends VideoControls implements VideoListeners{
    ImageButton back;
    Finish finish;
    SeekBar seekBar;
    long duration;
    Context context;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private static final int HANDLER_ANIMATE_OUT = 1;// out animate
    private static final int HANDLER_UPDATE_PROGRESS = 2;//cycle update progress
    private static final long PROGRESS_SEEK = 3000;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    public interface Finish {
        void onArrowPressed();
    }



    public VideoControlsEsp(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void showLoading(boolean initialLoad) {

    }

    @Override
    public void setPosition(@IntRange(from = 0L) long position) {
        DateFormat outFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        outFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date(position);
        String total = outFormat.format(d);
        currentTime.setText(total.startsWith("00:") ? total.substring(3) : total);
    }

    @Override
    public void setDuration(@IntRange(from = 0L) long duration) {
        this.duration=duration;
        DateFormat outFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        outFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date(duration);
        String total = outFormat.format(d);
        endTime.setText(total.startsWith("00:") ? total.substring(3) : total);
    }

    @Override
    public void updateProgress(@IntRange(from = 0L) long position, @IntRange(from = 0L) long duration, @IntRange(from = 0L, to = 100L) int bufferPercent) {
        seekBar.setProgress((int) ((position * 100) / duration));
        seekBar.setSecondaryProgress(bufferPercent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.exo_controller;
    }

    @Override
    protected void retrieveViews() {
        currentTime = (TextView) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_current_time);
        endTime = (TextView) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_end_time);

        titleView = (TextView) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_title);

        playPauseButton = (ImageButton) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_play_pause_btn);

        playPauseButton.setColorFilter(Color.WHITE);

        loadingProgress = (ProgressBar) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_video_loading);

        controlsContainer = (ViewGroup) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_interactive_container);
        textContainer = (ViewGroup) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_text_container);

        back = (ImageButton) findViewById(R.id.top_back);

        seekBar = (SeekBar) findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_video_seek);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    @Override
    protected void registerListeners() {
        playPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayPauseClick();
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView != null) videoView.release();
                finish.onArrowPressed();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                DateFormat outFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                outFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = new Date(((progress*duration)/100));
                String total = outFormat.format(d);
                currentTime.setText(total.startsWith("00:") ? total.substring(3) : total);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                internalListener.onSeekStarted();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                internalListener.onSeekEnded((int)((seekBar.getProgress()*duration)/100));
            }
        });
    }

    @Override
    public void setVideoView(@Nullable EMVideoView EMVideoView) {
        super.setVideoView(EMVideoView);
        MyGestureDetector detector=new MyGestureDetector(context,this);
        videoView.setOnTouchListener(detector);
    }

    public void setOnFinishListener(Finish finishListener) {
        this.finish = finishListener;
    }

    @Override
    protected void updateButtonDrawables() {

    }

    @Override
    protected void animateVisibility(boolean toVisible) {
        isVisible = toVisible;
        if (toVisible){
            textContainer.setVisibility(VISIBLE);
            controlsContainer.setVisibility(VISIBLE);
            seekBar.setVisibility(VISIBLE);
            titleView.setVisibility(VISIBLE);
            playPauseButton.setVisibility(VISIBLE);
            if (videoView.isPlaying())hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
        }else {
            hide();
        }
        onVisibilityChanged();
    }

    public void hide() {
        visibilityHandler.removeCallbacks(new Runnable() {
            @Override
            public void run() {
                animateVisibility(false);
            }
        }, hideDelay);
        textContainer.setVisibility(GONE);
        controlsContainer.setVisibility(GONE);
    }

    @Override
    protected void updateTextContainerVisibility() {
        titleView.setVisibility(VISIBLE);
    }

    @Override
    public void finishLoading() {
        loadingProgress.setVisibility(GONE);
    }

    @Override
    public void onSingleTouch() {
        animateVisibility(!isVisible);
    }

    @Override
    public void onDoubleTouch() {

    }

    @Override
    public void onVerticalScroll(MotionEvent motionEvent, float delta, int direction) {

    }

    @Override
    public void onHorizontalScroll(MotionEvent event, float delta) {
        onStartSeek();
        if (event.getPointerCount() == 1) {
            if (delta > 0) {// seek forward
                seekForWard();
            } else {  //seek backward
                seekBackWard();
            }
        }
    }

    private void seekBackWard() {
        if (videoView == null) {
            return;
        }

        int pos = videoView.getCurrentPosition();
        pos -= PROGRESS_SEEK;
        videoView.seekTo(pos);
        setSeekProgress();
    }

    private void seekForWard() {
        if (videoView == null) {
            return;
        }

        int pos = videoView.getCurrentPosition();
        pos += PROGRESS_SEEK;
        videoView.seekTo(pos);
        setSeekProgress();
    }

    private int setSeekProgress() {

        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            //get buffer percentage
            //int percent = mPlayer.getBufferPercentage();
            //set buffer progress
            //mSeekBar.setSecondaryProgress(percent * 10);
        }

        if (endTime != null)
            endTime.setText(stringToTime(duration));
        if (currentTime != null)
            currentTime.setText(stringToTime(position));
        return position;
    }

    private void onStartSeek(){
        if (videoView.isPlaying()){
            videoView.pause();
        }
        if (controlsContainer.getVisibility()==GONE){
            controlsContainer.setVisibility(VISIBLE);
        }
    }

    private void onStopSeek(){
        if (!videoView.isPlaying()){
            videoView.start();
        }
        hide();
    }

    private String stringToTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener, OnTouchListener{
        public static final int SWIPE_LEFT = 1;
        public static final int SWIPE_RIGHT = 2;
        private static final String TAG = "ViewGestureListener";
        private static final int SWIPE_THRESHOLD = 60;
        Context context;
        VideoListeners listeners;

        public MyGestureDetector(Context context, VideoListeners listeners) {
            this.context = context;
            this.listeners = listeners;
            gestureDetector = new GestureDetector(context, this);
        }

        public int getDeviceWidth(Context context) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
            return mDisplayMetrics.widthPixels;
        }

        public int getDeviceHeight(Context context) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
            return mDisplayMetrics.heightPixels;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
            /*final int action = MotionEventCompat.getActionMasked(motionEvent);
            if (action==MotionEvent.ACTION_CANCEL||action==MotionEvent.ACTION_UP){
                onStopSeek();
                return false;
            }else {
                return gestureDetector.onTouchEvent(motionEvent);
            }*/
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            listeners.onSingleTouch();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaX = e2.getX() - e1.getX();
            float deltaY = e2.getRawY() - e1.getY();
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                    listeners.onHorizontalScroll(e2, deltaX);
                }
                return true;
            } else {
                if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (e1.getX() < getDeviceWidth(context) * 1.0 / 5) {//left edge
                        Log.e("-deltaY", "" + -deltaY);
                        listeners.onVerticalScroll(e2, -deltaY * 0.2f, SWIPE_LEFT);
                    } else if (e1.getX() > getDeviceWidth(context) * 4.0 / 5) {//right edge
                        Log.e("-deltaY", "" + -deltaY);
                        listeners.onVerticalScroll(e2, -deltaY * 0.5f, SWIPE_RIGHT);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }


    }
}
