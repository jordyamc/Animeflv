package knf.animeflv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Recyclers.AdapterFavs;
import knf.animeflv.Recyclers.RecyclerAdapter;

/**
 * Created by Jordy on 23/08/2015.
 */
public class Favoritos extends AppCompatActivity implements RequestFav.callback{
    RecyclerView recyclerView;
    Toolbar toolbar;
    Toolbar ltoolbar;
    List<String> aids=new ArrayList<String>();
    List<String> Naids=new ArrayList<String>();
    String fav;
    String[] favoritos={};
    Context context;
    boolean shouldExecuteOnResume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_favs);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ltoolbar=(Toolbar) findViewById(R.id.ltoolbar_fav);
        }
        context=this;
        shouldExecuteOnResume = false;
        toolbar=(Toolbar) findViewById(R.id.favs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoritos");
        if (isXLargeScreen(context)){
            ltoolbar.setNavigationIcon(R.drawable.ic_back_r);
            ltoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        SharedPreferences sharedPreferences=getSharedPreferences("data", MODE_PRIVATE);
        fav=sharedPreferences.getString("favoritos", "");
        favoritos=fav.split(":::");
        aids=new ArrayList<String>();
        for (String i:favoritos){
            if (!i.equals("")) {
                aids.add(i);
            }
        }
        favoritos=new String[aids.size()];
        aids.toArray(favoritos);
        recyclerView=(RecyclerView) findViewById(R.id.rv_favs);
        if (aids.size()==0){
            Toast.makeText(context,"No hay favoritos",Toast.LENGTH_LONG).show();
        }else {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            new RequestFav(this, TaskType.GET_INFO).execute(favoritos);
        }
    }
    private boolean isNetworkAvailable() {
        Boolean net=false;
        int Tcon=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon){
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net=Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=WifiA.isConnected()||mobileA.isConnected();
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
    @Override
    public void favCall(String data,TaskType taskType){
        if (!data.trim().equals("")) {
            String[] crop = data.split(":::");
            List<String> titulos = new ArrayList<String>();
            for (String i : crop) {
                if (!i.trim().equals("")) {
                    titulos.add(i);
                }
            }
            List<String> links = new ArrayList<String>();
            for (String aid : aids) {
                if (isNetworkAvailable()) {
                    links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
                }else {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
                    if (file.exists()){
                        links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
                    }
                }
            }
            for (String tit : links) {
                Log.d("URL IMG", tit);
            }
            Log.d("Ntitulos", Integer.toString(titulos.size()));
            Log.d("Naids", Integer.toString(aids.size()));
            Log.d("Nlinks", Integer.toString(links.size()));
            if (!isNetworkAvailable()){
                if (favoritos.length!=links.size())
                Toast.makeText(context,"Sin conexion, cargando favoritos con cache disponible",Toast.LENGTH_SHORT).show();
            }
            AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(context,"Error de red",Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext()) ) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            recreate();
        } else{
            shouldExecuteOnResume = true;
        }
    }
}
