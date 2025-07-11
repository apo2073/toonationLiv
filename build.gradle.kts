plugins {
    id("java")
    id("maven-publish")
}

group = "com.github.apo2073"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("io.socket:socket.io-client:2.1.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
}

tasks.jar {
    archiveFileName.set("ToontaionLiv-$version.jar")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.apo2073"
            artifactId = "ToonationLiv"
            version = version

            from(components["java"])

            pom {
                name.set("ToontaionLiv")
                developers {
                    developer {
                        id="apo2073"
                        url="https://github.com/apo2073"
                    }
                }
            }
        }
    }
}


tasks.test {
    useJUnitPlatform()
}