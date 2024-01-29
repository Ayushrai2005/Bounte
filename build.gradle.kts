// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    id ("com.android.library") version "8.1.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

}


buildscript {

    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }

    dependencies {
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath ("com.android.tools.build:gradle:4.1.0")
    }
}