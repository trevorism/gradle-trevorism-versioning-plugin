package com.trevorism.plugin

import com.trevorism.plugin.ext.VersioningSettings
import com.trevorism.plugin.tasks.BumpCurrentVersionTask
import com.trevorism.plugin.tasks.InitializeVersioningTask
import com.trevorism.plugin.tasks.SyncVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author tbrooks
 */
class VersioningPlugin implements Plugin<Project> {

    private static final String VERSIONING_GROUP = "versioning"

    void apply(Project project) {
        project.apply plugin: 'java'
        project.extensions.create("versioningSettings", VersioningSettings)

        if(project.getTasksByName("appengineDeploy", false).isEmpty()){
            project.logger.warn("Appengine plugin not found. Apply the appengine plugin before the versioning plugin")
            return
        }


        project.task("initializeVersion", type: InitializeVersioningTask) {
            group = VERSIONING_GROUP
            description = "Sets some project properties for the versioning plugin"
        }
        project.task("bumpVersion", type: BumpCurrentVersionTask) {
            group = VERSIONING_GROUP
            description = "Updates the next pending version"
        }
        project.task("syncVersion", type: SyncVersionTask) {
            group = VERSIONING_GROUP
            description = "Updates relevant files to sync the project version"
            finalizedBy("bumpVersion")
        }

        project.tasks.compileJava.mustRunAfter("syncVersion")
        project.tasks.syncVersion.dependsOn("initializeVersion")
        project.tasks.appengineDeploy.dependsOn("syncVersion")
        project.tasks.bumpVersion.mustRunAfter("appengineDeploy")
    }

}
