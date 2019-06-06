package com.gmail.lesniakwojciech.listazakupowa;

import android.os.AsyncTask;
import android.util.Log;

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
            /*if(!TextUtils.isEmpty(strings[1])) {
                Log.d("ziutek", strings[1]);
                httpURLConnection.setRequestMethod(strings[1]);
            }
            if(!TextUtils.isEmpty(strings[2])) {
                Log.d("ziutek", strings[2]);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                final OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(strings[2].getBytes());
                outputStream.close();
            }*/
            final InputStream inputStream = httpURLConnection.getInputStream();
            final int BUFFOR_SIZE = 4096;
            final byte []buffor = new byte[BUFFOR_SIZE];
            for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)) {
                baos.write(buffor, 0, d);
            }
            inputStream.close();
            httpURLConnection.disconnect();
        }
        catch(final Exception exception) {
            Log.d("ziutek", exception.getLocalizedMessage());
        }

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
