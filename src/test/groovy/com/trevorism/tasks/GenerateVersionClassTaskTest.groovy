package com.trevorism.tasks

import com.trevorism.plugin.VersioningPluginTest
import com.trevorism.plugin.tasks.GenerateVersionClassTask
import org.gradle.api.Project
import org.junit.jupiter.api.Test

class GenerateVersionClassTaskTest {

    @Test
    void testToDashedVersion() {
        assert "1-2-3" == GenerateVersionClassTask.toDashedVersion("1.2.3")
        assert "0-10-0" == GenerateVersionClassTask.toDashedVersion("0.10.0")
    }

    @Test
    void testGeneratesConstantsFile() {
        Project project = VersioningPluginTest.createProject()
        GenerateVersionClassTask task = project.tasks.generateVersionClass
        task.generateVersionClass()

        File generated = new File(task.outputDirectory.get().asFile, "com/trevorism/AppVersion.java")
        assert generated.exists()

        String text = generated.text
        assert text.contains("package com.trevorism;")
        assert text.contains("public final class AppVersion")
        assert text.contains('public static final String SEMVER = "1.2.3";')
        assert text.contains('public static final String DASHED = "1-2-3";')
        assert text.contains("private AppVersion()")
    }

    @Test
    void testHonorsCustomPackageAndClassName() {
        Project project = VersioningPluginTest.createProject()
        GenerateVersionClassTask task = project.tasks.generateVersionClass
        task.packageName.set("com.trevorism.alert")
        task.className.set("Build")
        task.generateVersionClass()

        File generated = new File(task.outputDirectory.get().asFile, "com/trevorism/alert/Build.java")
        assert generated.exists()
        assert generated.text.contains("public final class Build")
    }
}
