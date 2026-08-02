package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Rewrites gradle.properties in place")
class InitializeVersioningTask extends DefaultTask {

    @Internal
    final RegularFileProperty gradlePropertiesFile = project.objects.fileProperty()

    @Internal
    final RegularFileProperty deployWorkflowFile = project.objects.fileProperty()

    @TaskAction
    void initializeVersioning() {
        File propertiesFile = gradlePropertiesFile.get().asFile
        if (hasAppVersion(propertiesFile)) {
            logger.lifecycle("${VersioningSettings.APP_VERSION_KEY} is already present in ${propertiesFile.name}")
            return
        }

        String version = readVersionFromWorkflow(deployWorkflowFile.asFile.getOrNull())
        appendAppVersion(propertiesFile, version)
        logger.lifecycle("Seeded ${VersioningSettings.APP_VERSION_KEY}=${version} into ${propertiesFile.name}")
    }

    static boolean hasAppVersion(File propertiesFile) {
        if (!propertiesFile.exists()) {
            return false
        }
        return propertiesFile.readLines().any { it =~ /^\s*${VersioningSettings.APP_VERSION_KEY}\s*=/ }
    }

    static String readVersionFromWorkflow(File workflowFile) {
        if (!workflowFile || !workflowFile.exists()) {
            return VersioningSettings.INITIAL_VERSION
        }
        String line = workflowFile.readLines().find { it =~ /^\s*version\s*:/ }
        if (!line) {
            return VersioningSettings.INITIAL_VERSION
        }
        String value = line.substring(line.indexOf(":") + 1).replace("'", "").replace('"', "").trim()
        return value ? value.replace("-", ".") : VersioningSettings.INITIAL_VERSION
    }

    static void appendAppVersion(File propertiesFile, String version) {
        String existing = propertiesFile.exists() ? propertiesFile.text : ""
        String separator = (!existing || existing.endsWith("\n")) ? "" : "\n"
        propertiesFile.text = "${existing}${separator}${VersioningSettings.APP_VERSION_KEY}=${version}\n"
    }
}
