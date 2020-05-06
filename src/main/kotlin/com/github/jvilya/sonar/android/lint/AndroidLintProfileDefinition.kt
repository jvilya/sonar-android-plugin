package com.github.jvilya.sonar.android.lint

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader

class AndroidLintProfileDefinition : BuiltInQualityProfilesDefinition {
    override fun define(context: BuiltInQualityProfilesDefinition.Context) {
        val profile = context.createBuiltInQualityProfile("Android Lint way", LANGUAGE_KEY)
        BuiltInQualityProfileJsonLoader.load(profile, REPOSITORY_KEY, AndroidLintRulesDefinition.PATH_TO_JSON)
        profile.done()
    }
}