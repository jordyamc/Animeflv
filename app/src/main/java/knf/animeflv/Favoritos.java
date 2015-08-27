package knf.animeflv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    List<String> aids=new ArrayList<String>();
    List<String> Naids=new ArrayList<String>();
    Context context;
    boolean shouldExecuteOnResume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_favs);
        context=this;
        shouldExecuteOnResume = false;
        toolbar=(Toolbar) findViewById(R.id.favs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoritos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        String fav=sharedPreferences.getString("favoritos", "");
        String[] favoritos={};
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
    @Override
    public void favCall(String data,TaskType taskType){
        String[] crop = data.split(":::");
        List<String> titulos = new ArrayList<String>();
        for (String i : crop) {
           if (!i.trim().equals("")) {
               titulos.add(i);
            }
        }
        List<String> links = new ArrayList<String>();
        for (String aid : aids) {
           links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
        }
        for (String tit : links) {
           Log.d("URL IMG", tit);
        }
        Log.d("Ntitulos", Integer.toString(titulos.size()));
        Log.d("Naids", Integer.toString(aids.size()));
        Log.d("Nlinks", Integer.toString(links.size()));
        AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
        recyclerView.setAdapter(adapter);
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
