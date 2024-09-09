package cn.amamiya.hupublacklist;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cn.amamiya.hupublacklist.hooks.Hooks;
import cn.amamiya.hupublacklist.utils.FileHelper;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam)   {
        XposedBridge.log("[HPBlack]Loaded app: " + lpparam.packageName);
        if (!lpparam.packageName.equals("com.hupu.games")){
            return;
        }

        // 创建黑名单列表
        XposedHelpers.findAndHookMethod("com.hupu.games.main.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                // 获取目标应用程序的 Context
                Context context = ((Activity) param.thisObject).getApplicationContext();

                // 获取目标应用程序的外部文件目录
                File externalFilesDir = context.getExternalFilesDir(null);

                if (externalFilesDir != null) {
                    // 在外部文件目录下创建一个名为 "blacklist.txt" 的文件
                    File newblacklistFile = new File(externalFilesDir, "blacklist.txt");
                    if (!newblacklistFile.exists()) {
                        try {
                            boolean created = newblacklistFile.createNewFile();
                            if (created) {
                                XposedBridge.log("[HPBlack]blacklist.txt 文件创建成功");
                            } else {
                                XposedBridge.log("[HPBlack]blacklist.txt 文件创建失败");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        XposedBridge.log("[HPBlack]blacklist.txt 文件已存在，无需创建");
                    }
                    // 在外部文件目录下创建一个名为 "keyword.txt" 的文件
                    File newkeywordFile = new File(externalFilesDir, "keyword.txt");
                    if (!newkeywordFile.exists()) {
                        try {
                            boolean created = newkeywordFile.createNewFile();
                            if (created) {
                                XposedBridge.log("[HPBlack]keyword.txt 文件创建成功");
                            } else {
                                XposedBridge.log("[HPBlack]keyword.txt 文件创建失败");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        XposedBridge.log("[HPBlack]keyword.txt 文件已存在，无需创建");
                    }
                }
            }
        });

        XposedHelpers.findAndHookMethod(android.app.Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Hooks.init(lpparam.classLoader);
            }
        });




    }
}
