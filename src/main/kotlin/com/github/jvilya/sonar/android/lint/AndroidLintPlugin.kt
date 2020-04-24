package com.github.jvilya.sonar.android.lint

import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition
import org.sonar.api.resources.Qualifiers

class AndroidLintPlugin : Plugin {

    override fun define(context: Plugin.Context) {
        context.addExtensions(
                listOf(
                        AndroidLintProfileDefinition::class.java,
                        AndroidLintRulesDefinition::class.java,
                        PropertyDefinition.builder("sonar.androidLint.reportPaths")
                        .name("Android Lint Report Files")
                        .description("Paths (absolute or relative) to xml files with Android Lint issues.")
                        .onQualifiers(listOf(Qualifiers.PROJECT))
                        .multiValues(true)
                        .build()
                )
        )
    }
}