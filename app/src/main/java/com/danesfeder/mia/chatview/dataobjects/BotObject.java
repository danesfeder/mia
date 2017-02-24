package com.danesfeder.mia.chatview.dataobjects;

import android.support.annotation.NonNull;

public abstract class BotObject {

    public static final int INPUT_OBJECT = 0;
    public static final int RESPONSE_OBJECT = 1;

    protected String text;

    public abstract void setText(@NonNull String text);

    @NonNull
    public abstract String getText();

    public abstract int getViewType();
}
