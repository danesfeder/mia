package com.danesfeder.mia.chatview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chatview.dataobjects.ChatList;
import com.danesfeder.mia.chatview.viewholders.BaseViewHolder;
import com.danesfeder.mia.chatview.viewholders.BotResponseViewHolder;
import com.danesfeder.mia.chatview.viewholders.UserInputViewHolder;

import static com.danesfeder.mia.chatview.dataobjects.BotObject.INPUT_OBJECT;
import static com.danesfeder.mia.chatview.dataobjects.BotObject.RESPONSE_OBJECT;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ChatList chatList;

    public ChatAdapter(ChatList chatList) {
        this.chatList = chatList;
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
        holder.onBindView(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }
}
