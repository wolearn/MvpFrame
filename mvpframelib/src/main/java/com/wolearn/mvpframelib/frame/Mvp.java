package com.wolearn.mvpframelib.frame;

import android.content.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wulei
 * Data: 2016/8/4.
 * <p>
 * 相同类型的实例只维护一个，M 和 P的实例第一次创建后会一直复用，V 的实例会更新到最近使用的实例
 */
public class Mvp<M extends MvpModel, V extends MvpView, P extends MvpPresenter> {
    public Map<String, Object> sInstanceMap;
    public Context mContext;

    private Mvp() {

    }

    private static class InstanceHolder {
        private static final Mvp INSTANCE = new Mvp();
    }

    public static Mvp getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context context){
        this.mContext = context;
    }

    public Context getApplictionContext(){
        return mContext;
    }

    /**
     * 实例化Map
     */
    private void initMap() {
        if (sInstanceMap == null) {
            sInstanceMap = new ConcurrentHashMap();
        }
    }

    /**
     * 注册Model层实例
     *
     * @param mClass
     */
    public void registerModel(Class<M> mClass) {
        initMap();

        M mInstance = null;

        if (!sInstanceMap.containsKey(mClass.getName())) {
            try {

                mInstance = (M) Class.forName(mClass.getName()).newInstance();
                sInstanceMap.put(mClass.getName(), mInstance);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册View层实例
     *
     * @param vClass
     */
    public void registerView(Class<V> vClass, V vInstance) {
        initMap();

        sInstanceMap.put(vClass.getName(), vInstance);

//        for (String className : sInstanceMap.keySet()) {
//            LogUtil.errer("Mvp", "key: " + className + " hasCode: " + sInstanceMap.get(className.toString()).hashCode());
//        }
    }

    /**
     * 注册Presenter层实例
     *
     * @param pClass
     */
    public void registerPresenter(Class<P> pClass) {
        initMap();

        P pInstance = null;

        if (!sInstanceMap.containsKey(pClass.getName())) {
            try {

                pInstance = (P) Class.forName(pClass.getName()).newInstance();
                sInstanceMap.put(pClass.getName(), pInstance);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取Model层实例, 为null的话， 走创建流程
     *
     * @param mClass
     * @return
     */
    public M getModel(Class<M> mClass) {
        initMap();

        if (!sInstanceMap.containsKey(mClass.getName())) {
            registerModel(mClass);
        }

        return (M) sInstanceMap.get(mClass.getName());
    }

    /**
     * 获取View层实例, view的实例由系统实例化，没有注册返回null
     *
     * @param vClass
     * @return
     */
    public V getView(Class<V> vClass) {
        if (sInstanceMap == null) return null;

        if (!sInstanceMap.containsKey(vClass.getName())) {
            return null;
        }

        return (V) sInstanceMap.get(vClass.getName());
    }

    /**
     * 获取Presenter层实例, 为null的话， 走创建流程
     *
     * @param pClass
     * @return
     */
    public P getPresenter(Class<P> pClass) {
        initMap();

        if (!sInstanceMap.containsKey(pClass.getName())) {
            registerPresenter(pClass);
        }

        return (P) sInstanceMap.get(pClass.getName());
    }

    /**
     * 移除实例
     */
    public void unRegister(Class clazz) {
        if (sInstanceMap == null) {
            return;
        }

        sInstanceMap.remove(clazz.getName());
    }


    /**
     * 获取父类泛型的类型
     *
     * @param o
     * @param i
     * @return
     */
    public static Class getGenericType(Object o, int i) {
        Type type = o.getClass().getGenericSuperclass();
        ParameterizedType p = (ParameterizedType) type;
        Class c = (Class) p.getActualTypeArguments()[i];
        return c;
    }
}
