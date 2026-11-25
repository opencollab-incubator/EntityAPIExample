plugins {
    id("java")
}

group = "dev.opencollab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("org.geysermc.geyser:api:2.11.2-SNAPSHOT")
    compileOnly("org.geysermc.geyser:core:2.11.2-SNAPSHOT")
    compileOnly(files("libs/geyserutils.jar"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

// Zip src/main/pack into a .mcpack and bundle it in the plugin jar
val packageResourcePack by tasks.registering(Zip::class) {
    from("src/main/pack")
    archiveFileName.set("example_pack.mcpack")
    destinationDirectory.set(layout.buildDirectory.dir("generated/pack"))
}

tasks.processResources {
    from(packageResourcePack)
}
