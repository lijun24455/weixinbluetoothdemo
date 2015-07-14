package com.sysu.lijun.wechatbluetoothdemo.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.sysu.lijun.wechatbluetoothdemo.tools.*;
import com.sysu.lijun.wechatbluetoothdemo.tools.Package;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.cert.TrustAnchor;
import java.util.UUID;

import proto.MmBp;

import static com.sysu.lijun.wechatbluetoothdemo.tools.Utility.byteMerger;

/**
 * 对蓝牙适配器BluetoothAdapter的封装，其中主要包括适配器的配置，以及两个线程；
 * 一个是获得到Socket的线程：ConnectThread；
 * 一个是持有这个Socket并负责数据读写的线程：WorkThread；
 *
 * Created by lijun on 15/6/27.
 */
public class BCController {

    private static final UUID MY_UUID_INSECURE;

    public static final int STATE_INIT_RESP = 8;
    public static final int STATE_INIT_REQ = 7;
    public static final int STATE_AUTH_RESP = 6;
    public static final int STATE_AUTH_REQ = 5;
    public static final int STATE_WORK_THREAD_OK = 4;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;

//    private static final String

    public static int getDataRecSeq() {
        return dataRecSeq;
    }

    public static void setDataRecSeq(int dataRecSeq) {
        BCController.dataRecSeq = dataRecSeq;
    }

    private static int dataRecSeq = 1;

    private static BCController instance;

    private BluetoothAdapter mBluetoorhAdapter;
    private ConnectThread mConnectThread;
    private WorkThread mWorkThread;

    private BluetoothCallBack mBluetoothCallback;

    private final byte[] mMacAddressByteSring;

    static {
        MY_UUID_INSECURE = UUID.fromString("e5b152ed-6b46-09e9-4678-665e9a972cbc");
    }

    private int mState;


    private BCController(Context paraContext, BluetoothCallBack paramCallBack){

        this.mConnectThread = null;
        this.mWorkThread = null;
        this.mState = STATE_LISTEN;
        this.mBluetoothCallback = paramCallBack;
        if (this.mBluetoothCallback!=null){
            this.mBluetoothCallback.onListenning();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager)paraContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoorhAdapter = bluetoothManager.getAdapter();
        if (!mBluetoorhAdapter.enable()){
            this.mBluetoorhAdapter.enable();
        }
        this.mMacAddressByteSring = getBluetoothMacBytes(mBluetoorhAdapter.getAddress());
    }

    /**
     * 获得BluetoothAdapter实例【单例】
     *
     * @param paraContext
     * @param paramCallBack 用于跟主界面Handler交互的回调函数
     * @return
     */
    public static BCController getInstance(Context paraContext, BluetoothCallBack paramCallBack){
        if (instance == null){
            instance = new BCController(paraContext, paramCallBack);
        }
        return instance;
    }

    public static void releaseInstance(){
        if (instance!=null)
            instance = null;
    }

    public static byte[] getBluetoothMacBytes(String MacAddress){
        String address[] = MacAddress.split(":");
        byte[] result = new byte[6];
        int position = 0;
        for(String frag:address){
            Short tmp = Short.valueOf(frag,16);
            byte[] btmp = Utility.short2Byte(tmp);
            result[position++] = btmp[1];
        }
        return result;
    }

    public void connect(){
        if (this.mBluetoorhAdapter == null){
            out("Bluetooth not init!");
            return;
        }
        setState(STATE_CONNECTING);
        if (this.mConnectThread != null){
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        this.mConnectThread = new ConnectThread(mBluetoorhAdapter);
        this.mConnectThread.start();
        this.dataRecSeq = 1;
    }

    public void disconnect(){
        out("------->call disconnect");
        setState(STATE_NONE);
        if (this.mConnectThread!=null){
            this.mConnectThread.cancel();
        }
    }

    public void setState(int state) {
        this.mState = state;
    }
    public int getState(){
        return this.mState;
    }


    /**
     * 连接线程，用于从RFCOMM连接中获得socket，然后创建WorkThread；
     */
    private class ConnectThread extends Thread{
        private final BluetoothAdapter mBluetothAdapter;
        private final BluetoothServerSocket mServerSocket;
        private BluetoothSocket mSocket;
        private volatile boolean mIsConnected;
        private BluetoothServerSocket tmp;
        private boolean mIsCancled;

        public ConnectThread(BluetoothAdapter paraAdapter){

            this.mBluetothAdapter = paraAdapter;
            this.mIsConnected = false;
            this.mIsCancled = false;
            try {
                tmp = mBluetothAdapter.listenUsingRfcommWithServiceRecord("Weixin",MY_UUID_INSECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
           this.mServerSocket = tmp;
        }

        public void cancel(){
            out("ConnectThread------->call cancel!");

            interrupt();

            Log.i("THREAD", "Connect Thread is Connected?--->" + this.mIsConnected);
            this.mIsCancled = true;
            if (this.mIsConnected){
                Log.i("THREAD", "Connect Thread is Canceled while Connected!");
                BCController.this.stopWorkThread();
                this.mIsConnected = false;
            }
//            interrupt();
            try {
                this.mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            setName("ConnectThread");
            Log.i("THREAD", "ConnectThread is running (out of while)");
//            Log.i("THREAD", "ConnectThread state is cancled? "+ this.mIsCancled);
            while (true) {
                try {
                    Log.i("THREAD", "ConnectThread is running (in while)");
                    this.mSocket = mServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                if (this.mSocket != null) {

                    Log.i("THREAD", "ConnectThread accept the Socket !");
                    this.mIsConnected = true;
                    BCController.this.startWorkThread(this.mSocket);
                    try {
                        this.mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 开启WorkThread
     * @param mSocket   从RFCOMM连接中获得的socket
     */
     void startWorkThread(BluetoothSocket mSocket) {
         Log.i("WORKTHREAD","START WORK THREAD...");
        if (this.mWorkThread != null){
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        this.mWorkThread = new WorkThread(mSocket);
        this.mWorkThread.start();

         Log.i("WORKTHREAD","WORKTHREAD IS STARTED,NOW SET STATE TO state_connected...");
         setState(STATE_CONNECTED);

     }

     void stopWorkThread(){
         if (this.mWorkThread != null){
             this.mWorkThread.cancel();
             this.mWorkThread = null;
         }
     }


    public boolean writeData(byte[] paramArrayOfByte){
        out("write data");
        WorkThread localWorkThread = this.mWorkThread;
        boolean bool = false;
        if (localWorkThread != null){
            bool = this.mWorkThread.write(paramArrayOfByte);
        }
        return bool;
    }

    /**
     * 工作线程，负责通过Socket读写数据
     */
    private class WorkThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        private volatile boolean mIsCancel;

        public WorkThread(BluetoothSocket socket){
            this.mSocket = socket;
            this.mIsCancel = false;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.mInputStream = tmpIn;
            this.mOutputStream = tmpOut;
            Log.i("WORKTHREAD","mSocket is "+mSocket.isConnected());
            setState(STATE_WORK_THREAD_OK);
        }

        public void cancel(){
//            interrupt();
            Log.i("WORKTHREAD", "cancel in WorkThread is called...");
            if (this.mIsCancel){
                Log.i("WORKTHREAD", "WorkThread is canceled already...");
                return;
            }
            interrupt();
            this.mIsCancel = true;
//            interrupt();
            try {
//                this.mSocket.close();
//                this.mInputStream.close();
//                this.mOutputStream.close();
                this.mSocket.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("IOWEX","close() of connect socket failed=====>" + e.toString());
            }

        }

        /**
         * 此处接收信息可优化，经测试，当有大量的数据传来的时候，微信会将这些数据分批发送，但此处read只读了一次；
         * 比如测试中发送来的3w多字节的的数据，这里分别收到了990长的byte[]若干组，
         *
         */
        @Override
        public void run() {
            mBluetoorhAdapter.cancelDiscovery();

            byte[] buffer = new byte[2048];
            while (true){
                Log.i("THREAD","WorkThread is running (in while)");
                if (this.mIsCancel){
                    out("Cancel is called while receiving data...");
                    break;
                }
                try {
                    int i = mInputStream.read(buffer);
                    Log.i("INPUTSTREAM", "收到信息长度：=========>" + i);
                    if (i <= 0){
                        continue;
                    }
                    byte[] arrayOfByte = new byte[i];
                    System.arraycopy(buffer, 0, arrayOfByte, 0, i);
                    setDataRecSeq(getDataRecSeq()+1);
                    mBluetoothCallback.handle(arrayOfByte);
                } catch (IOException e) {
                    Log.e("IOEWorkThreadIn", e.toString());
//                    mBluetoothCallback.onReset();
//                    cancel();
//                    reset();
//                    mBluetoothCallback.onReset();
                    break;
                }
            }
        }

        public boolean write(byte[] paraArrayOfByte){
            Log.i("WORKTHREAD", "now start writting data");
            try {
                if (this.mOutputStream == null){
                    return false;
                }
                this.mOutputStream.write(paraArrayOfByte);
                return true;
            } catch (IOException e) {
                Log.e("IOEWorkThreadOut", e.toString());
//                reset();
//                mBluetoothCallback.onReset();

            }
            return false;
        }
    }

    /**
     * 发送AuthRequest
     *
     * @return
     */
    public boolean startAuthReq() {

        Log.i("Blue", "push auth msg----------->");

        MmBp.BaseRequest.Builder baseBuilder = MmBp.BaseRequest.newBuilder();
        MmBp.BaseRequest baseRequest = baseBuilder.build();

        //组建AuthRequest包体
        MmBp.AuthRequest.Builder builder = MmBp.AuthRequest.newBuilder();
        builder.setBaseRequest(baseRequest)
                .setProtoVersion(0x010203)
                .setMacAddress(ByteString.copyFrom(getBluetoothMacBytes(this.mBluetoorhAdapter.getAddress())))
                .setAuthProto(1)
                .setAuthMethod(MmBp.EmAuthMethod.EAM_macNoEncrypt);
        MmBp.AuthRequest authRequest = builder.build();
        byte[] bPackBody = authRequest.toByteArray();

        //组建AuthRequest包头
        com.sysu.lijun.wechatbluetoothdemo.tools.Package.Builder headBuild = Package.newBuilder();
        headBuild.setCmdId((short) MmBp.EmCmdId.ECI_req_auth_VALUE)
                .setLength((short) (bPackBody.length + 8))
                .setSeq((short) (1));
        byte[] bPackHead = headBuild.build();

        mBluetoothCallback.onAuth();

        if (this.mWorkThread == null){
            return false;
        }

        Log.i("AUTH THREAD", "NOW START WRITE AUTH REQUEST...");
        if (this.mWorkThread.write(byteMerger(bPackHead, bPackBody))){
            this.setState(STATE_AUTH_REQ);
            return true;
        }

        return false;
    }

    /**
     * 发送InitRequest；
     * @param mSeq
     * @return
     */
    public boolean startInitReq(int mSeq){

        Log.i("Blue", "push auth msg----------->");

        MmBp.BaseRequest.Builder builder = MmBp.BaseRequest.newBuilder();
        MmBp.BaseRequest baseRequest = builder.build();
        MmBp.InitRequest.Builder builder1 = MmBp.InitRequest.newBuilder();
        builder1.setBaseRequest(baseRequest)
                .setChallenge(ByteString.copyFromUtf8("Challenge"));
        MmBp.InitRequest initRequest = builder1.build();
        byte[] bPackBody = initRequest.toByteArray();

        Package.Builder headBuild = Package.newBuilder();
        headBuild.setCmdId((short) MmBp.EmCmdId.ECI_req_init_VALUE)
                .setLength((short) (bPackBody.length + 8))
                .setSeq((short) ( mSeq + 1));
        byte[] bPackHead = headBuild.build();

        mBluetoothCallback.onInit();
        if (this.mWorkThread == null){
            return false;
        }

        if (this.mWorkThread.write(byteMerger(bPackHead, bPackBody))){
            this.setState(STATE_INIT_REQ);
            return true;
        }
//
        return false;
    }

    public void out(String paraString){
        Log.i("BCController", paraString);
    }

    public void reset(){
        mConnectThread.cancel();
    }
}
