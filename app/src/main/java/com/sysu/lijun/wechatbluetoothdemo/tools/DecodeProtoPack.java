package com.sysu.lijun.wechatbluetoothdemo.tools;

import android.util.Log;

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

        //14 = 12 + 2;
        String byteHexStr = Utility.byteArray2HexString(paramArrayOfByte, paramArrayOfByte.length);
        int frontFix = byteHexStr.indexOf("FECF")/2;
        int allFix = frontFix + 2;
        Log.i("GETBODY","--------->allFix:"+allFix);
        byte[] body = new byte[paramArrayOfByte.length - allFix];
        System.arraycopy(paramArrayOfByte, frontFix, body, 0, paramArrayOfByte.length-allFix);
        return body;

    }

}
