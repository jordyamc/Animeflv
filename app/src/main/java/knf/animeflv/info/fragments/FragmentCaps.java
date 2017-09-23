package knf.animeflv.info.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.RecyclerViewScrollListener;
import com.melnykov.fab.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterInfoCapsMaterial;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 15/12/2016.
 */

public class FragmentCaps extends Fragment {
    public static final String ACTION_RESET = "knf.animeflv.caps.RESET";
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.fastscroll)
    FastScroller fastScroller;
    @BindView(R.id.action_list)
    FloatingActionButton button_list;
    private boolean blocked = false;

    private AdapterInfoCapsMaterial adapter_caps;
    private LinearLayoutManager layoutManager;

    private WeakReference<Activity> activityWeakReference;

    private String json;

    private int progress = 0;

    private BroadcastReceiver receiver;

    public FragmentCaps() {
    }

    public static FragmentCaps get(String aid, String json) {
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putString("json", json);
        FragmentCaps fragment = new FragmentCaps();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_info_f_caps, container, false);
        ButterKnife.bind(this, view);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(getActivity());
        recyclerView.getRootView().setBackgroundColor(theme.background);
        button_list.setColorNormal(theme.accent);
        button_list.setColorPressed(theme.accent);
        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!blocked) {
                    if (MainStates.isListing()) {
                        button_list.setImageResource(R.drawable.ic_add_list);
                        MainStates.setListing(false);
                        adapter_caps.onStopList();
                    } else {
                        button_list.setImageResource(R.drawable.ic_done);
                        MainStates.setListing(true);
                        adapter_caps.onStartList();
                    }
                } else {
                    blocked = false;
                }
            }
        });
        button_list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                button_list.hide(true);
                blocked = true;
                return true;
            }
        });
        setCaps();

        return view;
    }

    private void setCaps() {
        final Bundle bundle = getArguments();
        final String aid = bundle.getString("aid");
        json = bundle.getString("json");
        final Parser parser = new Parser();
        adapter_caps = new AdapterInfoCapsMaterial(getActivity(), parser.parseNumerobyEID(json), aid, parser.parseEidsbyEID(json));
        layoutManager = new LinearLayoutManager(getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter_caps);
                fastScroller.setRecyclerView(recyclerView);
                fastScroller.addScrollerListener(new RecyclerViewScrollListener.ScrollerListener() {
                    @Override
                    public void onScroll(final float v) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!MainStates.isListing())
                                    if (v >= 0.9f) {
                                        button_list.hide(true);
                                    } else {
                                        button_list.show(true);
                                    }
                            }
                        });
                    }
                });
                int position = bundle.getInt("position", -1);
                if (position != -1)
                    layoutManager.scrollToPositionWithOffset(position, 20);

            }
        });
    }

    public void setReference(Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
    }

    private Activity activity() {
        return getActivity();
    }

    public void resetListButton() {
        try {
            button_list.show();
            blocked = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetList() {
        try {
            activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null && adapter_caps != null)
                        adapter_caps.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setallAsSeen() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity())
                .content("Procesando... (0/-)")
                .progress(true, 0)
                .cancelable(false)
                .build();
        List<String> list = new Parser().parseEidsbyEID(json);
        final int total = list.size();
        dialog.setContent("Procesando... (0/" + total + ")");
        dialog.show();
        SeenManager.get(activity()).setListSeenState(list, true, new SeenProgress() {
            @Override
            public void onStep() {
                progress++;
                activity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setContent("Procesando... (" + progress + "/" + total + ")");
                    }
                });
            }

            @Override
            public void onFinish() {
                progress = 0;
                dialog.dismiss();
                activity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter_caps.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void setallAsNotSeen() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity())
                .content("Procesando...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        List<String> list = new Parser().parseEidsbyEID(json);
        dialog.show();
        Log.e("Fast Seen", "Starting set Unseen - Total: " + list.size());
        SeenManager.get(activity()).setListSeenState(list, false, new SeenProgress() {
            @Override
            public void onStep() {

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                activity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter_caps.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public int getFirstItem() {
        if (layoutManager != null) {
            return layoutManager.findFirstVisibleItemPosition();
        } else {
            return -1;
        }
    }

    public interface SeenProgress {
        void onStep();

        void onFinish();
    }
}
