package com.gmail.lesniakwojciech.listazakupowa;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AsyncTaskRzadanie extends AsyncTask<String, Integer, String> {
    private Gotowe gotowe;

    public AsyncTaskRzadanie(final Gotowe gotowe) {
        this.gotowe = gotowe;
    }

    @Override
    protected String doInBackground(final String... strings) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            final InputStream inputStream = new java.net.URL(strings[0]).openStream();
            final int BUFFOR_SIZE = 4096;
            final byte buffor[] = new byte[BUFFOR_SIZE];
            for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)) {
                baos.write(buffor);
            }
        }
        catch(final Exception exception) {}

        return baos.toString();
    }

    @Override
    protected void onPostExecute(final String odpowiedz) {
        gotowe.wykonaj(odpowiedz);
    }

    protected interface Gotowe {
        void wykonaj(final String odpowiedz);
    }
}
