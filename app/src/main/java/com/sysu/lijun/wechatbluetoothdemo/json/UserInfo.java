package com.sysu.lijun.wechatbluetoothdemo.json;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 用户信息实体类
 *
 * Created by lijun on 15/7/1.
 */
public class UserInfo implements Parcelable{

    private String CustomerID;

    private String CustomerPoint;

    public String getCustomerNickName() {
        return CustomerNickName;
    }

    public void setCustomerNickName(String customerNickName) {
        CustomerNickName = customerNickName;
    }

    private String CustomerNickName;

    public String getHeadImgBase64() {
        return HeadImgBase64;
    }

    public void setHeadImgBase64(String headImgBase64) {
        HeadImgBase64 = headImgBase64;
    }

    private String HeadImgBase64;

    private List<LoginHistory> LoginHistorys ;

//    private List<OrderList> OrderLists ;

    private List<ShipItem> ShipLists ;

    public List<IntrestDetail> getIntrestLists() {
        return IntrestLists;
    }

    public void setIntrestLists(List<IntrestDetail> intrestLists) {
        IntrestLists = intrestLists;
    }

    private List<IntrestDetail> IntrestLists;

    public void setCustomerID(String CustomerID){
        this.CustomerID = CustomerID;
    }
    public String getCustomerID(){
        return this.CustomerID;
    }
    public void setCustomerPoint(String CustomerPoint){
        this.CustomerPoint = CustomerPoint;
    }
    public String getCustomerPoint(){
        return this.CustomerPoint;
    }
    public void setLoginHistory(List<LoginHistory> LoginHistory){
        this.LoginHistorys = LoginHistory;
    }
    public List<LoginHistory> getLoginHistory(){
        return this.LoginHistorys;
    }
//    public void setOrderList(List<OrderList> OrderList){
//        this.OrderLists = OrderList;
//    }
//    public List<OrderList> getOrderList(){
//        return this.OrderLists;
//    }
    public void setShipList(List<ShipItem> ShipList){
        this.ShipLists = ShipList;
    }
    public List<ShipItem> getShipList(){
        return this.ShipLists;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "CustomerID='" + CustomerID + '\'' +
                ",HeadImgBase64= " + HeadImgBase64 + '\'' +
                ", CustomerPoint='" + CustomerPoint + '\'' +
                ", CustomerNickName='" + CustomerNickName + '\'' +
                ", LoginHistorys=" + LoginHistorys.size() +
                ", ShipLists=" + ShipLists.size() +
                ", IntrestLists=" + IntrestLists.size() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
