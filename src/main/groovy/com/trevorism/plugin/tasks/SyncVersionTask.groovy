package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SyncVersionTask extends DefaultTask{

    @TaskAction
    void syncVersions(){
        project.logger.info "Synchronizing versions"
    }
}
