package knf.animeflv.info.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterInfoCapsMaterial;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 15/12/2016.
 */

public class FragmentCaps extends Fragment {
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.action_list)
    FloatingActionButton button_list;

    private boolean blocked = false;

    private AdapterInfoCapsMaterial adapter_caps;
    private RecyclerView.LayoutManager layoutManager;

    private WeakReference<Activity> activityWeakReference;

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
        button_list.setColorNormal(ThemeUtils.getAcentColor(getActivity()));
        button_list.setColorPressed(ThemeUtils.getAcentColor(getActivity()));
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
        Bundle bundle = getArguments();
        final String aid = bundle.getString("aid");
        final String json = bundle.getString("json");
        final Parser parser = new Parser();
        adapter_caps = new AdapterInfoCapsMaterial(getActivity(), parser.parseNumerobyEID(json), aid, parser.parseEidsbyEID(json));
        layoutManager = new LinearLayoutManager(getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter_caps);
            }
        });
    }

    public void setReference(Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
    }

    private Activity activity() {
        return activityWeakReference.get();
    }

    public void resetListButton() {
        button_list.show();
        blocked = false;
    }

    public void resetList() {
        activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null && adapter_caps != null)
                    adapter_caps.notifyDataSetChanged();
            }
        });
    }
}
