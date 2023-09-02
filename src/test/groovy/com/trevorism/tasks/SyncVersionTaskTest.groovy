package com.trevorism.tasks

import com.trevorism.plugin.VersioningPluginTest
import com.trevorism.plugin.tasks.SyncVersionTask
import org.gradle.api.Project
import org.junit.jupiter.api.Test

class SyncVersionTaskTest {

    @Test
    void testSyncDeployWithVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"
        SyncVersionTask syncVersionTask = project.tasks.syncVersion
        List<String> sampleLines = ["name: Deploy tested project to GCP", "  call-deploy:", "    with:", "      version: '0-0-1'", "permissions: write-all"]
        def result = syncVersionTask.replaceVersionLine(sampleLines, ":", "'", "", true)
        assert result.contains("      version: '1-2-3'")
    }

    @Test
    void testSyncDeployWithoutVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"
        SyncVersionTask syncVersionTask = project.tasks.syncVersion
        List<String> sampleLines = ["name: Deploy tested project to GCP", "  call-deploy:", "    with:", "permissions: write-all"]
        def result = syncVersionTask.replaceVersionLine(sampleLines, ":", "'", "", true)
        assert result == sampleLines
    }

    @Test
    void testSyncApplicationWithVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"
        SyncVersionTask syncVersionTask = project.tasks.syncVersion
        List<String> sampleLines = ["name: Deploy tested project to GCP", "  call-deploy:", "    with:", "      version = '0-0-1'", "permissions: write-all"]
        def result = syncVersionTask.replaceVersionLine(sampleLines, "=", "\"", "", false)
        assert result.contains("      version = \"1.2.3\"")
    }

    @Test
    void testSyncApplicationWithoutVersion() {
        Project project = VersioningPluginTest.createProject()
        project.version = "1.2.3"
        SyncVersionTask syncVersionTask = project.tasks.syncVersion
        List<String> sampleLines = ["name: Deploy tested project to GCP", "  call-deploy:", "    with:", "permissions: write-all"]
        def result = syncVersionTask.replaceVersionLine(sampleLines, "=", "\"", "", false)
        assert result == sampleLines
    }
}
