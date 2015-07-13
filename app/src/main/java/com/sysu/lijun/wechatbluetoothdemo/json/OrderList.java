package com.sysu.lijun.wechatbluetoothdemo.json;

import java.util.List;

/**
 * 订单详情实体类
 *
 * Created by lijun on 15/7/9.
 */
public class OrderList {
    private String OrderID;

    private String OrderMoney;

    private String OrderDate;

    private List<OrderDetail> OrderDetails ;

    public void setOrderID(String OrderID){
        this.OrderID = OrderID;
    }
    public String getOrderID(){
        return this.OrderID;
    }
    public void setOrderMoney(String OrderMoney){
        this.OrderMoney = OrderMoney;
    }
    public String getOrderMoney(){
        return this.OrderMoney;
    }
    public void setOrderDate(String OrderDate){
        this.OrderDate = OrderDate;
    }
    public String getOrderDate(){
        return this.OrderDate;
    }
    public void setOrderDetail(List<OrderDetail> OrderDetail){
        this.OrderDetails = OrderDetail;
    }
    public List<OrderDetail> getOrderDetail(){
        return this.OrderDetails;
    }
}
