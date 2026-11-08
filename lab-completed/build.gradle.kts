import kotlin.io.path.Path
plugins {
    java
    id("org.springframework.boot") version "4.0.9"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.5"
    id("gg.jte.gradle") version("3.2.5")
}

group = "de.tschuehly"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

jte{
    generate()
    sourceDirectory = Path("src/main/java")
    // registers the generated template classes for GraalVM reflection
    jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
}
dependencies {
    // LAB 1:
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // LAB 2:
    implementation("de.tschuehly:spring-view-component-jte:0.9.1")
    implementation("io.github.wimdeblauwe:htmx-spring-boot:5.2.0")
    jteGenerate("gg.jte:jte-native-resources:3.2.5")

    // LAB 9:
    implementation("io.projectreactor:reactor-core")

    implementation("net.datafaker:datafaker:2.7.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
