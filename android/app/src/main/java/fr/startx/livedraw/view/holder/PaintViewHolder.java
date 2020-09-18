package fr.startx.livedraw.view.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.startx.livedraw.R;
import fr.startx.livedraw.models.Paint;
import fr.startx.livedraw.view.RecyclerViewClickListener;
import fr.startx.livedraw.view.adapter.PaintAdapter;

public class PaintViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView deleteButton;
    private TextView subtitleView;
    private TextView titleView;

    private RecyclerViewClickListener listener;

    private PaintAdapter adapter;

    public PaintViewHolder(@NonNull View itemView, final PaintAdapter adapter, RecyclerViewClickListener clickListener) {
        super(itemView);

        this.adapter = adapter;

        titleView = itemView.findViewById(R.id.title);
        subtitleView = itemView.findViewById(R.id.subtitle);
        deleteButton = itemView.findViewById(R.id.btn_trash);

        this.listener = clickListener;
        itemView.setOnClickListener(this);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeData(getAdapterPosition());
            }
        });
    }

    public void display(Paint paint) {
        titleView.setText(paint.getName());
        subtitleView.setText(paint.toString());
    }

    @Override
    public void onClick(View view) {
        Log.w("onClick", "Click on View");
        listener.onClick(view, getAdapterPosition());
    }
}
