package knf.animeflv.Explorer.Fragments;

import android.app.Activity;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Explorer.Adapters.DirectoryAdapter;
import knf.animeflv.Explorer.Models.Directory;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;


public class DirectoryFragment extends Fragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.loading)
    InfinityLoading loading;
    @Bind(R.id.no_anime)
    TextView noAnime;
    private LinearLayoutManager manager;
    private List<Directory> current;
    private DirectoryAdapter adapter;

    public DirectoryFragment() {
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
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        ModelFactory.createDirectoryListAsync(getActivity(), new ModelFactory.AsyncDirectoryListener() {
            @Override
            public void onCreated(final List<Directory> list) {
                try {
                    current = list;
                    adapter = new DirectoryAdapter(getActivity(), list);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            recyclerView.setAdapter(adapter);
                            Log.d("Directory", "Size: " + list.size());
                            if (list.size() == 0) {
                                noAnime.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                noAnime.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (NullPointerException e) {
                    Log.e("DirectoryFragment", "Context is already NULL!!!!");
                }
            }
        });
        return root;
    }

    public boolean isListEqual(List<Directory> list) {
        if (current != null) {
            if (list.size() == current.size()) {
                for (int i = 0; i < current.size(); i++) {
                    if (current.get(i).getID().equals(list.get(i).getID())) {
                        if (!current.get(i).getFilesNumber().equals(list.get(i).getFilesNumber())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int aidPositionInCurrent(String aid) {
        int pos = 0;
        for (Directory directory : current) {
            if (directory.getID().equals(aid)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public void deleteDirectory(final Activity activity, final String aid) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int pos = aidPositionInCurrent(aid);
                if (pos != -1) {
                    adapter.notifyItemRemoved(pos);
                    adapter.recreateList(activity, getFinishListener(activity));
                } else {
                    adapter.recreateList(activity, getFinishListener(activity));
                }
            }
        });
    }

    private DirectoryAdapter.OnFinishListListener getFinishListener(final Activity activity) {
        return new DirectoryAdapter.OnFinishListListener() {
            @Override
            public void onFinish(final int count) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 0) {
                            noAnime.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noAnime.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
    }

    public void recharge(final Activity activity) {
        if (adapter == null) {
            ModelFactory.createDirectoryListAsync(activity, new ModelFactory.AsyncDirectoryListener() {
                @Override
                public void onCreated(final List<Directory> list) {
                    try {
                        if (!isListEqual(list)) {
                            current = list;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int pos = manager.findFirstVisibleItemPosition();
                                    recyclerView.setAdapter(new DirectoryAdapter(activity, list));
                                    int count = manager.getChildCount();
                                    if (pos != RecyclerView.NO_POSITION && pos < count) {
                                        manager.scrollToPosition(pos);
                                    }
                                    if (list.size() == 0) {
                                        noAnime.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        noAnime.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    } catch (NullPointerException e) {
                        Log.e("DirectoryFragment", "Context is already NULL!!!!");
                    }
                }
            });
        } else {
            adapter.recreateList(activity, getFinishListener(activity));
        }
    }
}
