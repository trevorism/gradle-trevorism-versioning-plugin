package com.trevorism.plugin

import com.trevorism.plugin.ext.VersioningSettings
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
        assert project.tasks.named("generateVersionClass")
    }

    @Test
    void testProjectVersionComesFromAppVersion() {
        Project project = createProject()
        assert "1.2.3" == project.version
    }

    @Test
    void testArchiveNameOmitsVersion() {
        Project project = createProject()
        assert "" == project.tasks.jar.archiveVersion.getOrElse("")
        assert "foo.jar" == project.tasks.jar.archiveFileName.get()
    }

    @Test
    void testGeneratedSourceDirectoryIsRegistered() {
        Project project = createProject()
        boolean registered = project.sourceSets.main.java.srcDirs.any {
            it.path.replace("\\", "/").endsWith("generated/sources/version")
        }
        assert registered
    }

    @Test
    void testMaintenanceTaskFilePropertiesResolve() {
        Project project = createProject()

        assert "gradle.properties" == project.tasks.initializeVersion.gradlePropertiesFile.get().asFile.name
        assert project.tasks.initializeVersion.deployWorkflowFile.get().asFile.path
                .replace("\\", "/").endsWith(".github/workflows/deploy.yml")
        assert "gradle.properties" == project.tasks.bumpVersion.gradlePropertiesFile.get().asFile.name
        assert "1.2.3" == project.tasks.bumpVersion.currentVersion.get()
    }

    @Test
    void testGenerateTaskPropertiesResolve() {
        Project project = createProject()

        assert "1.2.3" == project.tasks.generateVersionClass.semanticVersion.get()
        assert "com.trevorism" == project.tasks.generateVersionClass.packageName.get()
        assert "AppVersion" == project.tasks.generateVersionClass.className.get()
        assert project.tasks.generateVersionClass.outputDirectory.get().asFile.path
                .replace("\\", "/").endsWith("generated/sources/version")
    }

    @Test
    void testMaintenanceTasksExistWithoutAnyVersion() {
        Project project = ProjectBuilder.builder().withName("bar").build()
        project.apply plugin: 'com.trevorism.gradle.versioning'

        assert project.tasks.named("initializeVersion")
        assert project.tasks.named("bumpVersion")
        assert !project.tasks.findByName("generateVersionClass")
    }

    @Test
    void testResolveSemanticVersionPrefersAppVersion() {
        Project project = ProjectBuilder.builder().withName("baz").build()
        project.ext.setProperty(VersioningSettings.APP_VERSION_KEY, "2.0.0")
        project.version = "9.9.9"

        assert "2.0.0" == VersioningPlugin.resolveSemanticVersion(project)
    }

    @Test
    void testResolveSemanticVersionFallsBackToProjectVersion() {
        Project project = ProjectBuilder.builder().withName("baz").build()
        project.version = "3.1.0"

        assert "3.1.0" == VersioningPlugin.resolveSemanticVersion(project)
    }

    @Test
    void testResolveSemanticVersionReturnsNullWhenUnspecified() {
        Project project = ProjectBuilder.builder().withName("baz").build()

        assert null == VersioningPlugin.resolveSemanticVersion(project)
    }

    static Project createProject() {
        Project project = ProjectBuilder.builder().withName("foo").build()
        project.with {
            it.ext.setProperty(VersioningSettings.APP_VERSION_KEY, "1.2.3")
            apply plugin: 'com.trevorism.gradle.versioning'
        }
        return project
    }
}
