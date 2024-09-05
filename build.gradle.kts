import gg.jte.html.HtmlPolicy
import kotlin.io.path.Path

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    id("gg.jte.gradle") version("3.1.12")
}

group = "de.tschuehly"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}


repositories {
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
    mavenLocal()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("gg.jte:jte-spring-boot-starter-3:3.1.9")
    implementation("com.github.casid.jte:jte:e8fca09303")
//

    implementation("de.tschuehly:spring-view-component-jte:0.8.3")
//    implementation("org.eclipse.store:integrations-spring-boot3:1.2.0")

    implementation("io.github.wimdeblauwe:htmx-spring-boot:3.2.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


jte{
    generate()
    sourceDirectory.set(Path("src/main/java"))
}