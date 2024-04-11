package cn.amamiya.hupublacklist.hooks;

import static android.content.Context.MODE_PRIVATE;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TopicList implements IHook{

    @Override
    public String getHookName() {
        return "专区列表";
    }

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        XposedBridge.log("[HPBlack] 开始hook"+getHookName());
        final Class<?> itemClass = XposedHelpers.findClass("com.hupu.topic.data.TopicThreadItem", classLoader);
        findAndHookMethod("com.hupu.topic.fragment.TopicListFragment$TopicListDispatcher$TopicListHolder",
                classLoader,
                "bindHolder",
                itemClass,
                int.class,
                new XC_MethodHook(){
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Object topicListDispatcher = XposedHelpers.getObjectField(param.thisObject, "this$0");
                        Field activityField = topicListDispatcher.getClass().getDeclaredField("activity");
                        activityField.setAccessible(true);
                        Activity activity = (Activity) activityField.get(topicListDispatcher);

                        SharedPreferences sharedPreferences = activity.getSharedPreferences("blockusers", Context.MODE_PRIVATE);
                        String blockUsersString = sharedPreferences.getString("blockusers", "");
                        List<String> list = new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        Class<?> aClass = param.args[0].getClass();
                        Method getNickname = aClass.getMethod("component22");
                        String userName = (String) getNickname.invoke(param.args[0]);
                        if (list.contains(userName)){
                                // 获取 itemView
                                View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
                                // 隐藏
                                itemView.setVisibility(View.GONE);
                                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                                layoutParams.width=0;
                                layoutParams.height = 0;
                                itemView.setLayoutParams(layoutParams);
                        }

                    }
                });
    }
}
