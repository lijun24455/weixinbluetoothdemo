package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.json.UserInfo;
import com.sysu.lijun.wechatbluetoothdemo.tools.Utility;

import java.io.ByteArrayInputStream;


public class UserInfoFragment extends Fragment {

    private ImageView ivHeadImage;
    private TextView tvNickName;
    private TextView tvUserPoint;
    private ListView lvLoginHistory;
    private ListView lvShipList;
    private ListView lvIntrestList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_USERINFO = "USER_INFO";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserInfo mUserInfo;

//    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(UserInfo mUserInfo) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        args.putParcelable(ARG_USERINFO, mUserInfo);

        fragment.setArguments(args);
        return fragment;
    }

    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            mUserInfo = getArguments().getParcelable(ARG_USERINFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);


        ivHeadImage = (ImageView) rootView.findViewById(R.id.iv_head_img);
        tvNickName = (TextView) rootView.findViewById(R.id.tv_info_name);
        tvUserPoint = (TextView) rootView.findViewById(R.id.tv_info_score);

        lvLoginHistory = (ListView) rootView.findViewById(R.id.lv_login_history);
        lvShipList = (ListView) rootView.findViewById(R.id.lv_ship_list);
        lvIntrestList = (ListView) rootView.findViewById(R.id.lv_intrest_list);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        tvNickName.setText(mUserInfo.getCustomerNickName());
        tvUserPoint.setText(mUserInfo.getCustomerPoint());

        initImg();

        lvLoginHistory.setAdapter(new LoginHistoryAdapter(getActivity().getApplicationContext(), mUserInfo.getLoginHistory()));
        lvShipList.setAdapter(new ShipItemAdapter(getActivity().getApplicationContext(), mUserInfo.getShipList()));
        lvIntrestList.setAdapter(new IntrestItemAdapter(getActivity().getApplicationContext(), mUserInfo.getIntrestLists()));
    }

    private void initImg() {
        String headImgStr = mUserInfo.getHeadImgBase64();
        byte[] imgBytes = headImgStr.getBytes();

        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
//        Bitmap bitmap1 = BitmapFactory.decodeStream(new ByteArrayInputStream(imgBytes));

        ivHeadImage.setImageBitmap(bitmap);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
