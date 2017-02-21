package com.danesfeder.mia.chat.viewholders;

import android.view.View;
import android.widget.TextView;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chat.dataobjects.BotObject;

public class UserInputViewHolder extends BaseViewHolder {

    private TextView tvInputText;

    public UserInputViewHolder(View itemView) {
        super(itemView);
        this.tvInputText = (TextView) itemView.findViewById(R.id.tv_input_text);
    }

    @Override
    public void onBindView(BotObject object) {
        this.tvInputText.setText(object.getText());
    }
}
