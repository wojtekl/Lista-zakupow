package com.gmail.lesniakwojciech.listazakupowa;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    public static String readFromStream(final InputStream inputStream, final int from)
    {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStream.skip(from);
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            for(String s = br.readLine(); s != null; s = br.readLine())
            {
                stringBuilder.append(s);
            }
        }
        catch(final IOException exception){
        }

        return stringBuilder.toString();
    }

    public static byte[] readFromStream(final InputStream inputStream)
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final int BUFFOR_SIZE = 4096;
        final byte []buffor = new byte[BUFFOR_SIZE];
        try{
            for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)){
                baos.write(buffor, 0, d);
            }
        }
        catch(final IOException exception){
        }

        return baos.toByteArray();
    }
}
