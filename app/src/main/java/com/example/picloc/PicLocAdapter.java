package com.example.picloc;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PicLocAdapter extends RecyclerView.Adapter<PicLocAdapter.ViewHolder> {
    PicLocObject[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView longitudeTextView;
        private final TextView latitudeTextView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageViewLocation);
            longitudeTextView = (TextView) view.findViewById(R.id.textViewLongitude);
            latitudeTextView = (TextView) view.findViewById(R.id.textViewLatitude);
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public TextView getLatitudeTextView() { return this.latitudeTextView; }

        public TextView getLongitudeTextView() { return this.longitudeTextView; }
    }

    public PicLocAdapter(PicLocObject[] dataset) {
        localDataSet = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.photo_location_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getImageView().setImageBitmap(localDataSet[position].getPhoto());
        viewHolder.getLongitudeTextView().setText("Latitude: " + String.valueOf(localDataSet[position].getLatitude()));
        viewHolder.getLatitudeTextView().setText("Longitude: " + String.valueOf(localDataSet[position].getLongitude()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}