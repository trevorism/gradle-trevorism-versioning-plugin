package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class InitializeVersioningTask extends DefaultTask{

    @InputFile
    def gradlePropertiesFile = project.file("gradle.properties")

    @TaskAction
    void initVersionSystem(){
        def version = getInitialVersion()
        project.logger.info("Initializing version to ${version}")

        if(gradlePropertiesFile.exists()){
            project.logger.info("Gradle properties file already exists. Updating it.")
            def text = gradlePropertiesFile.text
            if(!text.contains("version=")){
                project.logger.info("Adding version to gradle properties file")
                text += "\nversion=${version}"
            }
            if(!text.contains(VersioningSettings.NEXT_VERSION_KEY)){
                project.logger.info("Adding next version strategy to gradle properties file")
                text += "\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            }
            project.logger.info("Setting properties file text to ${text}")

            gradlePropertiesFile.text = text
        }
        else{
            gradlePropertiesFile.createNewFile()
            String content = "version=${version}\n${VersioningSettings.NEXT_VERSION_KEY}=${VersioningSettings.PATCH}"
            project.logger.info("Creating a new gradle.properties file with content ${content}")
            gradlePropertiesFile << content
        }
        project.version = version
        project.ext."${VersioningSettings.NEXT_VERSION_KEY}" = VersioningSettings.PATCH
    }

    @Internal
    String getInitialVersion() {
        try{
            def file = project.file(project.versioningSettings.githubActionsDeployWorkflowPath)
            String versionFromProperties = project.version.toString()
            if(versionFromProperties == "unspecified")
                versionFromProperties = null
            if(!file.exists())
                return versionFromProperties ?: VersioningSettings.INITIAL_VERSION

            def versionContent = file.readLines().find { it.contains("version:") }
            def version = versionContent.split(":")[1].trim()
            def removeQuotes = version.replaceAll("'", "")
            def splitDashes = removeQuotes.split("-")
            return splitDashes.join(".")
        }catch (Exception e){
            project.logger.warn("Unable to initialize version from deploy.yml. Defaulting to ${VersioningSettings.INITIAL_VERSION}", e)
            return VersioningSettings.INITIAL_VERSION
        }
    }
}
