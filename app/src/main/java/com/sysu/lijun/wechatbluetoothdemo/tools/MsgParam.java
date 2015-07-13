package com.sysu.lijun.wechatbluetoothdemo.tools;

import java.security.PublicKey;

/**
 * BluetoothAdapter 与主界面交互时消息类别定义类
 *
 * Created by lijun on 15/6/27.
 */
public class MsgParam {
    public static final int MSG_WHAT_LISTENNING = 10001;
    public static final int MSG_WHAT_CONNECTED = 10002;
    public static final int MSG_WHAT_AUTH = 10003;
    public static final int MSG_WHAT_INIT = 10004;

    public static final int MSG_WHAT_RECV_DATA = 10005;

    public static final int MSG_WHAT_RESET = 10006;
}
