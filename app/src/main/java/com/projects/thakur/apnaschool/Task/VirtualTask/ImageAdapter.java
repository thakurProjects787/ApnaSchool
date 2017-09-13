package com.projects.thakur.apnaschool.Task.VirtualTask;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UploadFiles.ShowGalleryActivity;
import com.projects.thakur.apnaschool.UploadFiles.Upload;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<VirtualResultTaskModel> uploads;
    private CharSequence options[];
    private VirtualResultTaskModel upload;

    public ImageAdapter(Context context, List<VirtualResultTaskModel> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_show_gallery_photos, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VirtualResultTaskModel upload = uploads.get(position);

        holder.textViewName.setText(upload.getName());

        Glide.with(context).load(upload.getUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int pos = getAdapterPosition();
                    upload = uploads.get(pos);

                    // ************************************************************
                    // --------- Alert Dialog Box with Option ---------------------

                    // Delete Permission only for Owner
                     options = new CharSequence[]{"Download"};


                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setCancelable(false);
                    builder.setTitle("Select your option:");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on options[which]
                            //Toast.makeText(itemView.getContext(), upload.getName()+" - "+options[which], Toast.LENGTH_SHORT).show();

                            ShowEachVirtualTaskActivity eachGallery = new ShowEachVirtualTaskActivity();


                            if (options[which].equals("Download")) {
                            /*
                               Download file into local storage
                             */
                                eachGallery.downloadImage(upload, itemView.getContext());

                            }

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //the user clicked on Cancel
                        }
                    });
                    builder.show();

                    // *************************************************************
                }
            });
        }

    }


}
