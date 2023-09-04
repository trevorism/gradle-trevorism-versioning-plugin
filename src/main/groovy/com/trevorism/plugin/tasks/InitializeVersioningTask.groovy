package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class InitializeVersioningTask extends DefaultTask {

    @InputFile
    def gradlePropertiesFile = project.file("gradle.properties")

    @TaskAction
    void initVersionSystem() {
        boolean initialized = true
        if (project.version == "unspecified" || !project.hasProperty(VersioningSettings.NEXT_VERSION_KEY)) {
            initialized = false
        }
        String version = getInitialVersion()
        project.logger.info("Initializing version to ${version}")

        if (gradlePropertiesFile.exists()) {
            def text = gradlePropertiesFile.text
            if (!text.contains("version=")) {
                text += "\nversion=${version}"
            }
            if (!text.contains(VersioningSettings.NEXT_VERSION_KEY)) {
                text += "\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            }
            project.logger.info("Setting properties file text to ${text}")
            gradlePropertiesFile.text = text
        } else {
            gradlePropertiesFile.createNewFile()
            String content = "version=${version}\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            project.logger.info("Creating a new gradle.properties file with content ${content}")
            gradlePropertiesFile.text = content
        }
        if(!initialized){
            throw new GradleException("Versioning system was not initialized. Please re-run build.")
        }
    }

    @Internal
    String getInitialVersion() {
        try {
            def file = project.file(project.versioningSettings.githubActionsDeployWorkflowPath)
            if (!file.exists())
                return VersioningSettings.INITIAL_VERSION

            def versionContent = file.readLines().find { it.contains("version:") }
            def version = versionContent.split(":")[1].trim()
            def removeQuotes = version.replaceAll("'", "")
            def splitDashes = removeQuotes.split("-")
            return splitDashes.join(".")
        } catch (Exception e) {
            project.logger.warn("Unable to initialize version from deploy.yml. Defaulting to ${VersioningSettings.INITIAL_VERSION}", e)
            return VersioningSettings.INITIAL_VERSION
        }
    }
}
