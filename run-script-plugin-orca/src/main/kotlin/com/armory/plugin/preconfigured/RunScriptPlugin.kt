package com.armory.plugin.preconfigured

import com.netflix.spinnaker.kork.plugins.api.PluginSdks
import com.netflix.spinnaker.orca.api.preconfigured.jobs.PreconfiguredJobConfigurationProvider
import com.netflix.spinnaker.orca.clouddriver.config.KubernetesPreconfiguredJobProperties
import org.pf4j.Extension
import org.pf4j.Plugin
import org.pf4j.PluginWrapper


class RunScriptPlugin(wrapper: PluginWrapper): Plugin(wrapper) {

    override fun start() {
        System.out.println("RunScriptPlugin.start()")
    }

    override fun stop() {
        System.out.println("RunScriptPlugin.stop()")
    }
}

@Extension
class RunScriptPreConfiguredStage(val pluginSdks: PluginSdks, val configuration: PluginConfig) : PreconfiguredJobConfigurationProvider {
    override fun getJobConfigurations(): List<KubernetesPreconfiguredJobProperties> {
        val jobProperties = pluginSdks.yamlResourceLoader().loadResource("com/armory/plugin/preconfigured/armory-run-script.yaml", KubernetesPreconfiguredJobProperties::class.java)
        ConfigOverrider.override(configuration, jobProperties)
        return arrayListOf(jobProperties)
    }

}
