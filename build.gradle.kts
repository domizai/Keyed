import java.util.Properties

plugins {
    `java-library`
}

val libName = "Keyed"
group = "ch.domizai"

val libraryProperties = Properties().apply {
    rootProject.file("release.properties").inputStream().use { load(it) }
}

// A GitHub release tag like "v1.2.0" overrides prettyVersion.
version = findProperty("githubReleaseTag")?.toString()?.removePrefix("v")
    ?: libraryProperties.getProperty("prettyVersion").trim()

java {
    // Processing 4 runs on Java 17.
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    // Processing core depends on JOGL, which is only published here.
    maven { url = uri("https://jogamp.org/deployment/maven/") }
}

dependencies {
    // Provided by Processing at runtime, so not bundled.
    compileOnly(libs.processing.core)
}

tasks.jar {
    archiveBaseName = libName
    archiveVersion = ""
    archiveClassifier = ""
    // Read back by Keyed.VERSION.
    manifest {
        attributes("Implementation-Version" to project.version.toString())
    }
}

tasks.javadoc {
    // Docs are intentionally short, without @param/@return on every method.
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

// ---------------------------------------------------------------------------
// Release: ./gradlew buildReleaseArtifacts writes release/Keyed.zip, .pdex and .txt
// ---------------------------------------------------------------------------

val releaseDir = layout.projectDirectory.dir("release")

// Written as plain key=value: WriteProperties would escape ':' in URLs, which Processing doesn't unescape.
val writeLibraryProperties = tasks.register("writeLibraryProperties") {
    group = "processing"
    val out = layout.buildDirectory.file("release/library.properties")
    val text = listOf("name", "version", "authors", "url", "categories",
                      "sentence", "paragraph", "minRevision", "maxRevision")
        .joinToString("") { "$it=${libraryProperties.getProperty(it, "").trim()}\n" } +
        "prettyVersion=${project.version}\n"
    inputs.property("text", text)
    outputs.file(out)
    doLast { out.get().asFile.writeText(text) }
}

// The folder layout Processing expects inside libraries/Keyed.
val stageRelease = tasks.register<Sync>("stageRelease") {
    group = "processing"
    into(layout.buildDirectory.dir("release/$libName"))
    from(tasks.jar) { into("library") }
    from(tasks.javadoc) { into("reference") }
    from(writeLibraryProperties)
    from(layout.projectDirectory) {
        include("README.md", "LICENSE.txt", "examples/**", "src/main/**")
        exclude("**/.DS_Store")
    }
}

val packageRelease = tasks.register<Zip>("packageRelease") {
    group = "processing"
    from(stageRelease) { into(libName) }
    archiveFileName = "$libName.zip"
    destinationDirectory = layout.buildDirectory.dir("release")
}

tasks.register<Copy>("buildReleaseArtifacts") {
    group = "processing"
    description = "Builds Keyed.zip, Keyed.pdex and Keyed.txt in release/."
    into(releaseDir)
    from(packageRelease)
    // Regex renames instead of lambdas, so the configuration cache can store this task.
    from(packageRelease) { rename(".+", "$libName.pdex") }
    from(writeLibraryProperties) { rename(".+", "$libName.txt") }
}
