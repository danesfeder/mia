package com.danesfeder.mia.mia;

import android.os.AsyncTask;
import android.util.Log;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

class MiaServices {

    private final String LOG_TAG = MiaServices.class.getSimpleName();

    private AIDataService dataService;

    MiaServices(AIDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Called each time text is submitted from the EditText in the chat view
     * @param queryText - String input from EditText
     */
    void query(String queryText, final MiaContract.Presenter presenter) {

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(queryText);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                try {
                    return dataService.request(aiRequest);
                } catch (AIServiceException e) {
                    Log.e(LOG_TAG, e.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                presenter.onQueryResponse(aiResponse);
            }
        }.execute(aiRequest);
    }
}
