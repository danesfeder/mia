package com.danesfeder.mia.chat.dataobjects;

import android.support.annotation.NonNull;

public class ResponseObject extends BotObject {

    @Override
    public void setText(@NonNull String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getViewType() {
        return RESPONSE_OBJECT;
    }
}
