package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.controller.BCController;
import com.sysu.lijun.wechatbluetoothdemo.json.UserInfo;
import com.sysu.lijun.wechatbluetoothdemo.proto.LJDevice;
import com.sysu.lijun.wechatbluetoothdemo.tools.DecodeProtoPack;
import com.sysu.lijun.wechatbluetoothdemo.tools.MsgParam;
import com.sysu.lijun.wechatbluetoothdemo.controller.BluetoothCallBack;
import com.sysu.lijun.wechatbluetoothdemo.tools.Utility;



import proto.MmBp;


public class ListenActivity extends Activity {

    private TextView textViewInfo = null;
    private Button mRESETButton = null;
//    private ImageView imageViewUniqle = null;

    private BluetoothCallBack mBluetoothCallback = null;
    private BCController mBCController = null;

    private FrontFragmet mFragmentFront;
    private UserInfoFragment mFragmentUserInfo;

    private BluetoothOnThread bluetoothOnThread;


    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MsgParam.MSG_WHAT_LISTENNING:
//                    textViewInfo.setText("正在等待连接...");
                    mFragmentFront.onInfoChanged("正在等待连接...");
                    break;
                case MsgParam.MSG_WHAT_AUTH:
//                    textViewInfo.setText("正在发送Auth Req...");
                    mFragmentFront.onInfoChanged("正在发送Auth Req...");
                    break;
                case MsgParam.MSG_WHAT_INIT:
//                    textViewInfo.setText("正在发送Init Req");
                    mFragmentFront.onInfoChanged("正在发送Init Req...");
                    break;
                case MsgParam.MSG_WHAT_CONNECTED:
//                    textViewInfo.setText("完成连接，正在等待数据传入...");
                    mFragmentFront.onInfoChanged("完成连接，正在等待数据传入...");
                    break;
                case MsgParam.MSG_WHAT_RECV_DATA:
                    switch (msg.arg1){
                        case MmBp.EmCmdId.ECI_resp_auth_VALUE:
                            if (mBCController.getState() != BCController.STATE_AUTH_REQ){
                                Log.i("HANDLE_MSG","now device's state is not AUTH REQ...");
                                resetState();
                                break;
                            }
                            mBCController.setState(BCController.STATE_AUTH_RESP);
                            mBCController.startInitReq(msg.arg2);
                            break;
                        case MmBp.EmCmdId.ECI_resp_init_VALUE:
                            if (mBCController.getState() != BCController.STATE_INIT_REQ){
                                Log.i("HANDLE_MSG","now device's state is not INIT REQ...");
                                resetState();
                                break;
                            }
                            mBCController.setState(BCController.STATE_INIT_RESP);
                            mBluetoothCallback.onConnected();
                            break;
                        case MmBp.EmCmdId.ECI_push_recvData_VALUE:
                            if (mBCController.getState() != BCController.STATE_INIT_RESP){
                                Log.i("HANDLE_MSG", "now device's state is not INIT RSP...");
                                resetState();
                                break;
                            }
                            handle(msg);
                            break;
                    }
                    break;
                default:
                    break;

            }
        }
    };

    private void handle(Message msg) {
        Object msgObj = msg.obj;

        String byteStr = Utility.byteArray2HexString((byte[])msgObj,((byte[])msgObj).length);
        Log.i("RECVDATA", "Utilty--full--->"+Utility.byteArray2HexString((byte[])msgObj,((byte[])msgObj).length));

        byte[] bodyContent = DecodeProtoPack.getProtoPackBody((byte[]) msgObj);

        String contentStr = Utility.byteArray2HexString(bodyContent,bodyContent.length);
        Log.i("RECVDATA", "Utilty---body-->"+contentStr);
        LJDevice ljDevice = LJDevice.parse(bodyContent);
        String dataStr = ljDevice.body;

        Log.i("RECV DATA:", dataStr);

        updateUI(dataStr);
    }

    private void updateUI(String dataStr) {

        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(dataStr, UserInfo.class);

        if (userInfo != null){
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("USER","----->"+userInfo.toString());
//            mFragmentUserInfo = UserInfoFragment.newInstance(userInfo);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.fl_fragment_container,mFragmentUserInfo)
//                    .commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        Initialize();

        bluetoothOnThread = new BluetoothOnThread(mBCController);
        bluetoothOnThread.start();


    }



    private void Initialize() {

//        textViewInfo = (TextView) findViewById(R.id.tv_info);
//        imageViewUniqle = (ImageView) findViewById(R.id.img_uniqle);
        mFragmentFront = FrontFragmet.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container,mFragmentFront)
                .commit();

        this.mBluetoothCallback = new BluetoothCallBack(this.mHandler);
        this.mBCController = BCController.getInstance(this, this.mBluetoothCallback);
        this.mRESETButton = (Button) findViewById(R.id.btn_reset);
        this.mRESETButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetState();
            }
        });
    }

    private class BluetoothOnThread extends Thread{

        private BCController mBCController;
        private boolean isCancel;

        public BluetoothOnThread(BCController mBCController){
            this.mBCController = mBCController;
            this.isCancel = false;
        }

        @Override
        public void run() {
            Log.i("BLUETOOTHTHREAD", "BLUETOOTHTHREAD is running.....");
            this.mBCController.connect();

            while (this.mBCController.getState() < BCController.STATE_CONNECTED){
                continue;
            }

            this.mBCController.startAuthReq();

        }

        private void reset(){
            this.mBCController.reset();
            if (isCancel){
                isCancel = false;
            }
        }

        private void cancel(){
            if (this.isCancel){
                Log.i("BLUETOOTHTHREAD", "Bluetooth Thread already canceled!");
                return;
            }
            this.isCancel = true;
//            this.mBCController.reset();
            interrupt();
        }
    }

    private void resetState(){
        this.bluetoothOnThread.cancel();
//        this.bluetoothOnThread.start();
        bluetoothOnThread = new BluetoothOnThread(mBCController);
        bluetoothOnThread.start();
        this.mHandler.sendEmptyMessage(MsgParam.MSG_WHAT_LISTENNING);
    }
}