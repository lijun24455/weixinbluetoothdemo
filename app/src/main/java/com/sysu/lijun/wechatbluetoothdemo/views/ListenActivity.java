package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.controller.BCController;
import com.sysu.lijun.wechatbluetoothdemo.tools.MsgParam;
import com.sysu.lijun.wechatbluetoothdemo.controller.BluetoothCallBack;


import proto.MmBp;


public class ListenActivity extends Activity {

    private TextView textViewInfo = null;

    private BluetoothCallBack mBluetoothCallback = null;
    private BCController mBCController = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MsgParam.MSG_WHAT_LISTENNING:
                    textViewInfo.setText("正在等待连接...");
                    break;
                case MsgParam.MSG_WHAT_AUTH:
                    textViewInfo.setText("正在发送Auth Req...");
                    break;
                case MsgParam.MSG_WHAT_INIT:
                    textViewInfo.setText("正在发送Init Req");
                    break;
                case MsgParam.MSG_WHAT_CONNECTED:
                    textViewInfo.setText("完成连接，正在等待数据传入...");
                    break;
                case MsgParam.MSG_WHAT_RECV_DATA:
                    switch (msg.arg1){
                        case MmBp.EmCmdId.ECI_resp_auth_VALUE:
                            if (mBCController.getState() != BCController.STATE_AUTH_REQ){
                                Log.i("HANDLE_MSG","now device's state is not AUTH REQ...");
                                break;
                            }
                            mBCController.setState(BCController.STATE_AUTH_RESP);
                            mBCController.startInitReq(msg.arg2);
                            break;
                        case MmBp.EmCmdId.ECI_resp_init_VALUE:
                            if (mBCController.getState() != BCController.STATE_INIT_REQ){
                                Log.i("HANDLE_MSG","now device's state is not INIT REQ...");
                                break;
                            }
                            mBCController.setState(BCController.STATE_INIT_RESP);
                            break;
                        case MmBp.EmCmdId.ECI_push_recvData_VALUE:

                            if (mBCController.getState() != BCController.STATE_INIT_RESP){
                                Log.i("HANDLE_MSG","now device's state is not INIT RSP...");
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

        Log.i("BLUETOOTH", msg.toString());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        Initialize();

        mBCController.connect();

        while (mBCController.getState()<BCController.STATE_CONNECTED){
            continue;
        }

        mBCController.startAuthReq();

    }


    private void Initialize() {

        textViewInfo = (TextView) findViewById(R.id.tv_info);

//        this.mHandler = new BCHandler(Looper.getMainLooper(), this);
        this.mBluetoothCallback = new BluetoothCallBack(this.mHandler);
        this.mBCController = BCController.getInstance(this, this.mBluetoothCallback);
    }

//    private void sendMessage(int paramWhat, Object paramObject, int paramInt1, int paramInt2){
//        this.mHandler.sendMessage(Message.obtain(this.mHandler, paramWhat, paramInt1, paramInt2));
//    }

//    public static class BCHandler extends Handler{
//        private final WeakReference<ListenActivity> mWeakRef;
//
//        public BCHandler(Looper paramLooper, ListenActivity paramActivity){
//            super();
//            this.mWeakRef = new WeakReference<ListenActivity>(paramActivity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what){
//                case MsgParam.MSG_WHAT_LISTENNING:
//
//                    break;
//                case MsgParam.MSG_WHAT_AUTH:
//                    break;
//                case MsgParam.MSG_WHAT_INIT:
//                    break;
//                case MsgParam.MSG_WHAT_CONNECTED:
//                    break;
//                case MsgParam.MSG_WHAT_RECV_DATA:
//                    break;
//                default:
//                    break;
//
//            }
//
//        }
//    }

}
