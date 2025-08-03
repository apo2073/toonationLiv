plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "com.github.apo2073"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("io.socket:socket.io-client:2.1.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
    implementation(files("libs\\ToontaionLiv-1.1.jar"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.github.apo2073.Main"
    }
    minimize()
}

tasks.test {
    useJUnitPlatform()
}