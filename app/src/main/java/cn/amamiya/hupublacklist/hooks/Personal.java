package cn.amamiya.hupublacklist.hooks;



import static android.content.Context.MODE_PRIVATE;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


public class Personal implements IHook{
    @Override
    public String getHookName() {
        return "个人资料";
    }

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        XposedBridge.log("[HPBlack] 开始hook" + getHookName());
        final Class<?> personInfoClass = XposedHelpers.findClass("com.hupu.user.bean.PersonInfo", classLoader);
        findAndHookMethod("com.hupu.user.ui.PersonalActivity",
                classLoader,
                "constructTab",
                personInfoClass,
                new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Activity personalActivity = (Activity) param.thisObject;


                        SharedPreferences sharedPreferences = personalActivity.getSharedPreferences("blockusers", Context.MODE_PRIVATE);
                        String blockUsersString = sharedPreferences.getString("blockusers", "");
                        List<String> list = blockUsersString.isEmpty()?new ArrayList<>():new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        XposedBridge.log("[HPBlack] listString"+blockUsersString);

                        int textResourceId = personalActivity.getResources().getIdentifier("tv_name", "id", personalActivity.getPackageName());
                        TextView textView = personalActivity.findViewById(textResourceId);

                        Class<?> aClass = param.args[0].getClass();
                        Method getNickname = aClass.getMethod("getNickname");
                        String userName = (String) getNickname.invoke(param.args[0]);
                        XposedBridge.log("[HPBlack]userName="+userName);

                        if (list.contains(userName)) {
                            textView.setTextColor(Color.RED);
                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(personalActivity);
                                builder.setMessage(textView.getCurrentTextColor() == Color.RED ? "是否取消拉黑？" : "是否加入黑名单？")
                                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (textView.getCurrentTextColor() == Color.RED) {
                                                    XposedBridge.log(userName + " 被取消拉黑");
                                                    list.remove(userName);
                                                    sharedPreferences.edit().putString("blockusers",String.join(",",list)).apply();
                                                    textView.setTextColor(Color.BLACK);
                                                } else {
                                                    XposedBridge.log(userName+" 被拉黑");
                                                    list.add(userName);
                                                    sharedPreferences.edit().putString("blockusers",String.join(",",list)).apply();
                                                    textView.setTextColor(Color.RED);
                                                }
                                            }
                                        })
                                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                });
    }



}
