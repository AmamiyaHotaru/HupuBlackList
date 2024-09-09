package cn.amamiya.hupublacklist.hooks;

import static android.content.Context.MODE_PRIVATE;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.amamiya.hupublacklist.utils.FileHelper;
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

                        // 获取目标应用程序的外部文件目录
                        File externalFilesDir = activity.getExternalFilesDir(null);
                        if (externalFilesDir == null) {
                            Toast.makeText(activity,"获取数据目录出错，可以尝试重装APP",Toast.LENGTH_LONG).show();
                            return;
                        }

                        File blacklistFile = new File(externalFilesDir, "blacklist.txt");
                        String blockUsersString = FileHelper.readFileToString(blacklistFile);
                        List<String> blackList = new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        File keywordListFile = new File(externalFilesDir, "keyword.txt");
                        String keywordString = FileHelper.readFileToString(keywordListFile);
                        List<String> keywordList = new ArrayList<>(Arrays.asList(keywordString.split(",")));

                        Class<?> aClass = param.args[0].getClass();
                        Method getNickname = aClass.getMethod("component22");
                        String userName = (String) getNickname.invoke(param.args[0]);

                        Method getTitle = aClass.getMethod("component17");
                        String title = (String) getTitle.invoke(param.args[0]);


                        if (blackList.contains(userName)||(keywordList.stream().anyMatch(keyword -> !keyword.trim().isEmpty() && title.contains(keyword.trim())))){
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
