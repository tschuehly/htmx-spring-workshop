plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "de.tschuehly"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // LAB 1:
    implementation("org.springframework.boot:spring-boot-starter-web")

    // LAB 2:
    implementation("de.tschuehly:spring-view-component-jte:0.7.4")
    annotationProcessor("de.tschuehly:spring-view-component-core:0.7.4")
    implementation("io.github.wimdeblauwe:htmx-spring-boot:3.2.0")

    // LAB 9:
    implementation("io.projectreactor:reactor-core:3.6.5")

    implementation("net.datafaker:datafaker:2.1.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
