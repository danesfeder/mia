package com.danesfeder.mia.chatview.viewholders;

import android.view.View;
import android.widget.TextView;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chatview.dataobjects.BotObject;

public class BotResponseViewHolder extends BaseViewHolder {

    private TextView tvResponseText;

    public BotResponseViewHolder(View itemView) {
        super(itemView);
        this.tvResponseText = (TextView) itemView.findViewById(R.id.tv_response_text);
    }

    @Override
    public void onBindView(BotObject object) {
        this.tvResponseText.setText(object.getText());
    }
}
