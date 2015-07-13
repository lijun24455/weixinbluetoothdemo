package com.sysu.lijun.wechatbluetoothdemo.json;

/**
 * 我的关注信息实体类
 *
 * Created by lijun on 15/7/9.
 */
public class IntrestDetail {
    private String ProductID;

    private String UnitPrice;


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

}
