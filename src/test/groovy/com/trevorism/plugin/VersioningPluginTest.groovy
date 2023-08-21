package com.trevorism.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test


/**
 * @author tbrooks
 */
class VersioningPluginTest {

    @Test
    void testTasksAreAdded() {
        Project project = createProject()
        assert project.plugins.findPlugin("com.trevorism.gradle.versioning")
        assert project.tasks.named("initializeVersion")
        assert project.tasks.named("bumpVersion")
        assert project.tasks.named("syncVersion")

    }

    static Project createProject() {
        Project project = ProjectBuilder.builder().withName("foo").build()
        project.with {
            it.tasks.create("appengineStage")
            apply plugin: 'com.trevorism.gradle.versioning'
        }
        return project
    }
}
