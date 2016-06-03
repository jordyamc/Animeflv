package knf.animeflv.Explorer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Explorer.Adapters.DirectoryAdapter;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;


public class DirectoryFragment extends Fragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    public DirectoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.explorer_fragment, container, false);
        ButterKnife.bind(this, root);
        if (ThemeUtils.isAmoled(getActivity()))
            root.getRootView().setBackgroundColor(ColorsRes.Negro(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new DirectoryAdapter(getActivity()));
        return root;
    }
}
