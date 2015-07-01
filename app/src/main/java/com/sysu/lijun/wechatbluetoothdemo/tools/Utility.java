package com.sysu.lijun.wechatbluetoothdemo.tools;

/**
 * Created by lijun on 15/6/19.
 */

import android.util.Log;

import com.google.protobuf.ByteString;

/**
 * 各基础类型与byte之间的转换
 * @author shanl
 *
 */
public class Utility {

    /**
     * 将short转成byte[2]
     * @param a
     * @return
     */
    public static byte[] short2Byte(short a){
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }

    /**
     * 将short转成byte[2]
     * @param a
     * @param b
     * @param offset b中的偏移量
     */
    public static void short2Byte(short a, byte[] b, int offset){
        b[offset] = (byte) (a >> 8);
        b[offset+1] = (byte) (a);
    }

    /**
     * 将byte[2]转换成short
     * @param b
     * @return
     */
    public static short byte2Short(byte[] b){
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

    /**
     * 将byte[2]转换成short
     * @param b
     * @param offset
     * @return
     */
    public static short byte2Short(byte[] b, int offset){
        return (short) (((b[offset] & 0xff) << 8) | (b[offset+1] & 0xff));
    }

    /**
     * long转byte[8]
     *
     * @param a
     * @param b
     * @param offset
     *            b的偏移量
     */
    public static void long2Byte(long a, byte[] b, int offset) {
        b[offset + 0] = (byte) (a >> 56);
        b[offset + 1] = (byte) (a >> 48);
        b[offset + 2] = (byte) (a >> 40);
        b[offset + 3] = (byte) (a >> 32);

        b[offset + 4] = (byte) (a >> 24);
        b[offset + 5] = (byte) (a >> 16);
        b[offset + 6] = (byte) (a >> 8);
        b[offset + 7] = (byte) (a);
    }

    /**
     * byte[8]转long
     *
     * @param b
     * @param offset
     *            b的偏移量
     * @return
     */
    public static long byte2Long(byte[] b, int offset) {
        return ((((long) b[offset + 0] & 0xff) << 56)
                | (((long) b[offset + 1] & 0xff) << 48)
                | (((long) b[offset + 2] & 0xff) << 40)
                | (((long) b[offset + 3] & 0xff) << 32)

                | (((long) b[offset + 4] & 0xff) << 24)
                | (((long) b[offset + 5] & 0xff) << 16)
                | (((long) b[offset + 6] & 0xff) << 8)
                | (((long) b[offset + 7] & 0xff) << 0));
    }

    /**
     * byte[8]转long
     *
     * @param b
     * @return
     */
    public static long byte2Long(byte[] b) {
        return
                ((b[0]&0xff)<<56)|
                        ((b[1]&0xff)<<48)|
                        ((b[2]&0xff)<<40)|
                        ((b[3]&0xff)<<32)|

                        ((b[4]&0xff)<<24)|
                        ((b[5]&0xff)<<16)|
                        ((b[6]&0xff)<<8)|
                        (b[7]&0xff);
    }

    /**
     * long转byte[8]
     *
     * @param a
     * @return
     */
    public static byte[] long2Byte(long a) {
        byte[] b = new byte[4 * 2];

        b[0] = (byte) (a >> 56);
        b[1] = (byte) (a >> 48);
        b[2] = (byte) (a >> 40);
        b[3] = (byte) (a >> 32);

        b[4] = (byte) (a >> 24);
        b[5] = (byte) (a >> 16);
        b[6] = (byte) (a >> 8);
        b[7] = (byte) (a >> 0);

        return b;
    }

    /**
     * byte数组转int
     *
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        return ((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
                | ((b[2] & 0xff) << 8) | (b[3] & 0xff);
    }

    /**
     * byte数组转int
     *
     * @param b
     * @param offset
     * @return
     */
    public static int byte2Int(byte[] b, int offset) {
        return ((b[offset++] & 0xff) << 24) | ((b[offset++] & 0xff) << 16)
                | ((b[offset++] & 0xff) << 8) | (b[offset++] & 0xff);
    }

    /**
     * int转byte数组
     *
     * @param a
     * @return
     */
    public static byte[] int2Byte(int a) {
        byte[] b = new byte[4];
        b[0] = (byte) (a >> 24);
        b[1] = (byte) (a >> 16);
        b[2] = (byte) (a >> 8);
        b[3] = (byte) (a);

        return b;
    }

    /**
     * int转byte数组
     *
     * @param a
     * @param b
     * @param offset
     * @return
     */
    public static void int2Byte(int a, byte[] b, int offset) {
        b[offset++] = (byte) (a >> 24);
        b[offset++] = (byte) (a >> 16);
        b[offset++] = (byte) (a >> 8);
        b[offset++] = (byte) (a);
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static String byte2String(byte[] paramArrayOfByte){
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)){
            Log.e("Util", "Byte array is null or empty...");
            return "";
        }

        StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length);

        for (int i = 0; ; i++){
            if (i >= paramArrayOfByte.length)
                return localStringBuilder.toString();
            localStringBuilder.append((char)paramArrayOfByte[i]);
        }
    }

    public static String byteArray2HexString(byte[] paramArrayOfByte, int paramInt){
        int i = paramInt;
        StringBuilder localStringBuilder = new StringBuilder(paramInt);
        if (paramArrayOfByte.length < paramInt){
            i = paramArrayOfByte.length;
        }
        for (int j = 0; ;j++){
            if (j >= i){
                return localStringBuilder.toString();
            }
            Object[] arrayOfObject = new Object[1];

            arrayOfObject[0] = Byte.valueOf(paramArrayOfByte[j]);

            localStringBuilder.append(String.format("%02X", arrayOfObject));

        }
    }

    public static String byteArray2String(byte[] paramArrayOfByte, int paramInt){
        int i = paramInt;
        StringBuilder localStringBuilder = new StringBuilder(paramInt);
        if (paramArrayOfByte.length < paramInt){
            i = paramArrayOfByte.length;
        }
        for (int j = 0; ;j++){
            if (j >= i){
                return localStringBuilder.toString();
            }
            Object[] arrayOfObject = new Object[1];

            arrayOfObject[0] = Byte.valueOf(paramArrayOfByte[j]);

            localStringBuilder.append(String.format("%02x", arrayOfObject));

        }
    }

    public static String ByteString2HexString(ByteString paramByteString){
        byte[] arrayOfByte = ByteString2byteArray(paramByteString);
        if (arrayOfByte == null)
            return null;
        return byteArray2HexString(arrayOfByte, arrayOfByte.length);
    }

    private static byte[] ByteString2byteArray(ByteString paramByteString) {
        int i = paramByteString.size();
        if (i == 0)
            return null;
        byte[] arrayOfByte = new byte[i];
        paramByteString.copyTo(arrayOfByte,0,0,i);
        return arrayOfByte;
    }

}
