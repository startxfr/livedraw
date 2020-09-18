package fr.startx.livedraw.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.startx.livedraw.DatabaseManager;
import fr.startx.livedraw.R;
import fr.startx.livedraw.models.Paint;
import fr.startx.livedraw.view.RecyclerViewClickListener;
import fr.startx.livedraw.view.holder.PaintViewHolder;

public class PaintAdapter extends RecyclerView.Adapter<PaintViewHolder> {

    private List<Paint> list;
    private RecyclerViewClickListener listener;

    public PaintAdapter(final Context context) {
        this(context, null);
        listener = new RecyclerViewClickListener() {
            public void onClick(View view, int position) {
                Log.w(this.getClass().toString(), "Click at " + position);
                // TODO: Create PaintActivity class
                // Intent intent = new Intent(context, PaintActivity.class);
                // intent.putExtra("paintId", getData(position).getId());
                // context.startActivity(intent);
            }
        };
    }

    public PaintAdapter(Context context, RecyclerViewClickListener listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public PaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.paint_cell, parent, false);
        return new PaintViewHolder(view, this, listener);
    }

    @Override
    public void onBindViewHolder(PaintViewHolder holder, int position) {
        Paint paint = list.get(position);
        holder.display(paint);
    }

    public Paint getData(int position) {
        if (position >= 0 && position <= getItemCount()) {
            return list.get(position);
        }
        return null;
    }

    public void addData(Paint data, int position) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    public void addData(List<Paint> data) {
        list.addAll(data);
    }

    public void removeData(int position) {
        Paint p = getData(position);
        if (p != null) {
            list.remove(position);
            DatabaseManager.deleteDocument(p.getId());
            notifyItemRemoved(position);
        }
    }

    public void setData(List<Paint> data) {
        list = data;
        notifyDataSetChanged();
    }
}