package com.trevorism.plugin.tasks

import com.trevorism.plugin.ext.VersioningSettings
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Rewrites gradle.properties in place")
class BumpCurrentVersionTask extends DefaultTask {

    @Internal
    final RegularFileProperty gradlePropertiesFile = project.objects.fileProperty()

    @Internal
    final Property<String> currentVersion = project.objects.property(String)

    @Internal
    final Property<String> nextVersionStrategy = project.objects.property(String)

    @TaskAction
    void bumpCurrentVersion() {
        File propertiesFile = gradlePropertiesFile.get().asFile
        String version = currentVersion.getOrNull()
        if (!version) {
            logger.warn("No ${VersioningSettings.APP_VERSION_KEY} to bump. Run initializeVersion first.")
            return
        }

        String newVersion = bumpVersion(version, nextVersionStrategy.getOrElse(VersioningSettings.PATCH))
        String text = propertiesFile.text
        String updated = text.replaceFirst(/(?m)^([ \t]*${VersioningSettings.APP_VERSION_KEY}[ \t]*=[ \t]*)\S+/, "\$1${newVersion}")
        if (updated == text) {
            logger.warn("Unable to find ${VersioningSettings.APP_VERSION_KEY} in ${propertiesFile.name}")
            return
        }

        propertiesFile.text = updated
        logger.lifecycle("Bumped ${VersioningSettings.APP_VERSION_KEY} from ${version} to ${newVersion}")
    }

    static String bumpVersion(String version, String nextVersionStrategy) {
        String[] versionParts = version.split("\\.")
        String major = versionParts[0]
        String minor = versionParts[1]
        String patch = versionParts[2]

        if (nextVersionStrategy == VersioningSettings.MAJOR) {
            major = major.toInteger() + 1
            minor = "0"
            patch = "0"
        }
        if (nextVersionStrategy == VersioningSettings.MINOR) {
            minor = minor.toInteger() + 1
            patch = "0"
        }
        if (nextVersionStrategy == VersioningSettings.PATCH) {
            patch = patch.toInteger() + 1
        }
        return major + "." + minor + "." + patch
    }
}
