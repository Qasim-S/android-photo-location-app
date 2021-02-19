package com.example.picloc;

import android.hardware.Camera.PictureCallback;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PicLocAdapter extends RecyclerView.Adapter<PicLocAdapter.ViewHolder> {
    ArrayList<PicLocObject> localDataSet;
    private RecyclerViewClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageView;
        private final TextView longitudeTextView;
        private final TextView latitudeTextView;
        private final TextView idTextView;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageViewLocation);
            longitudeTextView = (TextView) view.findViewById(R.id.textViewLongitude);
            latitudeTextView = (TextView) view.findViewById(R.id.textViewLatitude);
            idTextView = (TextView) view.findViewById(R.id.textViewID);

            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public TextView getLatitudeTextView() { return this.latitudeTextView; }

        public TextView getLongitudeTextView() { return this.longitudeTextView; }

        public TextView getIdTextView() { return this.idTextView; }
    }

    public PicLocAdapter(ArrayList<PicLocObject> dataset, RecyclerViewClickListener listener) {
        localDataSet = dataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.photo_location_row_item, viewGroup, false);

        return new ViewHolder(view, mListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getImageView().setImageBitmap(localDataSet.get(position).getPhoto());
        viewHolder.getLongitudeTextView().setText("Latitude: " + String.valueOf(localDataSet.get(position).getLatitude()));
        viewHolder.getLatitudeTextView().setText("Longitude: " + String.valueOf(localDataSet.get(position).getLongitude()));
        viewHolder.getIdTextView().setText(String.valueOf(localDataSet.get(position).getId()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


}