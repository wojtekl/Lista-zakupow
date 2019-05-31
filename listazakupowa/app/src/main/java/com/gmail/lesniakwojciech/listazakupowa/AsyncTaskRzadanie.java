package com.gmail.lesniakwojciech.listazakupowa;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class AsyncTaskRzadanie extends AsyncTask<String, Integer, String> {
    private Gotowe gotowe;

    public AsyncTaskRzadanie(final Gotowe gotowe) {
        this.gotowe = gotowe;
    }

    @Override
    protected String doInBackground(final String... strings) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new java.net.URL(strings[0]).openConnection();

            final InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder(inputStream.available());
            String line;
            while(null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString();
            /* final int BUFFOR_SIZE = 4096;
            final byte buffor[] = new byte[BUFFOR_SIZE];
            for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)) {
                baos.write(buffor);
            } */
        }
        catch(final Exception exception) {
            return exception.getLocalizedMessage();
        }

        //return baos.toString();
    }

    @Override
    protected void onPostExecute(final String odpowiedz) {
        gotowe.wykonaj(odpowiedz);
    }

    protected interface Gotowe {
        void wykonaj(final String odpowiedz);
    }
}
