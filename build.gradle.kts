plugins {
    id("java")
}

group = "dev.opencollab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/")
    maven(url = "https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    compileOnly("org.geysermc.geyser:api:2.11.2-SNAPSHOT")
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
