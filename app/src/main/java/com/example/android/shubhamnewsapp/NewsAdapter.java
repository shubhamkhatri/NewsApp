package com.example.android.shubhamnewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, -1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View listItemView = convertView;
        News news = getItem(position);
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_story, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }
        else
            holder = (ViewHolder) listItemView.getTag();
        holder.titleTextView.setText(news.getTitle());
        holder.categoryTextView.setText(news.getcategoryLine());
        Date date = news.getDate();
        if (date != null)
            holder.dateTextView.setText(formatDate(date));
        holder.sectionTextView.setText(news.getSection());
        String imageUrl = news.getImageUrl();
        if (!imageUrl.isEmpty())
            Glide
                    .with(getContext())
                    .load(imageUrl)
                    .into(holder.thumbnailImageView);
        else
            holder.thumbnailImageView.setVisibility(View.GONE);

        return listItemView;
    }

    static class ViewHolder{
        @BindView(R.id.story_title)
        TextView titleTextView;
        @BindView(R.id.story_category)
        TextView categoryTextView;
        @BindView(R.id.story_date)
        TextView dateTextView;
        @BindView(R.id.story_section)
        TextView sectionTextView;
        @BindView(R.id.story_image)
        ImageView thumbnailImageView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
}
