package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.controller.BCController;
import com.sysu.lijun.wechatbluetoothdemo.json.UserInfo;
import com.sysu.lijun.wechatbluetoothdemo.proto.LJDevice;
import com.sysu.lijun.wechatbluetoothdemo.tools.DecodeProtoPack;
import com.sysu.lijun.wechatbluetoothdemo.tools.MsgParam;
import com.sysu.lijun.wechatbluetoothdemo.controller.BluetoothCallBack;
import com.sysu.lijun.wechatbluetoothdemo.tools.Utility;


import org.json.JSONException;
import org.json.JSONObject;

import proto.MmBp;


public class ListenActivity extends Activity {

    private TextView textViewInfo = null;
    private ImageView imageViewUniqle = null;

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
                            mBluetoothCallback.onConnected();

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
        Object msgObj = msg.obj;
        byte[] bodyContent = DecodeProtoPack.getProtoPackBody((byte[]) msgObj);

        String contentStr = Utility.byteArray2HexString(bodyContent,bodyContent.length);
        Log.i("RECVDATA", "Utilty----->"+contentStr);
        LJDevice ljDevice = LJDevice.parse(bodyContent);
        String dataStr = ljDevice.body;

        Log.i("RECV DATA:", dataStr);

        updateUI(dataStr);
    }

    private void updateUI(String dataStr) {

        Gson gson = new Gson();

        UserInfo userInfo = gson.fromJson(dataStr, UserInfo.class);




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
        imageViewUniqle = (ImageView) findViewById(R.id.img_uniqle);

        this.mBluetoothCallback = new BluetoothCallBack(this.mHandler);
        this.mBCController = BCController.getInstance(this, this.mBluetoothCallback);
    }

}
