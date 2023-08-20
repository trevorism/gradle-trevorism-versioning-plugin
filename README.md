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
            url uri("https://maven.pkg.github.com/trevorism/gradle-release-plugin")
            credentials {
                username = findProperty("github.user") ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    dependencies {
        classpath 'com.trevorism:gradle-release-plugin:0.7.0'
    }
}

apply plugin: "com.trevorism.gradle.versioning"
```

The github username and PAT should be supplied as credentials.

## About

About me -- https://www.trevorism.com