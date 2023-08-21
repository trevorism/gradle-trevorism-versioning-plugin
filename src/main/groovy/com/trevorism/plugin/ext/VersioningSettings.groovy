package com.trevorism.plugin.ext

class VersioningSettings {

    String triggeringTask = "appengineStage"
    String githubActionsDeployWorkflowPath = ".github/workflows/deploy.yml"
    String micronautApplicationPath = "src/main/groovy/com/trevorism/Application.groovy"
    String gradleBuildPath = "build.gradle"
    String readmePath = "README.md"

}
