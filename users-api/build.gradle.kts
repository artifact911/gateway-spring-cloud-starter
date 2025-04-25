plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("plugin.jpa") version "2.2.0-Beta1" // для автоматического открытия классов
    kotlin("plugin.noarg") version "2.2.0-Beta1" // если нужен пустой конструктор без дефолтных значений
}

group = "com.artifact"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    // Spring использует рефлексию Kotlin для работы с классами, и без этой зависимости возникает ошибка UnsatisfiedDependencyException
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}