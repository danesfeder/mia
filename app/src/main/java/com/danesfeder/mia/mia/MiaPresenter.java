package com.danesfeder.mia.mia;

import android.util.Log;

import com.danesfeder.mia.chatview.dataobjects.ChatList;
import com.danesfeder.mia.chatview.dataobjects.InputObject;
import com.danesfeder.mia.chatview.dataobjects.ResponseObject;
import com.google.gson.JsonElement;

import java.util.Map;

import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import ai.api.model.Result;

class MiaPresenter implements MiaContract.Presenter {

    private final String LOG_TAG = MiaPresenter.class.getSimpleName();

    private MiaContract.View view;
    private MiaServices mia;
    private ChatList chatList;

    MiaPresenter(MiaServices mia, ChatList chatList) {
        this.mia = mia;
        this.chatList = chatList;
    }

    @Override
    public void attachView(MiaContract.View view) {
        this.view = view;
    }

    @Override
    public void sendQueryRequest(String queryText) {
        InputObject inputObject = new InputObject();
        inputObject.setText(queryText);
        chatList.add(inputObject);
        view.notifyAdapterItemInserted(chatList.size() - 1);

        // Send the request
        this.mia.query(queryText, this);
    }

    @Override
    public void addMiaGreeting() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setText("Hi, I'm MIA!  How can I help you?");

        chatList.add(responseObject);
        view.notifyAdapterItemInserted(chatList.size() - 1);
    }

    @Override
    public void onQueryResponse(AIResponse response) {
        if (response != null) {
            Result result = response.getResult();
            Fulfillment fulfillment = response.getResult().getFulfillment();
            // Get parameters
            String parameterString = "";
            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                }
            }
            String logText = "Query:" + result.getResolvedQuery() +
                    "\nAction: " + result.getAction() +
                    "\nParameters: " + parameterString;
            Log.i(LOG_TAG, logText);

            ResponseObject responseObject = new ResponseObject();
            responseObject.setText(fulfillment.getSpeech());

            chatList.add(responseObject);
            view.notifyAdapterItemInserted(chatList.size() - 1);
            view.scrollToBottom();

            if (!result.isActionIncomplete()) {
                String origin = "";
                String destination = "";
                if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                        if (entry.getKey().equals("origin")) {
                            origin = entry.getValue().toString();
                        } else if (entry.getKey().equals("destination")) {
                            destination = entry.getValue().toString();
                        }
                    }
                }

                hideChat();
            }
        }
    }

    private void hideChat() {
        view.hideKeyboard();
        view.hideChatView();
        view.showSearchBtn();
    }
}
