plugins {
    id("java")
}

group = "dev.opencollab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    compileOnly(files("libs/geyserutils-spigot.jar"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
