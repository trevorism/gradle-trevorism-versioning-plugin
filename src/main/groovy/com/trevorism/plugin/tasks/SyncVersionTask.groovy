package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SyncVersionTask extends DefaultTask {

    @TaskAction
    void syncVersions() {
        VersioningSettings settings = project.versioningSettings
        Sync(settings.githubActionsDeployWorkflowPath, ":", "'", true)
        Sync(settings.micronautApplicationPath, "=", "\"")
        Sync(settings.gradleBuildPath, "=", "\"")
        Sync(settings.readmePath, ":", "")
    }

    void Sync(String path, String matchCharacter, String quoteChar, boolean convertDotToDash = false) {
        File file = project.file(path)
        List<String> fileLines = file.readLines()

        fileLines = replaceVersionLine(fileLines, matchCharacter, quoteChar, path, convertDotToDash)
        file.text = fileLines.join("\n")
    }

    private static String convertDotToDash(String version) {
        return version.replaceAll("\\.", "-")
    }

    List<String> replaceVersionLine(List<String> fileLines, String matchCharacter, String quoteChar, String filePath, boolean convertToDash = false) {
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

        String newLine = foundLine.substring(0, foundLine.indexOf(matchCharacter) + 1) + " ${quoteChar}${convertToDash ? convertDotToDash(project.version) : project.version}${quoteChar}"
        fileLines.set(indexOfVersion, newLine)
        return fileLines
    }

}
