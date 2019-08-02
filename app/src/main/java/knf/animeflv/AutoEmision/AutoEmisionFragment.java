package knf.animeflv.AutoEmision;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class AutoEmisionFragment extends Fragment implements OnListInteraction {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.no_data)
    LinearLayout no_data;
    @BindView(R.id.img_no_data)
    ImageView no_data_img;
    private AutoEmisionAdapter adapter;
    private EmisionRemoveListener listener;

    private List<EmObj> list;
    private int day;

    private int removeCount = 0;

    public AutoEmisionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_emision, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void asyncStart() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    day = getArguments().getInt("day");
                    list = AutoEmisionHelper.getDayList(getActivity(), getArguments().getString("array"), day);
                    startVerification(list, day);
                    setRecycler(list, day);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void setListener(EmisionRemoveListener listener) {
        Log.e("Emision Fragment", "Listener setted!!!");
        this.listener = listener;
        asyncStart();
    }

    private void setRecycler(final List<EmObj> list, int day) {
        try {
            AutoEmisionListHolder.setList(day, list);
            final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                private ThemeUtils.Theme theme = ThemeUtils.Theme.create(getContext());
                private boolean first = true;
                private boolean last = false;

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    adapter.onMoveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    AutoEmisionAdapter.ViewHolder holder = (AutoEmisionAdapter.ViewHolder) viewHolder;
                    if (first) {
                        ViewPropertyAnimator animator = holder.cardView.animate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardView.setCardElevation(8);
                            animator.start();
                        } else {
                            holder.cardView.setCardBackgroundColor(theme.accent);
                            holder.state.setColorFilter(theme.card_normal);
                            holder.title.setTextColor(ColorsRes.Blanco(getContext()));
                        }
                        first = false;
                    }
                    if (last) {
                        ViewPropertyAnimator animator = holder.cardView.animate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardView.setCardElevation(1);
                            animator.start();
                        } else {
                            holder.cardView.setCardBackgroundColor(theme.card_normal);
                            holder.state.setColorFilter(theme.accent);
                            holder.title.setTextColor(theme.textColor);
                        }
                        last = false;
                        first = true;
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    last = true;
                }
            });
            adapter = new AutoEmisionAdapter(getActivity(), list, day, AutoEmisionFragment.this);
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    helper.attachToRecyclerView(recyclerView);
                    if (list.size() == 0) {
                        no_data_img.setImageResource(ThemeUtils.getFlatImage(getActivity()));
                        no_data.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVerification(final List<EmObj> list, final int day) {
        new AsyncTask<Void, Void, Void>() {
            private boolean edited = false;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<EmObj> new_list = new ArrayList<>(list);
                    for (final EmObj obj : list) {
                        Log.e("Emision Check", "Day: " + day + "  Title: " + obj.getTitle());
                        String f_json = SelfGetter.getAnimeSync(getActivity(), new ANIME(Integer.parseInt(obj.getAid())));
                        if (!f_json.equals("null") && !f_json.startsWith("error"))
                            try {
                                JSONObject object = new JSONObject(f_json);
                                if (!isEmision(object)) {
                                    Log.e("AutoEmision", "Deleting from list: " + obj.getTitle());
                                    AutoEmisionListHolder.deleteFromList(new_list, obj.getAid(), day);
                                    AutoEmisionHelper.removeAnimeFromList(getActivity(), obj.getAid(), day, null);
                                    edited = true;
                                    removeCount++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                    if (edited) {
                        setRecycler(new_list, day);
                        if (listener != null)
                            listener.onEmisionRemove(removeCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private boolean isEmision(JSONObject object) {
        try {
            switch (object.getString("fecha_fin").trim()) {
                case "0000-00-00":
                case "prox":
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onListEdited(List<EmObj> list) {
        AutoEmisionListHolder.setList(getArguments().getInt("day"), list);
    }

    @Override
    public void onDestroyView() {
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView.setItemAnimator(null);
            recyclerView = null;
        }
        super.onDestroyView();
    }

    interface EmisionRemoveListener {
        void onEmisionRemove(int removeCount);
    }
}
