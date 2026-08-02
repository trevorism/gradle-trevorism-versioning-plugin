package com.trevorism.plugin

import com.trevorism.plugin.ext.VersioningSettings
import com.trevorism.plugin.tasks.BumpCurrentVersionTask
import com.trevorism.plugin.tasks.GenerateVersionClassTask
import com.trevorism.plugin.tasks.InitializeVersioningTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.AbstractArchiveTask

/**
 * @author tbrooks
 */
class VersioningPlugin implements Plugin<Project> {

    private static final String VERSIONING_GROUP = "versioning"
    private static final String GENERATED_SOURCE_PATH = "generated/sources/version"

    void apply(Project project) {
        project.pluginManager.apply("java")
        project.extensions.create(VersioningSettings.NAME, VersioningSettings)

        String semanticVersion = resolveSemanticVersion(project)
        registerMaintenanceTasks(project, semanticVersion)

        if (!semanticVersion) {
            project.logger.warn("No ${VersioningSettings.APP_VERSION_KEY} found in gradle.properties. Run initializeVersion to seed one.")
            return
        }

        project.version = semanticVersion
        project.tasks.withType(AbstractArchiveTask).configureEach { it.archiveVersion.set("") }

        registerVersionClassGeneration(project, semanticVersion)
        applyDeployVersion(project, semanticVersion)
    }

    static String resolveSemanticVersion(Project project) {
        def candidate = project.findProperty(VersioningSettings.APP_VERSION_KEY) ?: project.findProperty("version")
        String value = candidate?.toString()?.trim()
        if (!value || value == "unspecified") {
            return null
        }
        return value
    }

    private static void registerMaintenanceTasks(Project project, String semanticVersion) {
        project.tasks.register("initializeVersion", InitializeVersioningTask) {
            it.group = VERSIONING_GROUP
            it.description = "Seeds ${VersioningSettings.APP_VERSION_KEY} in gradle.properties from the deploy workflow"
            it.gradlePropertiesFile.set(project.file("gradle.properties"))
            it.deployWorkflowFile.fileProvider(project.provider {
                project.file(project.extensions.getByType(VersioningSettings).githubActionsDeployWorkflowPath)
            })
        }

        project.tasks.register("bumpVersion", BumpCurrentVersionTask) {
            it.group = VERSIONING_GROUP
            it.description = "Advances ${VersioningSettings.APP_VERSION_KEY} in gradle.properties"
            it.gradlePropertiesFile.set(project.file("gradle.properties"))
            it.currentVersion.set(project.provider { semanticVersion })
            it.nextVersionStrategy.set(project.provider {
                project.findProperty(VersioningSettings.NEXT_VERSION_KEY)?.toString() ?: VersioningSettings.PATCH
            })
        }
    }

    private static void registerVersionClassGeneration(Project project, String semanticVersion) {
        def generateTask = project.tasks.register("generateVersionClass", GenerateVersionClassTask) {
            it.group = VERSIONING_GROUP
            it.description = "Generates the compile time version constant"
            it.semanticVersion.set(semanticVersion)
            it.packageName.set(project.provider { project.extensions.getByType(VersioningSettings).packageName })
            it.className.set(project.provider { project.extensions.getByType(VersioningSettings).className })
            it.outputDirectory.set(project.layout.buildDirectory.dir(GENERATED_SOURCE_PATH))
        }

        project.extensions.getByType(SourceSetContainer).named("main").configure {
            it.java.srcDir(generateTask.flatMap { task -> task.outputDirectory })
        }
    }

    private static void applyDeployVersion(Project project, String semanticVersion) {
        project.afterEvaluate {
            def appengine = project.extensions.findByName("appengine")
            if (!appengine) {
                project.logger.info("Appengine plugin not found. Skipping deploy version wiring.")
                return
            }
            String dashedVersion = GenerateVersionClassTask.toDashedVersion(semanticVersion)
            String existingVersion = appengine.deploy.version
            if (existingVersion && existingVersion != dashedVersion) {
                project.logger.lifecycle("Overriding appengine deploy version ${existingVersion} with ${dashedVersion} from ${VersioningSettings.APP_VERSION_KEY}")
            }
            appengine.deploy.version = dashedVersion
        }
    }
}
