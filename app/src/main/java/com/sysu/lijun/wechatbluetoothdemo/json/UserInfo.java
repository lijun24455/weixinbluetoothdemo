package com.sysu.lijun.wechatbluetoothdemo.json;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lijun on 15/7/1.
 */
public class UserInfo implements Parcelable{

    private String CustomerID;

    private String CustomerPoint;

    private List<LoginHistory> LoginHistorys ;

    private List<OrderList> OrderLists ;

    private List<ShipList> ShipLists ;

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
    public void setOrderList(List<OrderList> OrderList){
        this.OrderLists = OrderList;
    }
    public List<OrderList> getOrderList(){
        return this.OrderLists;
    }
    public void setShipList(List<ShipList> ShipList){
        this.ShipLists = ShipList;
    }
    public List<ShipList> getShipList(){
        return this.ShipLists;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "CustomerID='" + CustomerID + '\'' +
                ", CustomerPoint='" + CustomerPoint + '\'' +
                ", LoginHistorys=" + LoginHistorys +
                ", OrderLists=" + OrderLists +
                ", ShipLists=" + ShipLists +
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
