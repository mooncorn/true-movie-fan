package com.example.truemoviefan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Review> reviews;

    public ReviewsAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View reviewView = inflater.inflate(R.layout.item_review, parent, false);

        // Return a new holder instance
        return new ViewHolder(reviewView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Review review = reviews.get(position);

        // Set item views based on your views and data model
        holder.txtReviewContent.setText(review.getContent());
        holder.txtUsername.setText(review.getUsername());
        Picasso.with(context).load(review.getAvatar()).into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtReviewContent, txtUsername;
        public ImageView ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            txtReviewContent = (TextView) itemView.findViewById(R.id.tvReviewContent);
            txtUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
        }
    }
}
