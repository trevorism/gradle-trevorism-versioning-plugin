package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class BumpCurrentVersionTask extends DefaultTask{

    @TaskAction
    void bumpCurrentVersion(){
        String version = project.version;
        String nextVersionStrategy = project.nextVersionStrategy

        String newVersion = bumpVersion(version, nextVersionStrategy)

        String gradlePropertiesText = project.file("gradle.properties").text
        String newText = gradlePropertiesText.replaceAll("version=${version}", "version=${newVersion}")

        project.file("gradle.properties").text = newText
    }

    private static String bumpVersion(String version, String nextVersionStrategy) {
        String[] versionParts = version.split("\\.");
        String major = versionParts[0]
        String minor = versionParts[1]
        String patch = versionParts[2]

        if (nextVersionStrategy == "major") {
            major = major.toInteger() + 1
            minor = "0"
            patch = "0"
        }
        if (nextVersionStrategy == "minor") {
            minor = minor.toInteger() + 1
            patch = "0"
        }
        if (nextVersionStrategy == "patch") {
            patch = patch.toInteger() + 1
        }
        String newVersion = major + "." + minor + "." + patch
        newVersion
    }
}
