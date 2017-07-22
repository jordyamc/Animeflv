package knf.animeflv.FavSync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;

/**
 * Created by Jordy on 19/07/2017.
 */

public class ListFragment extends Fragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public ListFragment() {
    }

    public static Bundle get(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        return bundle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_sync, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new FavSyncAdapter(getContext(), getArguments().getInt("type")));
        return view;
    }
}
