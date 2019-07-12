package com.gmail.lesniakwojciech.listazakupowa;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Locale;

public class AsyncTaskRzadanie extends AsyncTask<String, Integer, AsyncTaskRzadanie.RzadanieResponse> {
    public static final String POST = "POST";
    private static final int OK = 200;
    private final Listener listener;

    public AsyncTaskRzadanie(final Listener listener) {
        this.listener = listener;
    }

    @Override
    protected RzadanieResponse doInBackground(final String... strings) {
        try {
            final HttpURLConnection httpURLConnection
                    = (HttpURLConnection) new java.net.URL(strings[0]).openConnection();
            httpURLConnection.addRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
            if (strings.length > 1 && !TextUtils.isEmpty(strings[1])) {
                httpURLConnection.setRequestMethod(strings[1]);
            }
            if (strings.length > 2 && !TextUtils.isEmpty(strings[2])) {
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                final OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(strings[2].getBytes());
                outputStream.close();
            }
            final RzadanieResponse response = new RzadanieResponse();
            response.setCode(httpURLConnection.getResponseCode());
            if (OK == response.getCode()) {
                final InputStream inputStream = httpURLConnection.getInputStream();
                response.setMessage(Utils.readFromStream(inputStream, 0));
                inputStream.close();
            } else {
                final InputStream inputStream = httpURLConnection.getErrorStream();
                response.setMessage(Utils.readFromStream(inputStream, 0));
                inputStream.close();
            }
            httpURLConnection.disconnect();
            return response;
        } catch (final Exception exception) {
            final RzadanieResponse response = new RzadanieResponse();
            response.setCode(-1);
            response.setMessage(exception.getLocalizedMessage());
            return response;
        }
    }

    @Override
    protected void onPostExecute(final RzadanieResponse response) {
        listener.onPostExecute(response);
    }

    protected interface Listener {
        void onPostExecute(final RzadanieResponse response);
    }

    protected class RzadanieResponse {
        private int code;
        private String message;

        public int getCode() {
            return this.code;
        }

        public void setCode(final int code) {
            this.code = code;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public boolean isOK(final boolean checkCode) {
            boolean status = !TextUtils.isEmpty(message);
            if (checkCode) {
                status |= OK == this.code;
            }
            return status;
        }
    }
}
