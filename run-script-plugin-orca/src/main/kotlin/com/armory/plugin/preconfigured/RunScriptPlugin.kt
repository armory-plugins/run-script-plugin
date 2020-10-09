package com.armory.plugin.preconfigured

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
class RunScriptPreConfiguredStage(val configuration: PluginConfig) : PreconfiguredJobConfigurationProvider {
    private val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    override fun getJobConfigurations(): List<KubernetesPreconfiguredJobProperties> {
        val jobProperties = loadResource("com/armory/plugin/preconfigured/armory-run-script.yaml", KubernetesPreconfiguredJobProperties::class.java)
        ConfigOverrider.override(configuration, jobProperties)
        return arrayListOf(jobProperties)
    }

    fun <T : Any> loadResource(resourceName: String, toValueType: Class<T>): T {
        this.extensionClass.classLoader.getResourceAsStream(resourceName).use { inputStream ->
            if (inputStream != null) {
                return mapper.readValue(inputStream, toValueType)
            }
            throw IllegalArgumentException("Cannot load specified resource: $resourceName , for extension: ${extensionClass.simpleName}")
        }
    }

}
