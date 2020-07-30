package com.armory.plugin.preconfigured

import com.netflix.spinnaker.kork.plugins.api.ExtensionConfiguration

@ExtensionConfiguration("armory.RunScriptPreConfiguredJobStage")
data class PluginConfig(var account: String?, var credentials: String?, var namespace: String?)