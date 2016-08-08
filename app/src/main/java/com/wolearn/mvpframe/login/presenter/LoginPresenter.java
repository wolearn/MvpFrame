package com.wolearn.mvpframe.login.presenter;

import android.text.TextUtils;

import com.wolearn.mvpframe.R;
import com.wolearn.mvpframe.login.contract.LoginContract;
import com.wolearn.mvpframe.login.model.LoginHttp;
import com.wolearn.mvpframelib.frame.MvpPresenter;

/**
 * Created by wulei
 * Data: 2016/3/30.
 */
public class LoginPresenter extends MvpPresenter<LoginHttp, LoginContract.View> implements LoginContract.Present {
    private static final String TAG = "LoginPresenter";

    @Override
    public void login() {
        if (!checkParameter()) return;

        String account = getIView().getAccount();
        String password = getIView().getPassword();

        mModel.login(account, password);

        //模拟登陆成功
        getIView().loginSuccess();
    }

    /**
     * 登录参数校验
     *
     * @return
     */
    private boolean checkParameter() {
        try {
            if (TextUtils.isEmpty(getIView().getAccount())) {
                getIView().loginError(mContext.getString(R.string.toast_account_empty));
                return false;
            }

            if (TextUtils.isEmpty(getIView().getPassword())) {
                getIView().loginError(mContext.getString(R.string.toast_password_empty));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
