package knf.animeflv.SDControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
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
        String name = items.list().get(Gposition(holder, position));
        if (name.contains("_noWrite_")) {
            holder.button.setBackgroundColor(ColorsRes.Rojo(activity));
        } else {
            holder.button.setBackgroundColor(ColorsRes.Verde(activity));
        }
        holder.button.setText(name.replace("_noWrite_", ""));
        String path = FileUtil.init(activity).getSDPath();
        if (path != null) {
            if (path.equals(items.listDisrs().get(Gposition(holder, position)))) {
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
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("SDPath", items.list().get(Gposition(holder, position)).replace("_noWrite_", "")).commit();
                if (items.list().get(Gposition(holder, position)).contains("_noWrite_") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    activity.startActivityForResult(intent, SDSearcher.GRANT_WRITE_PERMISSION_CODE);
                } else {
                    holder.check.setChecked(true);
                    notifyDataSetChanged();
                }
            }
        });
        holder.button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    activity.startActivityForResult(intent, SDSearcher.GRANT_WRITE_PERMISSION_CODE);
                }
                return false;
            }
        });
    }

    private int Gposition(ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
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
