package com.sysu.lijun.wechatbluetoothdemo.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.sysu.lijun.wechatbluetoothdemo.tools.*;
import com.sysu.lijun.wechatbluetoothdemo.tools.Package;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import proto.MmBp;

import static com.sysu.lijun.wechatbluetoothdemo.tools.Utility.byteMerger;

/**
 * Created by lijun on 15/6/27.
 */
public class BCController {

    private static final UUID MY_UUID_INSECURE;

    public static final int STATE_INIT_RESP = 7;
    public static final int STATE_INIT_REQ = 6;
    public static final int STATE_AUTH_RESP = 5;
    public static final int STATE_AUTH_REQ = 4;
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
        this.mMacAddressByteSring = getBluetoothMacBytes(mBluetoorhAdapter.getAddress());

    }

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
        this.mConnectThread.cancel();
    }

    public void setState(int state) {
        this.mState = state;
    }
    public int getState(){
        return this.mState;
    }

    private class ConnectThread extends Thread{
        private final BluetoothAdapter mBluetothAdapter;
        private final BluetoothServerSocket mServerSocket;
        private BluetoothSocket mSocket;
        private volatile boolean mIsConnected;
        private BluetoothServerSocket tmp;

        public ConnectThread(BluetoothAdapter paraAdapter){
            this.mBluetothAdapter = paraAdapter;
            this.mIsConnected = false;
            try {
                tmp = mBluetothAdapter.listenUsingInsecureRfcommWithServiceRecord("Weixin",MY_UUID_INSECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
           this.mServerSocket = tmp;
        }

        public void cancel(){
            out("ConnectThread------->call cancel!");
            BCController.this.stopWorkThread();

            if (!this.mIsConnected){
                out("ConnectThread------>Cancel is done already...");
            }
            while (true){
                this.mIsConnected = false;
                interrupt();
                String str = "stop thread:" + getName() + "success.";

                try {
                    this.mServerSocket.close();
                    out(str);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void run() {
            setName("ConnectThread");

            while (true){
                try {
                    this.mSocket = mServerSocket.accept();

                    if (this.mSocket!=null){

                        this.mIsConnected = true;
                        BCController.this.startWorkThread(this.mSocket);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    this.mServerSocket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

     void startWorkThread(BluetoothSocket mSocket) {
        if (this.mWorkThread != null){
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        this.mWorkThread = new WorkThread(mSocket);
        this.mWorkThread.start();

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

    private class WorkThread extends Thread{
        private static final int MAX_BUFFER_SIZE = 2048;
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
        }

        public void cancel(){
            if (this.mIsCancel){
                out("WorkThread is done already.");
                return;
            }

            this.mIsCancel = true;
            interrupt();

            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[2048];

            while (true){
                if (this.mIsCancel){
                    out("Cancel is called while receiving data...");
                    return;
                }
                try {
                    int i = mInputStream.read(buffer);
                    if (i <= 0){
                        continue;
                    }

                    byte[] arrayOfByte = new byte[i];
                    System.arraycopy(buffer, 0, arrayOfByte, 0, i);
                    setDataRecSeq(getDataRecSeq()+1);


//                    mHandler.obtainMessage(MESSAGE_READ, i, -1, arrayOfByte).sendToTarget();
                    mBluetoothCallback.handle(arrayOfByte);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public boolean write(byte[] paraArrayOfByte){
            try {
                this.mOutputStream.write(paraArrayOfByte);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean startAuthReq() {

        Log.i("Blue", "push auth msg----------->");

        MmBp.BaseRequest.Builder baseBuilder = MmBp.BaseRequest.newBuilder();
        MmBp.BaseRequest baseRequest = baseBuilder.build();

        MmBp.AuthRequest.Builder builder = MmBp.AuthRequest.newBuilder();
        builder.setBaseRequest(baseRequest)
                .setProtoVersion(0x010203)
                .setMacAddress(ByteString.copyFrom(getBluetoothMacBytes(this.mBluetoorhAdapter.getAddress())))
                .setAuthProto(1)
                .setAuthMethod(MmBp.EmAuthMethod.EAM_macNoEncrypt);
        MmBp.AuthRequest authRequest = builder.build();
        byte[] bPackBody = authRequest.toByteArray();


        com.sysu.lijun.wechatbluetoothdemo.tools.Package.Builder headBuild = Package.newBuilder();
        headBuild.setCmdId((short) MmBp.EmCmdId.ECI_req_auth_VALUE)
                .setLength((short) (bPackBody.length + 8))
                .setSeq((short) (1));
        byte[] bPackHead = headBuild.build();

        mBluetoothCallback.onAuth();
        if (this.mWorkThread == null){
            return false;
        }
        if(this.mWorkThread.write(byteMerger(bPackHead, bPackBody))){
            this.setState(STATE_AUTH_REQ);
            return true;
        }
        return false;
    }

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
        return false;
    }

    public void out(String paraString){
        Log.i("BCController", paraString);
    }
}
