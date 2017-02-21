package com.danesfeder.mia.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chat.dataobjects.BotList;
import com.danesfeder.mia.chat.viewholders.BaseViewHolder;
import com.danesfeder.mia.chat.viewholders.BotResponseViewHolder;
import com.danesfeder.mia.chat.viewholders.UserInputViewHolder;

import static com.danesfeder.mia.chat.dataobjects.BotObject.INPUT_OBJECT;
import static com.danesfeder.mia.chat.dataobjects.BotObject.RESPONSE_OBJECT;

public class BotAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private BotList botList;

    public BotAdapter(BotList botList) {
        this.botList = botList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new layout inflater for views
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View itemView;

        switch (viewType) {
            case INPUT_OBJECT:
                itemView = li.inflate(R.layout.user_input_layout, parent, false);
                return new UserInputViewHolder(itemView);
            case RESPONSE_OBJECT:
                itemView = li.inflate(R.layout.bot_response_layout, parent, false);
                return new BotResponseViewHolder(itemView);
            default:
                itemView = li.inflate(R.layout.bot_response_layout, parent, false);
                return new BotResponseViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindView(botList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.botList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return botList.get(position).getViewType();
    }
}
