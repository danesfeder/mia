package com.danesfeder.mia.chatview.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.danesfeder.mia.chatview.dataobjects.BotObject;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(BotObject object);
}
