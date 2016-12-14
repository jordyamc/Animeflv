package knf.animeflv.SDControl;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Files.FileSearchResponse;

public class SDAdapter extends RecyclerView.Adapter<SDAdapter.ViewHolder> {

    private Activity activity;
    private FileSearchResponse items;
    private OnOptionsClicklistener clicklistener;

    public SDAdapter(Activity activity, FileSearchResponse items, OnOptionsClicklistener clicklistener) {
        this.activity = activity;
        this.items = items;
        this.clicklistener = clicklistener;
    }

    @Override
    public SDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SDAdapter.ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_sd_option, parent, false));
    }

    @Override
    public void onBindViewHolder(final SDAdapter.ViewHolder holder, final int position) {
        String name = items.list().get(holder.getAdapterPosition());
        if (name.contains("_noWrite_")) {
            holder.button.setBackgroundColor(ColorsRes.Rojo(activity));
        } else {
            holder.button.setBackgroundColor(ColorsRes.Verde(activity));
        }
        holder.button.setText(name.replace("_noWrite_", ""));
        String path = FileUtil.init(activity).getSDPath();
        if (path != null) {
            if (path.equals(items.listDisrs().get(holder.getAdapterPosition()))) {
                holder.check.setChecked(true);
                if (!name.contains("_noWrite_")) {
                    clicklistener.onOptionOK();
                }
            } else {
                holder.check.setChecked(false);
            }
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("SDPath", items.list().get(holder.getAdapterPosition()).replace("_noWrite_", "")).commit();
                if (items.list().get(holder.getAdapterPosition()).contains("_noWrite_")) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    activity.startActivityForResult(intent, SDSearcher.GRANT_WRITE_PERMISSION_CODE);
                } else {
                    holder.check.setChecked(true);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.list().size();
    }

    public interface OnOptionsClicklistener {
        void onClick(int selected, boolean havePermission);

        void onOptionOK();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.button)
        AppCompatButton button;
        @BindView(R.id.check)
        AppCompatCheckBox check;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
