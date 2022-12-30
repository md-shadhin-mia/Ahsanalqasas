package com.shadhin.ahsanalqasas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListOfStoryAdapter extends RecyclerView.Adapter<ListOfStoryAdapter.ViewHolder> {


    private ArrayList<String> story;
    private Context context;

    public ListOfStoryAdapter(ArrayList<String> story){
        this.story = story;
    }

    public ListOfStoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View StoryView = inflater.inflate(R.layout.story_list_item, parent, false);
        return new ListOfStoryAdapter.ViewHolder(StoryView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String str = this.story.get(position);
        holder.storyName.setText(str.split(";")[0]);
        holder.storyDirection.setText(str.split(";")[1]);
        holder.ClickAbleCard.setOnClickListener(new ItemClickListener(str.split(";")[0], str.split(";")[1]));
    }
    @Override
    public int getItemCount() {
        return story.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView storyName;
        TextView storyDirection;
        CardView ClickAbleCard;
        public ViewHolder(View itemView) {
            super(itemView);
            storyName = itemView.findViewById(R.id.story_name);
            storyDirection = itemView.findViewById(R.id.story_directions);
            ClickAbleCard = itemView.findViewById(R.id.story_item_card);
        }
    }

    public class ItemClickListener implements View.OnClickListener {
        private String ayat;
        private String title;

        public ItemClickListener(String titleD, String ayatD) {
            ayat = ayatD;
            title = titleD;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), QuranStory.class);
            intent.putExtra("title", this.title );
            intent.putExtra("ayats", this.ayat);
            view.getContext().startActivity(intent);
        }
    }
}
