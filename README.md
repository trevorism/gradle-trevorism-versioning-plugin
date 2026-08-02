# gradle-trevorism-versioning-plugin
![Build](https://github.com/trevorism/gradle-trevorism-versioning-plugin/actions/workflows/build.yml/badge.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/gradle-trevorism-versioning-plugin)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/gradle-trevorism-versioning-plugin)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/gradle-trevorism-versioning-plugin)

Drives version of Trevorism Webapps from a single version property

Current [Version](https://github.com/trevorism/gradle-trevorism-versioning-plugin/releases/latest)

## Usage

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            url uri("https://maven.pkg.github.com/trevorism/gradle-trevorism-versioning-plugin")
            credentials {
                username = findProperty("github.user") ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    dependencies {
        classpath 'com.trevorism:gradle-trevorism-versioning-plugin:1.0.0'
    }
}

apply plugin: "com.trevorism.gradle.versioning"
```

The github username and PAT should be supplied as credentials.

## What it does

The version lives in exactly one place, `appVersion` in `gradle.properties`:

```properties
appVersion=1.4.0
```

From that the plugin:

- sets `project.version`, while pinning every archive name so the jar stays `<name>-all.jar`
  rather than gaining a version segment that would break `appengine.stage.artifact` and the
  `entrypoint` in `app.yaml`
- generates `com.trevorism.AppVersion` into the main source set before compilation
- sets `appengine.deploy.version` to the dashed form

The generated class carries both forms, so the remaining version literals in a service can be
deleted:

```java
public final class AppVersion {
    public static final String SEMVER = "1.4.0";
    public static final String DASHED = "1-4-0";
}
```

```groovy
@OpenAPIDefinition(info = @Info(version = AppVersion.SEMVER))

@Get(value = "/version", produces = MediaType.TEXT_PLAIN)
String version() {
    return AppVersion.DASHED
}
```

Groovy resolves the Java constant at compile time, so it is a valid annotation attribute.

The version id used by the deploy workflow comes from the same property. `pipeline.yml` in
`trevorism/actions-workflows` reads `appVersion` from `gradle.properties` whenever the caller
supplies no explicit `version:` input.

If `appVersion` is absent the plugin warns and stays inert, so it is safe to apply before a repo
has finished migrating.

## Tasks

| Task | Purpose |
| --- | --- |
| `generateVersionClass` | Writes the version constant. Runs automatically before compilation. |
| `initializeVersion` | Seeds `appVersion` from the `version:` literal in `.github/workflows/deploy.yml`. Use once per repo when migrating. |
| `bumpVersion` | Advances `appVersion` by `nextVersionStrategy` (`patch`, `minor` or `major`). Not wired into the build. |

## Settings

```groovy
versioningSettings {
    packageName = "com.trevorism"
    className = "AppVersion"
    githubActionsDeployWorkflowPath = ".github/workflows/deploy.yml"
}
```

## About

About me -- https://www.trevorism.com