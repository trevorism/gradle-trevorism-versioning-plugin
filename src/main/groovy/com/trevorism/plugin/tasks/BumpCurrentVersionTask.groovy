package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class BumpCurrentVersionTask extends DefaultTask{

    @TaskAction
    void bumpCurrentVersion(){
        project.logger.info "Bumping current version"
    }
}
