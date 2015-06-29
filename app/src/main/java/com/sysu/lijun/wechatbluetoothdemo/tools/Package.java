package com.sysu.lijun.wechatbluetoothdemo.tools;

import android.util.Log;

/**
 * Created by lijun on 15/6/19.
 */
public class Package {

    private char bMagicNum = 0xFE;
    private char bVer = 1;
    private short nLength;
    private short nCmdId;
    private short nSeq;

    private static Package mPackageInstance;

    private static Builder builder;

    public static class Builder{
        private Builder(){
            this.mPackage = Package.getInstance();
        }
        private Package mPackage;

        public Builder setLength(short length){
            mPackage.setnLength(length);
            return this;
        }
        public Builder setCmdId(short cmdId){
            mPackage.setnCmdId(cmdId);
            return this;
        }
        public Builder setSeq(short seq){
            mPackage.setnSeq(seq);
            return this;
        }
        public byte[] build(){
            if (mPackage.hasEmptyField()){
                Log.w("Package","check your package field...");
            }
            byte[] headFix = {(byte) mPackage.bMagicNum,
                    (byte) mPackage.bVer,
                    Utility.short2Byte(mPackage.nLength)[0],
                    Utility.short2Byte(mPackage.nLength)[1],
                    Utility.short2Byte(mPackage.nCmdId)[0],
                    Utility.short2Byte(mPackage.nCmdId)[1],
                    Utility.short2Byte(mPackage.nSeq)[0],
                    Utility.short2Byte(mPackage.nSeq)[1] };
            return headFix;
        }
    }

    private static synchronized Package getInstance() {

        if (mPackageInstance == null){
            mPackageInstance = new Package();
        }
        return mPackageInstance;
    }

    private Package(){
        this.bMagicNum = 0xFE;
        this.bVer = 1;
        this.nCmdId = 0;
        this.nLength = 0;
        this.nSeq = 0;
    }

    private boolean hasEmptyField() {
        if (this.nCmdId == 0 || this.nLength == 0){
            return true;
        }
        return false;
    }

    public static synchronized Builder newBuilder(){
        if (builder == null){
            builder = new Builder();
        }
        return builder;
    }

    private void setnLength(short length){
        this.nLength = length;
    }
    private void setnCmdId(short cmdId){
        this.nCmdId = cmdId;
    }
    private void setnSeq(short seq){
        this.nSeq = seq;
    }

}
