package com.ankamagames.dofus.network.utils.types;

public class BooleanByteWrapper {
	
    public static byte setFlag(byte flag, byte offset, boolean value) throws Exception
    {
        if (offset >= 8)
            throw new Exception("offset must be lesser than 8");

        return value ? (byte) (flag | (1 << offset)) : (byte) (flag & (255 - (1 << offset)));
    }

    public static byte setFlag(int flag, byte offset, boolean value) throws Exception
    {
        if (offset >= 8)
            throw new Exception("offset must be lesser than 8");

        return value ? (byte) (flag | (1 << offset)) : (byte) (flag & (255 - (1 << offset)));
    }

    public static boolean getFlag(byte flag, byte offset) throws Exception
    {
        if (offset >= 8)
            throw new Exception("offset must be lesser than 8");

        return (flag & (byte) (1 << offset)) != 0;
    }

}
