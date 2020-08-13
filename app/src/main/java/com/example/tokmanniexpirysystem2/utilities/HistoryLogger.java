package com.example.tokmanniexpirysystem2.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.History;
import com.example.tokmanniexpirysystem2.entities.Product;
import com.example.tokmanniexpirysystem2.entities.User;

import java.util.Date;
import java.util.List;

public class HistoryLogger {

    public static void logEvent(Context context, Product product, UserAction action) {
        User user = Authintication.user;
        final History history = new History();
        history.setModifierId(user.getId());
        if(product == null) {
            history.setProductId(-100);
        } else {
            history.setProductId(product.getId());
        }

        history.setUserAction(action);
        history.setModificationDate(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getFirstName() + " ");
        stringBuilder.append(user.getLastName() + " ");
        stringBuilder.append(action.toString() + "ed ");
        if(product != null) {
            stringBuilder.append(product.getProductName() + " ");
        }
        stringBuilder.append("at " + history.getModificationDate());
        history.setSummary(stringBuilder.toString());

        class SaveHistory extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //adding to database
                DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                        .historyDao()
                        .insert(history);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        SaveHistory sh = new SaveHistory();
        sh.execute();
    }


    public static void clearHistory(Context context) {
        class GetHistory extends AsyncTask<Void, Void, List<History>> {

            @Override
            protected List<History> doInBackground(Void... voids) {
                List<History> historyList = DatabaseClient
                        .getInstance(context.getApplicationContext())
                        .getAppDatabase()
                        .historyDao()
                        .getAll();
                return historyList;
            }

            @Override
            protected void onPostExecute(List<History> history) {
                super.onPostExecute(history);
                trimHistory(context, history);
            }
        }

        GetHistory gh = new GetHistory();
        gh.execute();
    }

    public static void trimHistory(Context context, List<History> history) {
        class TrimHistory extends AsyncTask<List<History>, Void, Void> {

            @Override
            protected Void doInBackground(List<History>... history) {

                int size = history[0].size();
                int maxSize = 100000;
                if(size > maxSize) {
                    history[0].subList(0, size-maxSize).forEach(historyItem -> {
                        DatabaseClient
                                .getInstance(context.getApplicationContext())
                                .getAppDatabase()
                                .historyDao()
                                .delete(historyItem);
                    });
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        TrimHistory th = new TrimHistory();
        th.execute(history);
    }
}
