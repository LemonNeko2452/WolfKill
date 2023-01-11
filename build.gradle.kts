plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.2"
}

group = "work.anqi"
version = "0.1.8"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
