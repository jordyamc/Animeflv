package knf.animeflv.info;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.RecyclerAdapter;

/**
 * Created by Jordy on 12/08/2015.
 */
public class InfoCap extends Fragment{
    public InfoCap(){}
    Parser parser=new Parser();

    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    View view;
    RecyclerView rvAnimes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.info_capitulos,container,false);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("is_amoled", false)) {
            view.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
        rvAnimes = (RecyclerView) view.findViewById(R.id.rv_caps);
        rvAnimes.setHasFixedSize(true);
        rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        String aid = getArguments().getString("aid");
        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), parser.parseNumerobyEID(getJson()),aid,parser.parseEidsbyEID(getJson()));
        rvAnimes.setAdapter(adapter);

        return view;
    }
    public String getJson(){
        String aid = getArguments().getString("aid");
        String json="";
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json=getStringFromFile(file_loc);
        }
        return json;
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
    public static String getStringFromFile (String filePath) {
        String ret="";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        }catch (IOException e){}catch (Exception e){}
        return ret;
    }
}
