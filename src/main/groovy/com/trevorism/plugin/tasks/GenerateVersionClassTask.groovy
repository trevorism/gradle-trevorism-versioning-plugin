package com.trevorism.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Writing a single small source file is cheaper than a cache round trip")
class GenerateVersionClassTask extends DefaultTask {

    @Input
    final Property<String> semanticVersion = project.objects.property(String)

    @Input
    final Property<String> packageName = project.objects.property(String)

    @Input
    final Property<String> className = project.objects.property(String)

    @OutputDirectory
    final DirectoryProperty outputDirectory = project.objects.directoryProperty()

    @TaskAction
    void generateVersionClass() {
        String semver = semanticVersion.get()
        String dashed = toDashedVersion(semver)
        String type = className.get()
        String namespace = packageName.get()

        File packageDirectory = new File(outputDirectory.get().asFile, namespace.replace(".", "/"))
        packageDirectory.mkdirs()

        new File(packageDirectory, "${type}.java").text = """package ${namespace};

public final class ${type} {

    public static final String SEMVER = "${semver}";
    public static final String DASHED = "${dashed}";

    private ${type}() {
    }
}
"""
    }

    static String toDashedVersion(String version) {
        return version.replace(".", "-")
    }
}
