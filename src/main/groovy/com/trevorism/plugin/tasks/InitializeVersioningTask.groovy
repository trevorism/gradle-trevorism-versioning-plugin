package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class InitializeVersioningTask extends DefaultTask{

    @OutputFile
    def gradlePropertiesFile = project.file("gradle.properties")

    @TaskAction
    void initVersionSystem(){
        def version = getInitialVersion()

        if(gradlePropertiesFile.exists()){
            def text = gradlePropertiesFile.text
            if(!text.contains("version=")){
                text << "\nversion=${version}"
            }
            if(!text.contains(VersioningSettings.NEXT_VERSION_KEY)){
                text << "\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            }
            gradlePropertiesFile.text = text
        }
        else{
            gradlePropertiesFile.createNewFile()
            String content = "version=${version}\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            gradlePropertiesFile << content
        }
        project.version = version
        project.ext."${VersioningSettings.NEXT_VERSION_KEY}" = VersioningSettings.PATCH
    }

    @Internal
    String getInitialVersion() {
        try{
            def file = project.file(project.versionSettings.githubActionsDeployWorkflowPath)
            if(!file.exists())
                return VersioningSettings.INITIAL_VERSION

            def versionContent = file.readLines().find { it.contains("version:") }
            def version = versionContent.split(":")[1].trim()
            def removeQuotes = version.replaceAll("'", "")
            def splitDashes = removeQuotes.split("-")
            return splitDashes.join(".")
        }catch (Exception ignored){
            project.logger.warn("Unable to initialize version from deploy.yml. Defaulting to ${VersioningSettings.INITIAL_VERSION}")
            return VersioningSettings.INITIAL_VERSION
        }
    }
}
