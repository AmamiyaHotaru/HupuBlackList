package cn.amamiya.hupublacklist.hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Setup implements IHook{
    @Override
    public String getHookName() {
        return "设置";
    }

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        XposedBridge.log("[HPBlack] 开始hook" + getHookName());
        findAndHookMethod("com.hupu.user.ui.SetupActivity",
                classLoader,
                "getModuleOne",
                new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("[HPBlack] hook" + getHookName()+"getModuleOne成功");

                        Activity setupActivity = (Activity) param.thisObject;
                        Class<?> itemClass = XposedHelpers.findClass("com.hupu.user.widget.SetupSingleView", classLoader);
                        ViewGroup result = (ViewGroup) param.getResult();
                        Constructor<?> constructor = itemClass.getConstructor(Context.class, String.class);
                        Object setupSingleView = constructor.newInstance(setupActivity, "黑名单");
                        ViewGroup setupSingleView1 = (ViewGroup) setupSingleView;
                        setupSingleView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("cn.amamiya.hupublacklist", "cn.amamiya.hupublacklist.SettingActivity"));

                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                setupActivity.startActivity(intent);
                            }
                        });
                        result.addView((View) setupSingleView);
                        param.setResult(result);


                    }
                });
    }
}
