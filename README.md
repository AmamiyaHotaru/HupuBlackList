# 虎扑黑名单/HupuBlackList
一款lsposed/xposed插件，为虎扑添加本地拉黑功能（隐藏用户发帖）
## :warning: 注意 :warning:
练习时长两天半做出来的模块，使用时能正常运行的部分全是GPT完成，出现的BUG全是我导致的。

因此在使用时可能出现以下情况：

**未响应、闪退和~~牢大复活~~等**

如遇问题请停用并卸载模块并寻求其他解决方案

**如果首次不生效，尝试完全停止虎扑再运行**
## :warning: 已知问题 :warning:
## 未root的真机使用修补后的虎扑apk，第一次无法加入黑名单，手动进模块设置界面后退出就行。

## 已实现功能
- [x] 隐藏拉黑用户的专区发帖
- [ ] 折叠拉黑用户的专区回复
- [ ] 隐藏拉黑用户的首页推荐

## 安装
### :warning: 未ROOT用户使用需要卸载旧的虎扑 :warning:
### 方法一.下载模块自行安装或修补
### 方法二.使用修补好的虎扑安装包

## 使用
### ①进入需要加入黑名单用户的资料页面，点击用户名，弹出拉黑提示，拉黑用户字体颜色为红色，再次点击则取消拉黑。

<img src="https://github.com/AmamiyaHotaru/HupuBlackList/blob/main/img/Screenshot_20240409_183445.jpg" alt="Image text" width="50%" />

### ②点击“我的”导航栏右上角的设置按钮，进入设置界面，点击“黑名单”菜单项进入黑名单管理界面。

<img src="https://github.com/AmamiyaHotaru/HupuBlackList/blob/main/img/Screenshot_20240409_183459.jpg" alt="Image text" width="50%" />

### ③在黑名单管理界面可以进行手动添加、删除（**长按列表中的用户名**）操作。

## 鸣谢
* [Xposed](https://github.com/rovo89/Xposed)
* [LSPosed](https://github.com/LSPosed/LSPosed)
* [MultiprocessSharedPreferences](https://github.com/seven456/MultiprocessSharedPreferences)
