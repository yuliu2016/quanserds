import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.4.21"
    id("com.github.gmazzo.buildconfig") version "2.0.2"
    id("org.beryx.jlink") version "2.23.1"
}

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { setUrl("https://jitpack.io") }
}

java {
    modularity.inferModulePath.set(true)
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "quanserds"
version = "1.0"

buildConfig {
    packageName("io.quanserds")
    buildConfigField("String", "kVersion", "\"$version\"")
}

application {
    mainModule.set("quanserds")
    mainClass.set("ca.warp7.rt.view.aads.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.compileKotlin {
    destinationDir = tasks.compileJava.get().destinationDir
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

fun javafxOS(): String {
    return when {
        OperatingSystem.current().isWindows -> "win"
        OperatingSystem.current().isMacOsX -> "mac"
        else -> "linux"
    }
}

val javafxPlatform = javafxOS()
val javafxVersion = "15.0.1"

fun javafxModule(name: String): String =
    "org.openjfx:javafx-$name:$javafxVersion:$javafxPlatform"

dependencies {
    implementation(javafxModule("base"))
    implementation(javafxModule("graphics"))
    implementation(javafxModule("controls"))
    implementation(javafxModule("fxml"))

    implementation("org.kordamp.ikonli:ikonli-javafx:12.0.0")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.7.0")
}

jlink {
    launcher {
//        jvmArgs = listOf("--add-reads", "quanserds.merged.module=quanserds")
    }
    options.addAll("--strip-debug", "--no-header-files", "--no-man-pages")
}