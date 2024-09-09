package cn.amamiya.hupublacklist.hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
                        Object blacklistSingleView = constructor.newInstance(setupActivity, "黑名单");
                        Object keywordSingleView = constructor.newInstance(setupActivity, "屏蔽词");
                        ViewGroup blacklistSingleViewGroup = (ViewGroup) blacklistSingleView;
                        ViewGroup keywordSingleViewGroup = (ViewGroup) keywordSingleView;

                        // 获取目标应用程序的外部文件目录，读取文件
                        File externalFilesDir = setupActivity.getExternalFilesDir(null);
                        if (externalFilesDir == null) {
                            Toast.makeText(setupActivity,"获取数据目录出错，可以尝试重装APP",Toast.LENGTH_LONG).show();
                            return;
                        }
                        File blacklistFile = new File(externalFilesDir, "blacklist.txt");
                        String blockUsersString = FileHelper.readFileToString(blacklistFile);

                        File keywordFile = new File(externalFilesDir, "keyword.txt");



                        List<String> list = blockUsersString.isEmpty()?new ArrayList<>():new ArrayList<>(Arrays.asList(blockUsersString.split(",")));

                        // 黑名单
                        blacklistSingleViewGroup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                LinearLayout layout = new LinearLayout(setupActivity);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                // 创建 RecyclerView
                                RecyclerView recyclerView = new RecyclerView(setupActivity);
                                recyclerView.setLayoutManager(new LinearLayoutManager(setupActivity));
                                recyclerView.setAdapter(new MyRecyclerViewAdapter(setupActivity,list,blacklistFile));

                                // 将 RecyclerView 添加到布局中
                                layout.addView(recyclerView);

                                // 创建并显示对话框
                                AlertDialog.Builder builder = new AlertDialog.Builder(setupActivity);
                                builder.setView(layout);
                                builder.setTitle("黑名单列表");
                                builder.setPositiveButton("返回", null);
                                builder.show();

                            }
                        });

                        // 关键词
                        keywordSingleViewGroup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // 获取屏蔽词列表
                                String keywordString = FileHelper.readFileToString(keywordFile);

                                // 创建一个布局容器
                                LinearLayout layout = new LinearLayout(setupActivity);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                // 创建一个 EditText 用于用户输入
                                EditText editText = new EditText(setupActivity);
                                editText.setHint("请输入需要屏蔽的关键词");
                                editText.setText(keywordString);
                                // 将 EditText 添加到布局中
                                layout.addView(editText);

                                // 创建并显示对话框
                                AlertDialog.Builder builder = new AlertDialog.Builder(setupActivity);
                                builder.setView(layout);
                                builder.setTitle("输入关键词,用英文逗号隔开");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 在这里处理用户输入的关键词
                                        String keywords = editText.getText().toString();
                                        FileHelper.modifyFileContent(keywordFile,keywords);
                                        XposedBridge.log("[HPBlack] 当前屏蔽词列表是：" + keywords);


                                    }
                                });
                                builder.setNegativeButton("取消", null);
                                builder.show();
                            }
                        });

                        result.addView((View) blacklistSingleViewGroup);
                        result.addView((View) keywordSingleViewGroup);
                        param.setResult(result);


                    }
                });
    }
}
