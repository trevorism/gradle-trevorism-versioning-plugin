package com.trevorism.tasks

import com.trevorism.plugin.VersioningPluginTest
import com.trevorism.plugin.tasks.BumpCurrentVersionTask
import org.gradle.api.Project
import org.junit.jupiter.api.Test

class BumpCurrentVersionTaskTest {

    @Test
    void testBumpPatchVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"

        String newVersion = BumpCurrentVersionTask.bumpVersion(project.version, "patch")
        assert "1.2.4" == newVersion
    }

    @Test
    void testBumpPatchNine() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.9"

        String newVersion = BumpCurrentVersionTask.bumpVersion(project.version, "patch")
        assert "1.2.10" == newVersion
    }

    @Test
    void testBumpMinorVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"

        String newVersion = BumpCurrentVersionTask.bumpVersion(project.version, "minor")
        assert "1.3.0" == newVersion
    }

    @Test
    void testBumpMajorVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"

        String newVersion = BumpCurrentVersionTask.bumpVersion(project.version, "major")
        assert "2.0.0" == newVersion
    }
}
