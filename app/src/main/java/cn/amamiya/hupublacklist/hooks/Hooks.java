package cn.amamiya.hupublacklist.hooks;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hooks {
    static final IHook[] hooks = {
            new TopicList(),
            new Personal(),
            new Setup(),
            new PostDetail()
    };

    public static void init(ClassLoader classLoader){
        for (IHook hook : hooks) {
            try {
                hook.hook(classLoader);
            } catch (Throwable e) {
                XposedBridge.log("[HPBlack] " + hook.getHookName()+"hook失败");
                XposedBridge.log("[HPBlack] " + e);
            }
        }
    }
}
