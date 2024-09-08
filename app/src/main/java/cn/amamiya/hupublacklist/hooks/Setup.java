package cn.amamiya.hupublacklist.hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.amamiya.hupublacklist.adapter.MyRecyclerViewAdapter;
import cn.amamiya.hupublacklist.utils.FileHelper;
import de.robv.android.xposed.XC_MethodHook;
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
                        // 获取目标应用程序的外部文件目录
                        File externalFilesDir = setupActivity.getExternalFilesDir(null);
                        if (externalFilesDir == null) {
                            Toast.makeText(setupActivity,"获取数据目录出错，可以尝试重装APP",Toast.LENGTH_LONG).show();
                            return;
                        }

                        File blacklist = new File(externalFilesDir, "blacklist.txt");
                        String blockUsersString = FileHelper.readFileToString(blacklist);

                        List<String> list = blockUsersString.isEmpty()?new ArrayList<>():new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        setupSingleView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                LinearLayout layout = new LinearLayout(setupActivity);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                // 创建 RecyclerView
                                RecyclerView recyclerView = new RecyclerView(setupActivity);
                                recyclerView.setLayoutManager(new LinearLayoutManager(setupActivity));
                                recyclerView.setAdapter(new MyRecyclerViewAdapter(setupActivity,list,blacklist));

                                // 将 RecyclerView 添加到布局中
                                layout.addView(recyclerView);

                                // 创建并显示对话框
                                AlertDialog.Builder builder = new AlertDialog.Builder(setupActivity);
                                builder.setView(layout);
                                builder.setTitle("黑名单列表");
                                builder.setPositiveButton("返回", null);
                                builder.show();


//                                Intent intent = new Intent();
//                                intent.setComponent(new ComponentName("cn.amamiya.hupublacklist", "cn.amamiya.hupublacklist.SettingActivity"));
//
//                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                setupActivity.startActivity(intent);
                            }
                        });
                        result.addView((View) setupSingleView);
                        param.setResult(result);


                    }
                });
    }
}
