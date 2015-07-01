package com.sysu.lijun.wechatbluetoothdemo.tools;

/**
 * Created by lijun on 15/6/19.
 */
public class DecodeProtoPack {
    public static final int HEAD_LEN = 8;

//    public DecodeProtoPack()
//    {
//
//    }

    public static int getCmdId(byte[] paramArrayOfByte)
    {
        int i = -1;
        if ((paramArrayOfByte[0] == -2) && (paramArrayOfByte[1] == 1))
            i = (paramArrayOfByte[4] << 8) + paramArrayOfByte[5];
        return i;
    }

    public static int getSeqId(byte[] paramArrayOfByte)
    {
        int i = -1;
        if ((paramArrayOfByte[0] == -2) && (paramArrayOfByte[1] == 1))
            i = (paramArrayOfByte[6] << 8) + paramArrayOfByte[7];
        return i;
    }

    public static int getUnsignedShort(short paramShort)
    {
        return 0xFFFF & paramShort;
    }

    private int min(int paramInt1, int paramInt2)
    {
        if (paramInt1 < paramInt2)
            return paramInt1;
        return paramInt2;
    }

    public static byte[] getProtoPackBody(byte[] paramArrayOfByte){

        byte[] body = new byte[paramArrayOfByte.length - 14];
        System.arraycopy(paramArrayOfByte, 12, body, 0, paramArrayOfByte.length-14);

        return body;

    }

}
