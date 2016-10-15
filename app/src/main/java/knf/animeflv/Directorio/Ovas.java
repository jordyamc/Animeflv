package knf.animeflv.Directorio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import knf.animeflv.AnimeSorter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterDirAnimeNew;

/**
 * Created by Jordy on 30/08/2015.
 */
public class Ovas extends Fragment {
    RecyclerView rvAnimes;
    View view;
    Parser parser=new Parser();

    public Ovas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.directorio_ovas,container,false);
        rvAnimes = (RecyclerView) view.findViewById(R.id.rv_ovas);
        rvAnimes.setHasFixedSize(true);
        rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        String json=getJson();
        List<AnimeClass> animes = AnimeSorter.sortByName(parser.DirOvas(json));
        AdapterDirAnimeNew adapter = new AdapterDirAnimeNew(getActivity(), animes);
        rvAnimes.setAdapter(adapter);
        return view;
    }

    public String getJson() {
        return getArguments().getString("json");
    }
}
