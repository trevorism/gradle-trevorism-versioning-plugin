package com.trevorism.plugin.ext

class VersioningSettings {

    static final String NAME = "versioningSettings"
    static final String PATCH = "patch"
    static final String MINOR = "minor"
    static final String MAJOR = "major"
    static final String INITIAL_VERSION = "0.0.1"
    static final String APP_VERSION_KEY = "appVersion"
    static final String NEXT_VERSION_KEY = "nextVersionStrategy"

    String packageName = "com.trevorism"
    String className = "AppVersion"
    String githubActionsDeployWorkflowPath = ".github/workflows/deploy.yml"
}
