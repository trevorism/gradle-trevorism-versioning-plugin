package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class InitializeVersioningTask extends DefaultTask{

    @TaskAction
    void initVersionSystem(){
        project.logger.info "Creating version and next version properties"
    }
}
