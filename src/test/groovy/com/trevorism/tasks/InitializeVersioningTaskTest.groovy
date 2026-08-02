package com.trevorism.tasks

import com.trevorism.plugin.tasks.InitializeVersioningTask
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InitializeVersioningTaskTest {

    @TempDir
    File temporaryFolder

    @Test
    void testReadsVersionFromDeployWorkflow() {
        File workflow = new File(temporaryFolder, "deploy.yml")
        workflow.text = "jobs:\n  pipeline:\n    with:\n      version: '1-4-0'\n"

        assert "1.4.0" == InitializeVersioningTask.readVersionFromWorkflow(workflow)
    }

    @Test
    void testReadsUnquotedVersionFromDeployWorkflow() {
        File workflow = new File(temporaryFolder, "deploy.yml")
        workflow.text = "      version: 0-4-0\n"

        assert "0.4.0" == InitializeVersioningTask.readVersionFromWorkflow(workflow)
    }

    @Test
    void testDefaultsWhenWorkflowHasNoVersion() {
        File workflow = new File(temporaryFolder, "deploy.yml")
        workflow.text = "jobs:\n  pipeline:\n    with:\n      gcp_project: 'trevorism-action'\n"

        assert "0.0.1" == InitializeVersioningTask.readVersionFromWorkflow(workflow)
    }

    @Test
    void testDefaultsWhenWorkflowIsMissing() {
        assert "0.0.1" == InitializeVersioningTask.readVersionFromWorkflow(new File(temporaryFolder, "absent.yml"))
        assert "0.0.1" == InitializeVersioningTask.readVersionFromWorkflow(null)
    }

    @Test
    void testAppendKeepsExistingProperties() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2"

        InitializeVersioningTask.appendAppVersion(properties, "1.4.0")

        assert "micronautVersion=5.0.2\nappVersion=1.4.0\n" == properties.text
    }

    @Test
    void testAppendCreatesFileWhenAbsent() {
        File properties = new File(temporaryFolder, "gradle.properties")

        InitializeVersioningTask.appendAppVersion(properties, "1.4.0")

        assert "appVersion=1.4.0\n" == properties.text
    }

    @Test
    void testDetectsExistingAppVersion() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2\nappVersion = 1.4.0\n"

        assert InitializeVersioningTask.hasAppVersion(properties)
    }

    @Test
    void testMicronautVersionIsNotMistakenForAppVersion() {
        File properties = new File(temporaryFolder, "gradle.properties")
        properties.text = "micronautVersion=5.0.2\n"

        assert !InitializeVersioningTask.hasAppVersion(properties)
    }
}
