package cn.amamiya.hupublacklist.hooks;

import static android.content.Context.MODE_PRIVATE;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class PostDetail implements IHook {

    @Override
    public String getHookName() {
        return "帖子详情";
    }

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        XposedBridge.log("[HPBlack] 开始hook" + getHookName());

        findAndHookMethod("com.hupu.android.bbs.detail.PostDetailActivity", classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Activity thisObject = (Activity) param.thisObject;
                        SharedPreferences sharedPreferences = thisObject.getSharedPreferences("blockusers", Context.MODE_PRIVATE);
                        String blockUsersString = sharedPreferences.getString("blockusers", "");
                        List<String> list = new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        findAndHookMethod("com.hupu.android.bbs.replylist.BBSPostReplyPackageEntity",
                                classLoader,
                                "setHide",
                                boolean.class,
                                new XC_MethodHook() {
                                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                        Field headerEntityField = XposedHelpers.findField(param.thisObject.getClass(), "headerEntity");
                                        headerEntityField.setAccessible(true);
                                        Object headerEntity = headerEntityField.get(param.thisObject);

                                        Field authorEntityField = XposedHelpers.findField(headerEntity.getClass(), "authorEntity");
                                        authorEntityField.setAccessible(true);
                                        Object authorEntity = authorEntityField.get(headerEntity);

                                        Field nicknameField = XposedHelpers.findField(authorEntity.getClass(), "nickname");
                                        nicknameField.setAccessible(true);
                                        String nickname = (String) nicknameField.get(authorEntity);

                                        if (list.contains(nickname)){
                                            param.args[0] = true;
                                        }
                                    }
                                });


                    }
                });
    }
}


//        findAndHookMethod("com.hupu.android.bbs.replylist.BBSPostReplyPackageEntity",
//                classLoader,
//                "setHide",
//                boolean.class,
//                new XC_MethodHook(){
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("[HPBlack] 成功hook getHeaderEntity啦");
//                        XposedBridge.log(String.valueOf((boolean)param.args[0]));
//                        Field headerEntityField = XposedHelpers.findField(param.thisObject.getClass(), "headerEntity");
//                        headerEntityField.setAccessible(true);
//                        Object headerEntity = headerEntityField.get(param.thisObject);
//
//                        Field authorEntityField = XposedHelpers.findField(headerEntity.getClass(), "authorEntity");
//                        authorEntityField.setAccessible(true);
//                        Object authorEntity = authorEntityField.get(headerEntity);
//
//                        Field nicknameField = XposedHelpers.findField(authorEntity.getClass(),"nickname");
//                        nicknameField.setAccessible(true);
//                        String nickname = (String)nicknameField.get(authorEntity);
//                        XposedBridge.log(nickname);
//                        param.g
//

//
//                        param.args[0]=true;
//
//                    }
//                });
//    }





