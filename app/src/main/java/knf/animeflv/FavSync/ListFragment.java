package knf.animeflv.FavSync;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

public class ListFragment extends Fragment implements FavSyncAdapter.ListCountInterface {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.no_data)
    LinearLayout linearLayout;
    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    private FavSyncAdapter adapter;

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
        adapter=new FavSyncAdapter(getContext(), getArguments().getInt("type"), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        img_no_data.setImageResource(ThemeUtils.getFlatImage(getContext()));
        return view;
    }

    @Override
    public void onListCount(int count) {
        if (count == 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
