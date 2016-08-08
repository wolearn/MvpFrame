![](http://upload-images.jianshu.io/upload_images/1931006-fa6fbaa08b91e647.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#前言
听到一些童鞋抱怨MVP，所有搞了个辅助实现MVP的小东西，叫MvpFrame。还不了解MVP的先看[《Google原味mvp实践》](http://www.jianshu.com/p/dc9733bc3a54)。主要的功能如下
* 省代码。不能偷懒的框架都是耍流氓，当然像Rx系列这样可以简化逻辑的也是正经人。
* 不依赖其他库。不跟Retrofit，Rxjava等等耦合，只是纯粹的辅助MVP的实现。
* 小，只有8k。可以在任意最小业务单元使用，即使之前业务没使用，或者之后不想使用都没关系。
* M，V，P中各个层级的实例托管。
* 维护M，V，P中对其他层的引用，并保证实例可回收。
* 在工程任意位置获取MVP中的实例。

开源地址是
>https://github.com/wolearn/MvpFrame

#怎么用
可以把上面工程中的mvpframelib作为Android Lib引入，或者直接复制java文件也可以。我简单解释下还是我常用那个登陆的例子。先看目录结构。
![](http://upload-images.jianshu.io/upload_images/1931006-13632165bf609f39.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
##初始化
在Application中调用
>Mvp.getInstance().init(this);

规划好view和presenter的接口。
```
public class LoginContract {
    public interface View extends BaseView {
        String getAccount();
        String getPassword();
        void loginSuccess();
        void loginError(String errorMsg);
    }

    public interface Presenter extends BasePresenter {
        void login();
    }
}
```
用一个契约类来定义需要的方法。之前有童鞋问我，这个接口写好烦，能不能不写。肯定不能。我能想到的理由有三点
* 依赖倒置原则。高层模块和底层模块之间不能直接依赖，而是应该依赖其接口
* 保证其可测性
* 面向接口编程的前期设计感

##View层
可以支持Activity和Fragment，Activity继承MvpActivity，Fragment继承MvpFragment。
```
public class LoginActivity extends MvpActivity<LoginPresenter> implements LoginContract.View, View.OnClickListener {
    private EditText edtAccount, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    @Override
    public BaseView getBaseView() {
        return this;
    }

    @Override
    public void onClick(View v) {
        mPresenter.login();
    }

    private void initViews() {
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void loginError(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getAccount() {
        return edtAccount.getText().toString();
    }

    @Override
    public String getPassword() {
        return edtPassword.getText().toString();
    }
}
```
* 注意MvpActivity<LoginPresenter>，在继承的时候要通过泛型确定Presenter层的具体类型，一定要写。
* getBaseView方法返回的是IView类型，如果是当前Activity实现的话，直接返回this即可。
* mPresenter可以直接使用，不用声明和实例化

##Presenter层
Presenter继承MvpPresenter实现即可。
```
public class LoginPresenter extends MvpPresenter<LoginHttp, LoginContract.View> implements LoginContract.Presenter {
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
```
* 注意MvpPresenter<LoginHttp, LoginContract.View>的泛型，要确定Model的具体实现和IView。
* IView的回调接口对象通过getIView()方法获取
* mModel指向泛型中确认的LoginHttp对象，可以直接使用，不用声明和实例化
* mContext是一个ApplicationContext的引用

##Model层
数据的来源一般有三个：DB，NET，Cache。看个图
![](http://upload-images.jianshu.io/upload_images/1931006-cedb0686a7d085bb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
之前有童鞋说，业务过程很烦，要搞个UseCase文件，然后又要搞个Repository文件，最后搞个HttpLogin，因为登录本来就只是要跟服务端确认，前面2个文件基本都是透传。是不是一定要这么死板呢？当然不是，结构可以根据业务的复杂度做调整的。比如我这里登录就是直接让HttpLogin继承MvpModel, 直接跟P层交互。
```
public class LoginHttp extends MvpModel {
    private static final String TAG = "LoginHttp";

    /**
     * 密码登陆
     *
     * @param account
     * @param password
     */
    public void login(String account, String password) {
//        execute(api.login(account, password), callback);
    }
}
```
如果你当前业务是只跟DB打交道，也可以让LoginDB继承MvpModel，然后在P层的泛型中确认类型即可。
##其他任意位置获取M，V，P实例
默认通过以下API获取的唯一实例，传入为实例的class类型
>Mvp.getInstance().getPresenter();
Mvp.getInstance().getModel();        
Mvp.getInstance().getView();

getPresenter 和 getModel 的实例默认会创建，getView 要确定Activity或者Fragment已经创建，否则可能为null。

#后记
建议以文件的形式引入，方便依据工程业务做定制化。喜欢请帮忙戳喜欢，有问题欢迎评论。
