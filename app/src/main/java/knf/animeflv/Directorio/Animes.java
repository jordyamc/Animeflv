package knf.animeflv.Directorio;

/**
 * Created by Jordy on 30/08/2015.
 */
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import knf.animeflv.AnimeSorter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterDirAnimeNew;

public class Animes extends Fragment{
    RecyclerView rvAnimes;
    View view;
    Parser parser=new Parser();
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    public Animes() {
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.directorio_animes,container,false);
        rvAnimes = (RecyclerView) view.findViewById(R.id.rv_animes);
        rvAnimes.setHasFixedSize(true);
        rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        String json=getJson();
        List<AnimeClass> animes = AnimeSorter.sortByName(parser.DirAnimes(json));
        AdapterDirAnimeNew adapter = new AdapterDirAnimeNew(getActivity(), animes);
        rvAnimes.setAdapter(adapter);
        return view;
    }

    public String getJson() {
        String json = "";
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json = getStringFromFile(file_loc);
        }
        return json;
    }
}
