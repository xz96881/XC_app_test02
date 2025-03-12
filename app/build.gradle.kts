// 声明 Gradle 插件（新版语法使用 alias）
plugins {
    // 使用 libs.plugins.android.application 插件（对应 com.android.application 插件）
    // 这里的 libs 是版本目录（Version Catalog），通常在 settings.gradle 中定义
    alias(libs.plugins.android.application)
}

// Android 应用的基础配置
android {
    // 应用的包名（取代了旧版的 applicationId）
    namespace = "com.zxz.xc_tset02"
    // 编译使用的 SDK 版本（建议使用最新稳定版）
    compileSdk = 35

    // 默认配置（所有构建变体共享）
    defaultConfig {
        // 应用ID（最终打包时的唯一标识，可与 namespace 不同）
        applicationId = "com.zxz.xc_tset02"
        // 最低支持的 Android 版本
        minSdk = 24  // Android 7.0 (Nougat)
        // 目标 SDK 版本（建议与 compileSdk 一致或使用最新）
        targetSdk = 35
        // 内部版本号（每次发布必须递增）
        versionCode = 1
        // 用户可见的版本号
        versionName = "1.0"

        // 测试运行器配置
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 构建类型配置（默认有 debug 和 release）
    buildTypes {
        release {
            // 是否启用代码混淆（建议 release 开启）
            isMinifyEnabled = false
            // 混淆规则文件配置
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Android 默认优化规则
                "proguard-rules.pro" // 项目自定义规则
            )
        }
        // 默认 debug 类型无需显式配置，会自动配置 debuggable=true
    }

    // Java 编译选项
    compileOptions {
        // 源代码兼容性（Java 11）
        sourceCompatibility = JavaVersion.VERSION_11
        // 生成的字节码版本（Java 11）
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// 项目依赖配置
/*它可以指定当前项目所有的依赖关系。
通常Android Studio项目一共有3种依赖方式：本地依赖、库依赖和远程依赖。
本地依赖可以对本地的jar包或目录添加依赖关系，
库依赖可以对项目中的库模块添加依赖关系，
远程依赖则可以对jcenter仓库上的开源项目添加依赖关系。
*/
dependencies {
    // 使用 libs 版本目录中的依赖（统一管理版本号）
    implementation(libs.appcompat)          // AndroidX AppCompat 库
    implementation(libs.material)           // Material Design 组件库
    implementation(libs.activity)           // AndroidX Activity 组件
    implementation(libs.constraintlayout)
    implementation(files("libs\\org.eclipse.paho.client.mqttv3-1.2.0.jar"))   // 约束布局库

    // 测试依赖
    testImplementation(libs.junit)          // JUnit 4 测试框架
    androidTestImplementation(libs.ext.junit) // AndroidX JUnit 扩展
    androidTestImplementation(libs.espresso.core) // Espresso UI 测试框架
}