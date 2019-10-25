package com.bitcoin.juwan.api3;

import android.app.Activity;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * FileName：MyBufferknife
 * Create By：liumengqiang
 * Description：TODO
 */
public class MyBufferknife {
    private static final ActivityViewFinder activityFinder = new ActivityViewFinder();//默认声明一个Activity View查找器

    private static final LinkedHashMap<String, ViewBinder> viewBinderMap = new LinkedHashMap<>();

    public static void init(Activity activity) {
        init(activity, activity, activityFinder);
    }

    public static void init(Activity activity, Object o, ActivityViewFinder activityFinder) {
        String activityClassName = activity.getClass().getName();
        ViewBinder viewBinder = viewBinderMap.get(activityClassName + "$BindView");
        if(viewBinder == null) {
            try {
                Class<?> aClass = Class.forName(activityClassName + "$BindView");
                viewBinder = (ViewBinder)aClass.newInstance();
                viewBinder.bindView(activity, o, activityFinder);

                viewBinderMap.put(activityClassName, viewBinder);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            viewBinder.bindView(activity, o, activityFinder);
        }
    }
}
