package knf.animeflv;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.HashMap;

import knf.animeflv.playerSources.DemoPlayer;
import knf.animeflv.playerSources.SmoothStreamingRendererBuilder;
import knf.animeflv.playerSources.SmoothStreamingTestMediaDrmCallback;

/**
 * Created by Jordy on 06/02/2016.
 */
public class PlayerExo extends AppCompatActivity {
    private SurfaceView surfaceView;
    private DemoPlayer demoPlayer;
    private Intent intent;
    private String ops;
    private String url;
    private String UserAgent;
    private HashMap<String, String> options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_exo);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        intent = getIntent();
        UserAgent = System.getProperty("http.agent");
        ops = intent.getStringExtra("ops");
        if (ops != null) {
            url = intent.getStringExtra("url");
            options = new HashMap<>();
            String[] conf = ops.split(";;;");
            for (String split : conf) {
                String[] temp = split.split(":::");
                options.put(temp[0], temp[1]);
            }
            //videoView.setVideoURI(Uri.parse(url), options);
            //mMediaPlayer.setDataSource(this, Uri.parse(url),options);
            //new DemoPlayer(new SmoothStreamingRendererBuilder())
        } else {
            url = intent.getStringExtra("url");
            if (url != null) {
                //videoView.setVideoURI(Uri.parse(url));
                //mMediaPlayer.setDataSource(this,Uri.parse(url));
            } else {
                String file = intent.getStringExtra("file");
                if (file != null) {
                    //videoView.setVideoPath(file);
                    //mMediaPlayer.setDataSource(file);
                } else {
                    Log.d("Exo Player", "No URL, No File");
                    Toast.makeText(this, "No URL, NO FILE", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        demoPlayer = new DemoPlayer(new SmoothStreamingRendererBuilder(this, UserAgent, url,
                new SmoothStreamingTestMediaDrmCallback()));

    }
}
