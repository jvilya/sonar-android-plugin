package com.github.jvilya.sonar.android.lint

import org.sonar.api.server.rule.RulesDefinition
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader
import org.sonarsource.analyzer.commons.RuleMetadataLoader

class AndroidLintRulesDefinition : RulesDefinition {
    companion object {
        const val RESOURCE_FOLDER = "org/sonar/l10n/androidlint/rules"
        const val PATH_TO_JSON = "org/sonar/l10n/androidlint/rules/Android_lint_profile.json"
    }

    override fun define(context: RulesDefinition.Context) {
        context.createRepository(REPOSITORY_KEY, LANGUAGE_KEY)
            .setName("Android Lint Analyzer")
            .apply {
                val ruleMetadataLoader = RuleMetadataLoader(RESOURCE_FOLDER, PATH_TO_JSON)
                ruleMetadataLoader.addRulesByRuleKey(
                    this,
                    BuiltInQualityProfileJsonLoader.loadActiveKeysFromJsonProfile(PATH_TO_JSON).toList())
            }
            .done()
    }
}