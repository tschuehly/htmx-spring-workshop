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
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("gg.jte:jte-spring-boot-starter-3:3.1.9")
    implementation("gg.jte:jte:3.1.9")
//
    implementation("de.tschuehly:spring-view-component-jte:0.7.4")
    annotationProcessor("de.tschuehly:spring-view-component-core:0.7.4")
//    implementation("org.eclipse.store:integrations-spring-boot3:1.2.0")

    implementation("net.datafaker:datafaker:2.1.0")
    implementation("io.projectreactor:reactor-core:3.6.5")

    implementation("io.github.wimdeblauwe:htmx-spring-boot:3.2.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
