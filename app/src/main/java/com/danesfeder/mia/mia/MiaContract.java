package com.danesfeder.mia.mia;

import ai.api.model.AIResponse;

interface MiaContract {

    interface View {

        void notifyAdapterItemInserted(int position);

        void scrollToBottom();

        void hideChatView();

        void hideKeyboard();

        void showSearchBtn();
    }

    interface Presenter {

        void attachView(MiaContract.View view);

        void onQueryResponse(AIResponse response);

        void sendQueryRequest(String queryText);

        void addMiaGreeting();
    }
}
