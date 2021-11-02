package com.armory.plugin.preconfigured

import com.netflix.spinnaker.orca.clouddriver.config.KubernetesPreconfiguredJobProperties
import java.lang.IllegalStateException

class ConfigOverrider() {
  companion object {
    /**
     * swap manifest properties for configuration values
     */
    fun override(configuration: PluginConfig, jobProperties : KubernetesPreconfiguredJobProperties) {
      if (!configuration.account.isNullOrEmpty()) {
        jobProperties.account = configuration.account
      }
      if (!configuration.credentials.isNullOrEmpty()) {
        jobProperties.credentials = configuration.credentials
      }

      val initContainer = jobProperties.manifest.spec?.template?.spec?.initContainers?.firstOrNull{ it.name == "git" }

      if (initContainer == null) {
        throw IllegalStateException("job manifest must have initContainer with name git")
      }

      if (!configuration.initContainerImage.isNullOrEmpty()) {
        initContainer.image = configuration.initContainerImage
      }

      // TODO: there's probably a way more efficient way to do this.
      if (!configuration.artifactServiceUrl.isNullOrEmpty()) {
        val targetEnvVariable = initContainer.env?.firstOrNull{ it.name == "ARTIFACT_SERVICE" }
        if (targetEnvVariable != null) {
          targetEnvVariable.value = configuration.artifactServiceUrl
        }
      }

      if (!configuration.gitArtifactAccount.isNullOrEmpty()) {
        val targetEnvVariable = initContainer.env?.firstOrNull{ it.name == "ARTIFACT_ACCOUNT" }
        if (targetEnvVariable != null) {
          targetEnvVariable.value = configuration.gitArtifactAccount
        }
      }
    }
  }
}