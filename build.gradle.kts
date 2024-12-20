plugins {
    id("java")
}

group = "kr.apo2073"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.jsoup:jsoup:1.7.2")
    implementation("io.socket:socket.io-client:2.1.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
}

tasks.test {
    useJUnitPlatform()
}