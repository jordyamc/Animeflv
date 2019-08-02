package knf.animeflv.Directorio;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterDirPeliculaNew;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;

public class Animes extends Fragment{
    RecyclerView rvAnimes;
    View view;
    Parser parser=new Parser();

    public Animes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.directorio_animes,container,false);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(getActivity());
        view.setBackgroundColor(ThemeUtils.isTablet(getActivity()) ? theme.primary : theme.background);
        rvAnimes = view.findViewById(R.id.rv_animes);
        rvAnimes.setHasFixedSize(true);
        rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        if (ThemeUtils.isTablet(getActivity()) && !DesignUtils.forcePhone(getActivity())) {
            rvAnimes.setPadding(0, (int) Parser.toPx(getActivity(), 10), 0, Keys.getNavBarSize(getActivity()));
            rvAnimes.setClipToPadding(false);
        }
        setDirectory(getActivity());
        Log.e(this.getClass().getName(), "Created!!!");
        return view;
    }

    public void setDirectory(final Activity context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    List<AnimeClass> animes = DirectoryHelper.get(context).getAllType("Anime");
                    final AdapterDirPeliculaNew adapter = new AdapterDirPeliculaNew(context, animes);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvAnimes.setAdapter(adapter);
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }
}
