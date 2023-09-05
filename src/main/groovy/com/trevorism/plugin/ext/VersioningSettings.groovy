package com.trevorism.plugin.ext

class VersioningSettings {

    static final String NAME = "versioningSettings"
    static final String PATCH = "patch"
    static final String MINOR = "minor"
    static final String MAJOR = "major"
    static final String INITIAL_VERSION = "0.0.1"
    static final String NEXT_VERSION_KEY = "nextVersionStrategy"

    String triggeringTask = "appengineStage"
    String githubActionsDeployWorkflowPath = ".github/workflows/deploy.yml"
    String micronautApplicationPath = "src/main/groovy/com/trevorism/Application.groovy"
    String gradleBuildPath = "build.gradle"
    String readmePath = "README.md"

}
