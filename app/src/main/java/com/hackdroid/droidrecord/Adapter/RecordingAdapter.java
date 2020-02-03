package com.hackdroid.droidrecord.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackdroid.droidrecord.R;

import java.io.File;
import java.text.SimpleDateFormat;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.Viewholder> {
    File[] files;
    Activity activity;
    Context context;
    private OnItemClick onItemClick;

    public RecordingAdapter(File[] files, Activity activity, Context context, OnItemClick onItemClick) {
        this.files = files;
        this.activity = activity;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_audio_file, parent, false);
        return new RecordingAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        holder.time.setText("" + sdf.format(files[position].lastModified()));
        holder.name.setText("" + files[position].getName());
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, time;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClick.onAudioItemClick(files[getAdapterPosition()], getAdapterPosition());

        }
    }

    public interface OnItemClick {
        void onAudioItemClick(File file, int position);
    }
}
