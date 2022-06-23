import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.7.0"
    id("com.github.gmazzo.buildconfig") version "3.1.0"
    id("org.beryx.jlink") version "2.25.0"
}

repositories {
    mavenCentral()
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { setUrl("https://jitpack.io") }
}

java {
    modularity.inferModulePath.set(true)
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "quanserds"
version = "1.0"

buildConfig {
    packageName("io.quanserds")
    buildConfigField("String", "kVersion", "\"$version\"")
}

application {
    mainModule.set("quanserds")
    mainClass.set("io.quanserds.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.compileJava {
    options.isIncremental = false
}

tasks.compileKotlin {
    destinationDirectory.set(tasks.compileJava.get().destinationDirectory)
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
val javafxVersion = "18.0.1"

fun javafxModule(name: String): String =
    "org.openjfx:javafx-$name:$javafxVersion:$javafxPlatform"

dependencies {
    implementation(javafxModule("base"))
    implementation(javafxModule("graphics"))
    implementation(javafxModule("controls"))
    implementation(javafxModule("fxml"))

    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.2")
}

jlink {
    launcher {
//        jvmArgs = listOf("--add-reads", "quanserds.merged.module=quanserds")
    }
    options.addAll("--strip-debug", "--no-header-files", "--no-man-pages")

    /*
    jpackage --verbose --type=msi --win-dir-chooser --win-menu --win-menu-group "Quanser Tools"
    --win-per-user-install --win-shortcut --win-upgrade-uuid "7752b28a-94ea-4b4a-8b65-9f31923ac83a"
    --java-options "--add-reads merged.module=quanserds" --app-version 20.3.2 --name "Quanser Driver
    Station" --icon icon.ico --runtime-image image --module quanserds/io.quanserds.Main
     */
}