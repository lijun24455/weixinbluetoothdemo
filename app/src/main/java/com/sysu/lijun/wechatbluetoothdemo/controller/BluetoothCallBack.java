package com.sysu.lijun.wechatbluetoothdemo.controller;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sysu.lijun.wechatbluetoothdemo.tools.DecodeProtoPack;
import com.sysu.lijun.wechatbluetoothdemo.tools.MsgParam;

/**
 * BluetoothAdapter在收发数据时候与主界面UI交互的回调；
 * 持有与主界面绑定的Handler
 *
 * Created by lijun on 15/6/27.
 */
public class BluetoothCallBack {
    private Handler mHandler;

    public BluetoothCallBack(Handler paramHandler){
        if (paramHandler != null){
            this.mHandler = paramHandler;
        }
    }

    public void onListenning(){
        sendMessage(MsgParam.MSG_WHAT_LISTENNING, null, 0, 0);
    }

    public void onAuth(){
        sendMessage(MsgParam.MSG_WHAT_AUTH, null, 0, 0);
    }

    public void onInit(){
        sendMessage(MsgParam.MSG_WHAT_INIT,null, 0, 0);
    }

    public void onConnected(){
        sendMessage(MsgParam.MSG_WHAT_CONNECTED, null, 0, 0);
    }

    public void sendMessage(int paramWhat, Object paramObject, int paramInt1, int paramInt2){
        this.mHandler.sendMessage(Message.obtain(this.mHandler, paramWhat, paramInt1, paramInt2, paramObject));
    }


    public void handle(byte[] arrayOfByte) {
        byte[] message = new byte[arrayOfByte.length];
        System.arraycopy(arrayOfByte, 0, message, 0, arrayOfByte.length);
//        byte[] messageBody = DecodeProtoPack.getProtoPackBody(message);
        int mCmdId = DecodeProtoPack.getCmdId(message);
        int mSeqId = DecodeProtoPack.getSeqId(message);
        Log.i("HANDLE","收到信息:"+mCmdId);

        sendMessage(MsgParam.MSG_WHAT_RECV_DATA, message, mCmdId, mSeqId);
//        this.mHandler.obtainMessage(MsgParam.MSG_WHAT_RECV_DATA, mCmdId, mSeqId, arrayOfByte).sendToTarget();
    }

    public void onReset(){
        sendMessage(MsgParam.MSG_WHAT_RESET, null, 0, 0);
    }


}
