package com.sysu.lijun.wechatbluetoothdemo.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.json.LoginHistory;

import java.util.List;

/**
 * Created by lijun on 15/7/13.
 */
public class LoginHistoryAdapter extends BaseAdapter {

    private List<LoginHistory> loginHistoryList;
    private Context context;


    public LoginHistoryAdapter(Context context, List<LoginHistory> loginHistoryList) {
        this.loginHistoryList = loginHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return loginHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return loginHistoryList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.login_item, null);

            holder = new ViewHolder();
            holder.tvLoginDate = (TextView) convertView.findViewById(R.id.tv_login_time_item);
            holder.tvLoginStore = (TextView) convertView.findViewById(R.id.tv_login_store_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvLoginDate.setText(loginHistoryList.get(position).getLoginDate());
        holder.tvLoginStore.setText(loginHistoryList.get(position).getLoginStore());

        return convertView;
    }

    static class ViewHolder{
        TextView tvLoginDate;
        TextView tvLoginStore;
    }


}
