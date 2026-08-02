package com.trevorism.tasks

import com.trevorism.plugin.VersioningPluginTest
import com.trevorism.plugin.tasks.BumpCurrentVersionTask
import org.gradle.api.Project
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class BumpCurrentVersionTaskTest {

    @TempDir
    File temporaryFolder

    @Test
    void testBumpPatchVersion() {
        assert "1.2.4" == BumpCurrentVersionTask.bumpVersion("1.2.3", "patch")
    }

    @Test
    void testBumpPatchNine() {
        assert "1.2.10" == BumpCurrentVersionTask.bumpVersion("1.2.9", "patch")
    }

    @Test
    void testBumpMinorVersion() {
        assert "1.3.0" == BumpCurrentVersionTask.bumpVersion("1.2.3", "minor")
    }

    @Test
    void testBumpMajorVersion() {
        assert "2.0.0" == BumpCurrentVersionTask.bumpVersion("1.2.3", "major")
    }

    @Test
    void testRewritesOnlyTheAppVersionLine() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2\nappVersion=1.2.3\n"

        bump(properties)

        assert "micronautVersion=5.0.2\nappVersion=1.2.4\n" == properties.text
    }

    @Test
    void testPreservesCarriageReturnsAndSpacing() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2\r\nappVersion = 1.2.3\r\n"

        bump(properties)

        assert "micronautVersion=5.0.2\r\nappVersion = 1.2.4\r\n" == properties.text
    }

    @Test
    void testLeavesFileAloneWhenAppVersionIsAbsent() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2\n"

        bump(properties)

        assert "micronautVersion=5.0.2\n" == properties.text
    }

    private static void bump(File properties) {
        Project project = VersioningPluginTest.createProject()
        BumpCurrentVersionTask task = project.tasks.bumpVersion
        task.gradlePropertiesFile.set(properties)
        task.bumpCurrentVersion()
    }
}
