plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.glycin"
version = project.findProperty("deployVersion") ?: "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.9.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }

    jvmToolchain(21)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    bootBuildImage {
        imageName.set("europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/persistence-service:$version")

        // Add the OpenTelemetry build pack to include the Java agent.
        // Unfortunately, if we just want to add 1 build pack, we have to specify them all...
        buildpacks.addAll(
            "paketo-buildpacks/ca-certificates",
            "paketo-buildpacks/bellsoft-liberica",
            "paketo-buildpacks/syft",
            "paketo-buildpacks/executable-jar",
            "paketo-buildpacks/dist-zip",
            "paketo-buildpacks/spring-boot",
            "gcr.io/paketo-buildpacks/opentelemetry",
        )
        environment.put("BP_OPENTELEMETRY_ENABLED", "true")
    }
}
