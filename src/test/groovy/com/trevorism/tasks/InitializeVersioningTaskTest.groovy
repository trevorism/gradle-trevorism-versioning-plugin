package com.trevorism.tasks

import com.trevorism.plugin.VersioningPluginTest
import com.trevorism.plugin.tasks.InitializeVersioningTask
import org.gradle.api.Project
import org.junit.jupiter.api.Test

class InitializeVersioningTaskTest {

    @Test
    void testInitializeBlankProject(){
        Project project = VersioningPluginTest.createProject()
        InitializeVersioningTask ivt = project.tasks.initializeVersion
        ivt.initVersionSystem()

        def text = project.file("gradle.properties").text

        assert text.contains("version=0.0.1")
        assert text.contains("nextVersionStrategy=patch")

        assert project."version" == "0.0.1"
        assert project."nextVersionStrategy" == "patch"
    }

}
