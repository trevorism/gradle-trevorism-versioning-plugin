package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SyncVersionTask extends DefaultTask {

    @TaskAction
    void syncVersions() {
        VersioningSettings settings = project.versioningSettings
        UpdateVersionWithinFile(settings.githubActionsDeployWorkflowPath, ":", "'", "", true)
        UpdateVersionWithinFile(settings.micronautApplicationPath, "=", "\"", ",")
        UpdateVersionWithinFile(settings.gradleBuildPath, "=", "\"", "", true)
        UpdateVersionWithinFile(settings.readmePath, ":", "", "")
        UpdateAppYaml()
    }

    void UpdateVersionWithinFile(String path, String matchCharacter, String quoteChar, String suffix, boolean convertDotToDash = false) {
        File file = project.file(path)
        List<String> fileLines = file.readLines()
        fileLines = replaceVersionOnLine(fileLines, matchCharacter, quoteChar, suffix, path, convertDotToDash)
        file.text = fileLines.join("\n")
    }

    private static String convertDotToDash(String version) {
        return version.replaceAll("\\.", "-")
    }

    List<String> replaceVersionOnLine(List<String> fileLines, String matchCharacter, String quoteChar, String suffix, String filePath, boolean convertToDash = false) {
        int indexOfVersion = -1
        String foundLine = ""
        for (String line : fileLines) {
            if (line.contains("version") && line.contains(matchCharacter)) {
                indexOfVersion = fileLines.indexOf(line)
                foundLine = line
            }
        }
        if (indexOfVersion == -1) {
            project.logger.warn("Unable to sync ${filePath}")
            return fileLines
        }

        String newLine = foundLine.substring(0, foundLine.indexOf(matchCharacter) + 1) + " ${quoteChar}${convertToDash ? convertDotToDash(project.version) : project.version}${quoteChar}${suffix}"
        fileLines.set(indexOfVersion, newLine)
        return fileLines
    }

    void UpdateAppYaml() {
        String path = "src/main/appengine/app.yaml"
        File file = project.file(path)
        List<String> fileLines = file.readLines()
        int indexOfEntrypoint = -1
        for (String line : fileLines) {
            if (line.startsWith("entrypoint: java -jar")) {
                indexOfEntrypoint = fileLines.indexOf(line)
            }
        }
        if (indexOfEntrypoint == -1) {
            project.logger.warn("Unable to sync app.yaml")
        }
        String newLine = "entrypoint: java -jar ${project.name}-${project.version}-all.jar"
        fileLines.set(indexOfEntrypoint, newLine)
        file.text = fileLines.join("\n")
    }
}
