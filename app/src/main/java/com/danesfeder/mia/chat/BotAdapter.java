package com.danesfeder.mia.chat;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BotObject> botObjects;

    public BotAdapter(ArrayList<BotObject> botObjects) {
        this.botObjects = botObjects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.botObjects.size();
    }
}
