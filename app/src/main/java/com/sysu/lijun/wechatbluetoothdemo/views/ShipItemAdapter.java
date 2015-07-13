package com.sysu.lijun.wechatbluetoothdemo.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.json.ShipItem;

import java.util.List;

/**
 * 购物清单ListView的Adapter
 *
 * Created by lijun on 15/7/13.
 */
public class ShipItemAdapter extends BaseAdapter {

    private Context context;
    private List<ShipItem> list;

    public ShipItemAdapter(Context context, List<ShipItem> mProductList) {
        this.context = context;
        this.list = mProductList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.product_item,null);

            holder = new ViewHolder();
            holder.tvBarCode = (TextView) convertView.findViewById(R.id.tv_product_barcode_item);
            holder.tvUnitPrice = (TextView) convertView.findViewById(R.id.tv_product_unitprice_item);
            holder.tvQuantity = (TextView) convertView.findViewById(R.id.tv_product_quantity_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvBarCode.setText(list.get(position).getProductID());
        holder.tvUnitPrice.setText(list.get(position).getUnitPrice());
        holder.tvQuantity.setText(list.get(position).getQuantity());

        return convertView;
    }

    static class ViewHolder{
        TextView tvBarCode;
        TextView tvUnitPrice;
        TextView tvQuantity;
    }
}
