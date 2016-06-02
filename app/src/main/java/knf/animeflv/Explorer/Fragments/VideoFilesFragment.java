package knf.animeflv.Explorer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.Explorer.Adapters.VideoFileAdapter;
import knf.animeflv.R;

/**
 * Created by Jordy on 01/06/2016.
 */

public class VideoFilesFragment extends Fragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    public VideoFilesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.explorer_fragment, container, false);
        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new VideoFileAdapter(getActivity(), new File(getArguments().getString("path"))));
        return root;
    }
}
