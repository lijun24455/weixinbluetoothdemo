package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.sysu.lijun.wechatbluetoothdemo.controller.ClsUtils;
import com.sysu.lijun.wechatbluetoothdemo.json.IntrestDetail;
import com.sysu.lijun.wechatbluetoothdemo.json.LoginHistory;
import com.sysu.lijun.wechatbluetoothdemo.json.OrderDetail;
import com.sysu.lijun.wechatbluetoothdemo.json.OrderList;
import com.sysu.lijun.wechatbluetoothdemo.json.ShipItem;
import com.sysu.lijun.wechatbluetoothdemo.json.UserInfo;
import com.sysu.lijun.wechatbluetoothdemo.proto.LJDevice;
import com.sysu.lijun.wechatbluetoothdemo.tools.DecodeProtoPack;
import com.sysu.lijun.wechatbluetoothdemo.tools.MsgParam;
import com.sysu.lijun.wechatbluetoothdemo.controller.BluetoothCallBack;
import com.sysu.lijun.wechatbluetoothdemo.tools.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import proto.MmBp;


/**
 * 主Activity
 *
 */
public class ListenActivity extends Activity {

    private Button mRESETButton = null;

    private BluetoothCallBack mBluetoothCallback = null;
    private BCController mBCController = null;

    private FrontFragmet mFragmentFront;
    private UserInfoFragment mFragmentUserInfo;

    private BluetoothOnThread bluetoothOnThread;
    private final static String strPsw = "0000";
    private static BluetoothDevice remoteDevice = null;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            Log.i("BR","收到蓝牙广播 action = "+action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
//                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.i("UUID", "UUID---->" + device.getUuids());
            }

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                try
                {
                    ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                    ClsUtils.createBond(device.getClass(), device);
//                    ClsUtils.cancelPairingUserInput(device.getClass(), device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pair(device.getAddress(), strPsw);

            }
        }
    };

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
                case MsgParam.MSG_WHAT_RESET:
                    mFragmentFront.onInfoChanged("正在重新连接...");
                    resetState();
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
                        case MmBp.EmCmdId.ECI_push_switchView_VALUE:
                            if (mBCController.getState() != BCController.STATE_LISTEN){
                                resetState();
                            }
                            break;
                        default:
                            Log.i("Handler recv", "recv MSG ECI:"+ msg.arg1);
                    }
                    break;
                default:
                    Log.i("Handler recv:","------->recv MSG what:"+msg.what);
                    break;

            }
        }
    };

    public static boolean pair(String strAddr, String strPsw)
    {
        boolean result = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        bluetoothAdapter.cancelDiscovery();

        if (!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
        { // 检查蓝牙地址是否有效

            Log.d("mylog", "devAdd un effient!");
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);

        if (device.getBondState() != BluetoothDevice.BOND_BONDED)
        {
            try
            {
                Log.d("mylog", "NOT BOND_BONDED");
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block

                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            } //

        }
        else
        {
            Log.d("mylog", "HAS BOND_BONDED");
            try
            {
                ClsUtils.createBond(device.getClass(), device);
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            }
        }
        return result;
    }

    private void handle(Message msg) {
        Object msgObj = msg.obj;

        String byteStr = Utility.byteArray2HexString((byte[]) msgObj, ((byte[]) msgObj).length);
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
        JSONObject rootObject = null;

        JSONArray loginHistoryArray = null;
//        JSONArray orderListArray = null;
        JSONArray shipListArray = null;
        JSONArray intrestListArray = null;
//        List<OrderList> ordersList = null;
//        List<OrderDetail> orderDetailList = null;

        List<LoginHistory> loginHistoryList = null;
        List<IntrestDetail> intrestDetailList = null;
        List<ShipItem> shipItemList = null;

        try {
            rootObject = new JSONObject(dataStr);
            loginHistoryArray = rootObject.getJSONArray("LoginHistory");    //登录历史
//            orderListArray = rootObject.getJSONArray("OrderList");
            shipListArray = rootObject.getJSONArray("ShipList");            //购物清单
            intrestListArray = rootObject.getJSONArray("IntrestList");      //关注列表

            //添加登陆历史数据
            loginHistoryList = new ArrayList<LoginHistory>();
            for (int i = 0 ; i < loginHistoryArray.length() ; i++){
                JSONObject loginRecordObject = loginHistoryArray.getJSONObject(i);
                LoginHistory loginRecord = gson.fromJson(loginRecordObject.toString(), LoginHistory.class);
                loginHistoryList.add(loginRecord);
            }
            Log.i("UPDATEUI","LOGIN HISTORY NUM:"+loginHistoryArray.length());
            userInfo.setLoginHistory(loginHistoryList);

//            //添加历史订单数据
//            ordersList = new ArrayList<OrderList>();
//            OrderList orderlist = null;
//            for(int i = 0 ; i < orderListArray.length() ; i++){
//                JSONObject orderObject = orderListArray.getJSONObject(i);
//                orderlist = gson.fromJson(orderObject.toString(), OrderList.class);
//                JSONArray orderDetailArray = orderObject.getJSONArray("OrderDetail");
//                orderDetailList = new ArrayList<OrderDetail>();
//                for(int j = 0; j<orderDetailArray.length(); j++) {
//                    JSONObject orderDetailOject = orderDetailArray.getJSONObject(j);
//                    OrderDetail orderDetail = gson.fromJson(orderDetailOject.toString(), OrderDetail.class);
//                    orderDetailList.add(orderDetail);
//                }
//                orderlist.setOrderDetail(orderDetailList);
//            }
//            ordersList.add(orderlist);
//            userInfo.setOrderList(ordersList);

            //添加购物清单数据
            shipItemList = new ArrayList<ShipItem>();
            for(int i = 0; i <shipListArray.length(); i++){
                JSONObject shipObject = shipListArray.getJSONObject(i);
                ShipItem shipItem = gson.fromJson(shipObject.toString(), ShipItem.class);
                shipItemList.add(shipItem);
            }
            Log.i("UPDATEUI", "SHIP LIST NUM:"+shipListArray.length());
            userInfo.setShipList(shipItemList);

            //添加关注清单数据
            intrestDetailList = new ArrayList<IntrestDetail>();
            for(int i = 0; i < intrestListArray.length() ; i++){
                JSONObject intrestDetailObject = intrestListArray.getJSONObject(i);
                IntrestDetail intrestDetail = gson.fromJson(intrestDetailObject.toString(), IntrestDetail.class);
                intrestDetailList.add(intrestDetail);
            }
            Log.i("UPDATEUI","INTREST LIST NUM:"+intrestListArray.length());
            userInfo.setIntrestLists(intrestDetailList);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (userInfo != null){
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("USER","----->"+userInfo.toString());
            mFragmentUserInfo = UserInfoFragment.newInstance(userInfo);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_fragment_container,mFragmentUserInfo)
                    .commit();
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
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
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

            while (this.mBCController.getState() < BCController.STATE_WORK_THREAD_OK){
//                Log.i("BLUETOOTHTHREAD","BLUETOOTH NOT GET THE RIGHT STATE!--->"+this.mBCController.getState());
            }

            if(!this.mBCController.startAuthReq()){
                resetState();
            }

        }

        private void cancel(){
            if (this.isCancel){
                Log.i("BLUETOOTHTHREAD", "Bluetooth Thread already canceled!");
                return;
            }
            this.isCancel = true;
//            this.mBCController.disconnect();
//            this.mBCController.reset();
            interrupt();
            Log.i("BLUETOOTHTHREAD","Bluetooth Thread is Interrupted?--->" + this.isInterrupted());
        }
    }

    private void resetState(){
        Log.i("Listening reset", "resetState calling...");
        this.bluetoothOnThread.cancel();
//        this.bluetoothOnThread.start();
        bluetoothOnThread = new BluetoothOnThread(mBCController);
        bluetoothOnThread.start();
        this.mHandler.sendEmptyMessage(MsgParam.MSG_WHAT_LISTENNING);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
    }
}