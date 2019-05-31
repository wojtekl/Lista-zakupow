package com.gmail.lesniakwojciech.listazakupowa;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class AsyncTaskRzadanie extends AsyncTask<String, Integer, String> {
    private final Gotowe gotowe;

    public AsyncTaskRzadanie(final Gotowe gotowe) {
        this.gotowe = gotowe;
    }

    @Override
    protected String doInBackground(final String... strings) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            final HttpURLConnection httpURLConnection
                    = (HttpURLConnection) new java.net.URL(strings[0]).openConnection();
            final InputStream inputStream = httpURLConnection.getInputStream();
            final int BUFFOR_SIZE = 4096;
            final byte []buffor = new byte[BUFFOR_SIZE];
            for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)) {
                baos.write(buffor, 0, d);
            }
            inputStream.close();
            httpURLConnection.disconnect();
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
