package me.exrates.scheduleservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ZipUtil {

    public static byte[] zip(byte[] balancesBytes) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(balancesBytes);
        deflater.finish();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            baos.write(tmp, 0, size);
        }
        baos.close();
        return baos.toByteArray();
    }

    public static byte[] unzip(byte[] zippedBytes) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(zippedBytes);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4 * 1024];
        while (!inflater.finished()) {
            int size = inflater.inflate(tmp);
            baos.write(tmp, 0, size);
        }
        baos.close();
        return baos.toByteArray();
    }
}