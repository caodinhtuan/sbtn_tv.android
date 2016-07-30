package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.UserInfo;
import com.sbtn.androidtv.eventbus.ReloadDataEvent;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.LogoutCallBack;
import com.sbtn.androidtv.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class AccountInfoFragment extends Fragment {
    public static final String TAG = "AccountInfoFragment";
    private ImageView imageViewAvatar;
    private TextView textViewFullName;
    private TextView textViewEmail;

    public static AccountInfoFragment newInstance() {

//        Bundle args = new Bundle();

        AccountInfoFragment fragment = new AccountInfoFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_info, container, false);
        initView(v);
        bindingView();
        return v;
    }

    private void initView(View v) {
        imageViewAvatar = (ImageView) v.findViewById(R.id.avatar);
        textViewFullName = (TextView) v.findViewById(R.id.txtName);
        textViewEmail = (TextView) v.findViewById(R.id.txtEmail);
        View buttonLogOut = v.findViewById(R.id.btLogOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestFramework.logOut(getActivity(), new RequestFramework.DataCallBack<LogoutCallBack>() {
                    @Override
                    public void onResponse(LogoutCallBack dataResult) {
                        handleAfterLogOut();
                    }

                    @Override
                    public void onFailure() {
                        handleAfterLogOut();
                    }
                });
            }
        });
        buttonLogOut.requestFocus();
    }

    private void handleAfterLogOut() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof AccountManagerActivity) {
            ((AccountManagerActivity) activity).handleShowFragment();
        }

        EventBus.getDefault().post(new ReloadDataEvent(TAG));
    }

    private void bindingView() {
        UserInfo userInfo = CacheDataManager.getInstance(getActivity()).getUserInfo();
        if (userInfo != null) {
            if (StringUtils.isNotEmpty(userInfo.getAvatarUrl())) {
                imageViewAvatar.setImageResource(R.drawable.icon_user_default_loading);
                ImageLoader.getInstance().displayImage(userInfo.getAvatarUrl(), imageViewAvatar);
            } else {
                imageViewAvatar.setImageResource(R.drawable.icon_user_default);
            }
            textViewFullName.append(
                    StringUtils.isNotEmpty(userInfo.getFullName()) ? userInfo.getFullName() : "N/A");
            textViewEmail.append(
                    StringUtils.isNotEmpty(userInfo.getEmail()) ? userInfo.getEmail() : "N/A");
        } else {
            imageViewAvatar.setImageResource(R.drawable.icon_user_default);
            textViewFullName.setText("N/A");
            textViewEmail.setText("N/A");
        }
    }
}
