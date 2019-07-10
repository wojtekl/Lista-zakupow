package com.gmail.lesniakwojciech.listazakupowa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static String readFromStream(final InputStream inputStream, final int from) throws IOException {
        inputStream.skip(from);
        return new String(readFromStream(inputStream));
    }

    public static byte[] readFromStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final int BUFFOR_SIZE = 4096;
        final byte []buffor = new byte[BUFFOR_SIZE];
        for(int d = inputStream.read(buffor); -1 < d; d = inputStream.read(buffor)){
            baos.write(buffor, 0, d);
        }

        return baos.toByteArray();
    }
}
