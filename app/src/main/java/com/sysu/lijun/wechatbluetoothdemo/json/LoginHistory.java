package com.sysu.lijun.wechatbluetoothdemo.json;

/**
 * 登录记录实体类
 *
 * Created by lijun on 15/7/9.
 */
public class LoginHistory {

    private String LoginStore;

    private String LoginDate;

    public void setLoginStore(String LoginStore){
        this.LoginStore = LoginStore;
    }
    public String getLoginStore(){
        return this.LoginStore;
    }
    public void setLoginDate(String LoginDate){
        this.LoginDate = LoginDate;
    }
    public String getLoginDate(){
        return this.LoginDate;
    }


}
