package com.sysu.lijun.wechatbluetoothdemo.json;

/**
 * Created by lijun on 15/7/9.
 */
public class OrderDetail {
    private String ProductID;

    private String UnitPrice;

    private String Quantity;

    public void setProductID(String ProductID){
        this.ProductID = ProductID;
    }
    public String getProductID(){
        return this.ProductID;
    }
    public void setUnitPrice(String UnitPrice){
        this.UnitPrice = UnitPrice;
    }
    public String getUnitPrice(){
        return this.UnitPrice;
    }
    public void setQuantity(String Quantity){
        this.Quantity = Quantity;
    }
    public String getQuantity(){
        return this.Quantity;
    }
}
