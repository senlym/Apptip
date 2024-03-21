plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "Avange.apptip"
    compileSdk = 34

    defaultConfig {
        applicationId = "Avange.apptip"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources.excludes.add("META-INF/NOTICE.md")
        resources.excludes.add("META-INF/*")
    }
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("androidx.navigation:navigation-runtime:2.7.7")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.googlecode.json-simple:json-simple:1.1.1")
    implementation ("com.sun.mail:jakarta.mail:2.0.1")
    implementation ("com.jsibbold:zoomage:1.3.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation(files("libs\\dotsloader-1.4.2.aar"))
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.hamcrest:hamcrest-core:1.1")).using(module("junit:junit:4.10"))
            substitute(module("org.hamcrest:hamcrest-core:1.1")).using(module("junit:junit:4.10"))
        }
}
}