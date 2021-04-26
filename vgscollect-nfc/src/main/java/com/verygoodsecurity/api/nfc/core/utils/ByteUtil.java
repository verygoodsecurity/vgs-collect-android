package com.verygoodsecurity.api.nfc.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteUtil {

    public static boolean matchBitByBitIndex(int pVal, int pBitIndex) {
        if(pBitIndex >= 0 && pBitIndex <= 31) {
            return (pVal & 1 << pBitIndex) != 0;
        } else {
            throw new IllegalArgumentException("wrong parameter");
        }
    }

    public static byte[] fromString(String pData) {
        if(pData == null) {
            throw new IllegalArgumentException("Argument can\'t be null");
        } else {
            String text = pData.replace(" ", "");
            if(text.length() % 2 != 0) {
                throw new IllegalArgumentException("Hex binary needs to be even-length :" + pData);
            } else {
                byte[] commandByte = new byte[Math.round((float)text.length() / 2.0F)];
                int j = 0;

                for(int i = 0; i < text.length(); i += 2) {
                    Integer val = Integer.valueOf(Integer.parseInt(text.substring(i, i + 2), 16));
                    commandByte[j++] = val.byteValue();
                }

                return commandByte;
            }
        }
    }
    public static byte[] readTagIdBytes(final ByteArrayInputStream stream) {
        ByteArrayOutputStream tagBAOS = new ByteArrayOutputStream();
        byte tagFirstOctet = (byte) stream.read();
        tagBAOS.write(tagFirstOctet);

        byte MASK = (byte) 0x1F;
        if ((tagFirstOctet & MASK) == MASK) {
            do {
                int nextOctet = stream.read();
                if (nextOctet < 0) {
                    break;
                }
                byte tlvIdNextOctet = (byte) nextOctet;

                tagBAOS.write(tlvIdNextOctet);

                if (!ByteUtil.matchBitByBitIndex(tlvIdNextOctet, 7) || ByteUtil.matchBitByBitIndex(tlvIdNextOctet, 7)
                        && (tlvIdNextOctet & 0x7f) == 0) {
                    break;
                }
            } while (true);
        }
        return tagBAOS.toByteArray();
    }
}
