package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.support.v4.content.FileProvider.getUriForFile;

public class UkrytaWiadomosc {
    private static final String PODPIS = "com.gmail.lesniakwojciech.listazakupowa";
    private static final int PODPIS_DLUGOSC = PODPIS.length();
    private static final int IKONA_ROZMIAR = 3331;

    public UkrytaWiadomosc()
    {
        this.tresc = "";
        this.data = 0;
    }

    private String tresc;

    public String getTresc()
    {
        return this.tresc;
    }

    public void setTresc(final String tresc)
    {
        this.tresc = tresc;
    }

    private long data;

    public long getData()
    {
        return this.data;
    }

    public void setData(final long data)
    {
        this.data = data;
    }

    public boolean odczytaj(final Context context, final Uri uri)
    {
        String string = "";
        try {
            string = Utils.readFromStream(context.getContentResolver().openInputStream(uri),
                    IKONA_ROZMIAR);
        }
        catch (final IOException exception) {
            Toast.makeText(context, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(string) || !PODPIS.equals(string.substring(0, PODPIS_DLUGOSC)))
        {
            return false;
        }

        final int dataPoczatek = PODPIS_DLUGOSC + 1;
        final int trescPoczatek = string.indexOf(";", dataPoczatek) + 1;

        data = Long.parseLong(string.substring(dataPoczatek, trescPoczatek - 1));
        tresc = string.substring(trescPoczatek);

        return true;
    }

    public Uri przygotuj(final Context context)
    {
        final byte wiadomosc[] = new StringBuilder()
                .append(PODPIS)
                .append(";")
                .append(Calendar.getInstance().getTimeInMillis())
                .append(";")
                .append(tresc)
                .toString()
                .getBytes();

        final byte ikona[] = Utils.readFromStream(context.getResources()
                .openRawResource(R.raw.ic_launcher));
        try {
            final File file = File.createTempFile(
                    context.getString(R.string.nazwaPliku),
                    ".jpg",
                    context.getCacheDir());
            final FileOutputStream fos = new FileOutputStream(file.getPath());
            fos.write(ikona);
            fos.write(wiadomosc);
            fos.close();
            file.deleteOnExit();
            return getUriForFile(context, context.getString(R.string.FILEPROVIDER), file);
        }
        catch(final IOException exception){
        }

        return null;
    }
}
