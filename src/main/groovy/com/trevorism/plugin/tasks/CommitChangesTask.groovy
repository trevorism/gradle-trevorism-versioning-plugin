package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CommitChangesTask extends DefaultTask{

    @TaskAction
    void commitChanges() {
        project.exec {
            commandLine "git", "add", "."
        }
        project.exec {
            commandLine "git", "commit", "-m", project.versioningSettings.commitMessage
        }
        project.exec {
            commandLine "git", "push", "origin", "master"
        }
    }
}
