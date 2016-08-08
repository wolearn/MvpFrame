package com.wolearn.mvpframelib.frame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wolearn.mvpframelib.base.BaseView;

/**
 * Created by wulei
 * Data: 2016/8/4.
 */
public abstract class MvpActivity <P extends MvpPresenter> extends AppCompatActivity implements MvpView{
    public P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMvp();
    }

    public void initMvp(){
        Mvp mvp = Mvp.getInstance();
        mvp.registerView(this.getClass(), this);
        mPresenter = (P) mvp.getPresenter(Mvp.getGenericType(this, 0));
        mPresenter.initPresenter(getBaseView());
    }

    /**
     * 确定IView类型
     * @return
     */
    public abstract BaseView getBaseView();

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Mvp.getInstance().unRegister(this.getClass());
        mPresenter.destory();
    }
}
