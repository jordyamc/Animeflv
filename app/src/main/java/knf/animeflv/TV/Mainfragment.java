package knf.animeflv.TV;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainOrganizer;
import knf.animeflv.TV.MainFiles.MainTvAdapter;

public class Mainfragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public Mainfragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tvmain, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        BaseGetter.getJson(getActivity(), new INICIO(), new BaseGetter.AsyncInterface() {
            @Override
            public void onFinish(final String json) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new MainTvAdapter(getActivity(), MainOrganizer.init(json).list(getActivity())));
                    }
                });
            }
        });
        return rootView;
    }
}
