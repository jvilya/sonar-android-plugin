package com.github.jvilya.sonar.android.lint

import org.sonar.api.resources.AbstractLanguage

class AndroidLintLanguage : AbstractLanguage(LANGUAGE_KEY, LANGUAGE_NAME) {
    override fun getFileSuffixes(): Array<String> {
        // Other plugins are responsible for ".xml", ".java", ".kt", ".kts"
        // So this plugin is not going to use it
        return arrayOf(".properties", ".gradle", ".cfg", ".txt")
    }
}