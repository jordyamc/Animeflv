package knf.animeflv.CustomViews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.R;

/**
 * Created by Jordy on 25/09/2017.
 */

public class LauncherIconAdapter extends RecyclerView.Adapter<LauncherIconAdapter.SimpleListVH> implements MDAdapter {
    private MaterialDialog dialog;
    private List<MaterialSimpleListItem> items = new ArrayList(4);
    private LauncherIconAdapter.Callback callback;

    public LauncherIconAdapter(Callback callback) {
        this.callback = callback;
    }

    public void add(MaterialSimpleListItem item) {
        this.items.add(item);
        this.notifyItemInserted(this.items.size() - 1);
    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public MaterialSimpleListItem getItem(int index) {
        return (MaterialSimpleListItem) this.items.get(index);
    }

    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public SimpleListVH onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_list_item, viewGroup, false);
        return new SimpleListVH(view, this);
    }

    @Override
    public void onBindViewHolder(SimpleListVH holder, int position) {
        if (this.dialog != null) {
            MaterialSimpleListItem item = (MaterialSimpleListItem) this.items.get(position);
            if (item.getIcon() != null) {
                holder.icon.setImageDrawable(item.getIcon());
                holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(), item.getIconPadding(), item.getIconPadding());
            } else {
                holder.icon.setVisibility(View.GONE);
            }

            holder.title.setTextColor(this.dialog.getBuilder().getItemColor());
            holder.title.setText(item.getContent());
            this.dialog.setTypeface(holder.title, this.dialog.getBuilder().getRegularFont());
        }
    }

    public int getItemCount() {
        return this.items.size();
    }

    public interface Callback {
        void onMaterialListItemSelected(MaterialDialog var1, int var2, MaterialSimpleListItem var3);
    }

    static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView icon;
        final TextView title;
        final LauncherIconAdapter adapter;

        SimpleListVH(View itemView, LauncherIconAdapter adapter) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(android.R.id.icon);
            this.title = (TextView) itemView.findViewById(android.R.id.title);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (this.adapter.callback != null) {
                this.adapter.callback.onMaterialListItemSelected(this.adapter.dialog, this.getAdapterPosition(), this.adapter.getItem(this.getAdapterPosition()));
            }

        }
    }


}
