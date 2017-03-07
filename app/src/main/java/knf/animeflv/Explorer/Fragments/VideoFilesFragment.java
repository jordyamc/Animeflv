package knf.animeflv.Explorer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ndczz.infinityloading.InfinityLoading;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Explorer.Adapters.DirectoryAdapter;
import knf.animeflv.Explorer.Adapters.VideoFileAdapter;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.Explorer.Models.VideoFile;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

public class VideoFilesFragment extends Fragment {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    InfinityLoading loading;
    @BindView(R.id.no_anime)
    TextView noAnime;

    public VideoFilesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.explorer_fragment, container, false);
        ButterKnife.bind(this, root);
        loading.setProgressColor(ThemeUtils.getAcentColor(getActivity()));
        if (ThemeUtils.isAmoled(getActivity())) {
            root.getRootView().setBackgroundColor(ColorsRes.Negro(getActivity()));
            noAnime.setTextColor(ColorsRes.SecondaryTextDark(getActivity()));
        } else {
            noAnime.setTextColor(ColorsRes.SecondaryTextLight(getActivity()));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final File file = new File(getArguments().getString("path"));
        ModelFactory.createVideosListAsync(file, new ModelFactory.AsyncFileListener() {
            @Override
            public void onCreated(final List<VideoFile> list) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            recyclerView.setAdapter(new VideoFileAdapter(getActivity(), file, list, new DirectoryAdapter.OnFinishListListener() {
                                @Override
                                public void onFinish(final int count) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (count == 0) {
                                                noAnime.setVisibility(View.VISIBLE);
                                            } else {
                                                noAnime.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }));
                            if (list.size() == 0) {
                                noAnime.setVisibility(View.VISIBLE);
                            } else {
                                noAnime.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("VideoFragment", "Context is already NULL!!!!");
                }
            }
        });
        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        try {
            ((VideoFileAdapter) recyclerView.getAdapter()).performDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
