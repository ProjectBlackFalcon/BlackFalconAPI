package com.ankamagames.dofus.util;

import java.io.ByteArrayOutputStream;

import com.ankamagames.dofus.network.utils.DofusDataWriter;

public class NetworkUtils {

    private static int InstanceId = 0;

    public static byte[] writePacket(DofusDataWriter writer, ByteArrayOutputStream bous, int id) throws Exception {
        byte[] data = bous.toByteArray();
        writer.Clear();
        byte num = computeTypeLen(data.length);
        int num1 = subComputeStaticHeader(id, num);
        writer.writeShort((short) num1);
        writer.writeInt(InstanceId++);
        switch (num) {
            case 0:
                break;
            case 1:
                writer.writeByte((byte) data.length);
                break;
            case 2:
                writer.writeShort((short) data.length);
                break;
            case 3:
                writer.writeByte((byte) ((data.length >> 16) & 255));
                writer.writeShort((short) (data.length & 65535));
                break;
            default:
                throw new Exception("Packet's length can't be encoded on 4 or more bytes");
        }
        writer.write(data);
        return writer.bous.toByteArray();
    }

    private static byte computeTypeLen(int param1) {
        byte num;
        if (param1 > 65535)
            num = 3;
        else if (param1 <= 255)
            num = (byte) (param1 <= 0 ? 0 : 1);
        else
            num = 2;
        return num;
    }

    private static int subComputeStaticHeader(int id, byte typeLen) {
        return (id << 2) | typeLen;
    }

    public static String bytesToString(byte[] bytes, String format, boolean spacer) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(format, b));
            if (spacer) sb.append(" ");
        }
        return sb.toString();
    }
}
