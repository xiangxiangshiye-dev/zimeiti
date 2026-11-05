# 湘湘视野 Android 创作平台

## 项目简介

这是一个基于 Android WebView 的本地自媒体创作工具，品牌名称为「湘湘视野」。

品牌签名：行走，发现热爱生活。

## 当前功能

- 文章、视频、图片、音乐等创作入口
- 热点选题与创作提示
- 作品本地保存、搜索、编辑、删除和导出
- 发布计划管理
- 创作统计
- 品牌资料设置
- JSON 数据备份与恢复
- 图片封面生成及安卓文件保存接口

## 技术方案

- Android 原生外壳：Kotlin
- 页面：HTML、CSS、JavaScript
- 本地数据：localStorage
- 应用包名：com.xiangxiang.creator
- compileSdk：35
- minSdk：23
- Java：17
- Android Gradle Plugin：8.6.1
- GitHub Actions：Gradle 8.7 自动编译

## 编译方式

在 GitHub 仓库打开 Actions，选择 Build XiangXiang Creator APK，运行工作流。

成功后，在运行记录的 Artifacts 中下载 xiangxiang-creator-apk。

APK 输出路径：
app/build/outputs/apk/debug/app-debug.apk

## 数据说明

当前作品和计划数据保存在本机应用数据中。卸载应用或清除应用数据前，请先导出 JSON 备份。

## 项目目录

- app/src/main/assets/index.html：创作工具页面
- app/src/main/java/com/xiangxiang/bookkeeping/MainActivity.kt：Android WebView 主程序
- .github/workflows/build-apk.yml：自动编译配置
- backup/：本地修改备份，不上传 GitHub
