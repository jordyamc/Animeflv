package knf.animeflv.WebServer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 14/12/2016.
 */

public class ServerActivity extends AppCompatActivity {
    @BindView(R.id.ip_adress)
    TextView ip;
    @BindView(R.id.action_start)
    FloatingActionButton button;
    ServerManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_server_layout);
        ButterKnife.bind(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ip.setText(getIp());
                        if (getManager().isAlive()) {
                            getManager().stop();
                            Log.e("Web Server", "STOP");
                        } else {
                            try {
                                getManager().startStream(ServerActivity.this, new File(FileUtil.init(ServerActivity.this).getSDPath() + "/Animeflv/download/2357/2357_14.mp4"));
                                Log.e("Web Server", "START");
                            } catch (Exception e) {
                                Log.e("Web Server", "ERROR STARTING");
                            }
                        }
                    }
                });
            }
        });
    }

    private ServerManager getManager() {
        return ServerManager.get();
    }

    private String getIp() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":6991";
    }
}
