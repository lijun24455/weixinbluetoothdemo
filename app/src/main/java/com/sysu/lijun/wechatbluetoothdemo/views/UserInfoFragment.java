package com.sysu.lijun.wechatbluetoothdemo.views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sysu.lijun.wechatbluetoothdemo.R;
import com.sysu.lijun.wechatbluetoothdemo.json.UserInfo;


public class UserInfoFragment extends Fragment {

    private TextView tvUserName;
    private TextView tvTime;
    private TextView tvStore;
    private TextView tvScore;

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

        tvUserName = (TextView) rootView.findViewById(R.id.tv_info_name);
        tvTime = (TextView) rootView.findViewById(R.id.tv_info_time);
        tvStore = (TextView) rootView.findViewById(R.id.tv_info_store);
        tvScore = (TextView) rootView.findViewById(R.id.tv_info_score);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();
        this.tvUserName.setText(mUserInfo.getCustomerID());
        this.tvTime.setText(mUserInfo.getLoginHistory().get(0).getLoginDate());
        this.tvStore.setText(mUserInfo.getLoginHistory().get(0).getLoginStore());
        this.tvScore.setText(mUserInfo.getCustomerPoint());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
