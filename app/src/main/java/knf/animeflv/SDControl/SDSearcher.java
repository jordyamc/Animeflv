package knf.animeflv.SDControl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Files.FileSearchResponse;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class SDSearcher extends Fragment implements SDAdapter.OnOptionsClicklistener {

    public static final int GRANT_WRITE_PERMISSION_CODE = 558;
    public static final int SD_SELECTED = 887;
    public static final int SD_NO_SELECTED = 884;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.titulo)
    TextView textView;
    private FileSearchResponse response;

    public SDSearcher() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sd_search, container, false);
        ButterKnife.bind(this, view);
        textView.setTextColor(ThemeUtils.isAmoled(getActivity()) ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
        response = FileUtil.init(getActivity()).searchforSD();
        if (response.existSD()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new SDAdapter(getActivity(), response, this));
        } else {
            Toaster.toast("SD no encontrada");
            getActivity().finish();
        }
        return view;
    }

    public void resetResponse() {
        try {
            response = FileUtil.init(getActivity()).searchforSD();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(new SDAdapter(getActivity(), response, SDSearcher.this));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(int selected, boolean havePermission) {
        if (havePermission) {
            SDResultContainer.setResult(SD_SELECTED);
        }
    }

    @Override
    public void onOptionOK() {
        ((SDManager) getActivity()).setFinishEnabled(true);
    }
}
