package com.armory.plugin.preconfigured

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.netflix.spinnaker.orca.clouddriver.config.KubernetesPreconfiguredJobProperties
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import io.kubernetes.client.models.V1EnvVar
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigOverriderTest : JUnit5Minutests {
  fun tests() = rootContext {
    test("should assign overridden properties correctly") {
      val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      val job = mapper.readValue(File("src/main/resources/com/armory/plugin/preconfigured/armory-run-script.yaml"), KubernetesPreconfiguredJobProperties::class.java)
      val configuration = PluginConfig(
        account = "test",
        credentials = "test",
        artifactServiceUrl = "http://my-test",
        initContainerImage = "armory",
        gitArtifactAccount = "armory"
      )

      ConfigOverrider.override(configuration, job)

      assertEquals(job.account, configuration.account)
      assertEquals(job.credentials, configuration.credentials)

      val initContainer = job.manifest.spec.template.spec.initContainers.firstOrNull{ it.name == "git" }
      assertNotNull(initContainer)
      assertEquals(initContainer.image, configuration.initContainerImage)

      val serviceUrlVar = getVariable(initContainer.env, "ARTIFACT_SERVICE")
      val accountVar = getVariable(initContainer.env, "ARTIFACT_ACCOUNT")

      assertNotNull(serviceUrlVar)
      assertNotNull(accountVar)
      assertEquals(serviceUrlVar.value, configuration.artifactServiceUrl)
      assertEquals(accountVar.value, configuration.gitArtifactAccount)
    }
  }

  private fun getVariable(vars: List<V1EnvVar>, target : String) : V1EnvVar? {
    return vars.firstOrNull { it.name == target }
  }
}