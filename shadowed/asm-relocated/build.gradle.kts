plugins {
  `java-library`
  id("com.github.johnrengelman.shadow")
  id("convention")
  // This project doesn't need Kotlin, but it is now applied thanks to `convention`. problem?
}

version = "9.6.0.0"

val isSnapshot = version.toString().endsWith("SNAPSHOT", true)
val VERSION_ASM = "9.6"

dependencies {
  implementation("org.ow2.asm:asm:$VERSION_ASM")
  implementation("org.ow2.asm:asm-tree:$VERSION_ASM")
}

configurations.all {
  resolutionStrategy {
    eachDependency {
      if (requested.group == "org.ow2.asm") {
        useVersion(VERSION_ASM)
      }
    }
  }
}

dagp {
  version(version)
  pom {
    name.set("asm, relocated")
    description.set("asm, relocated")
    inceptionYear.set("2022")
  }
  publishTaskDescription("Publishes to Maven Central and promotes.")
}

tasks.shadowJar {
  archiveClassifier.set("")
  relocate("org.objectweb.asm", "com.autonomousapps.internal.asm")
}

val javaComponent = components["java"] as AdhocComponentWithVariants
listOf("apiElements", "runtimeElements").forEach { unpublishable ->
  javaComponent.withVariantsFromConfiguration(configurations[unpublishable]) {
    skip()
  }
}
