package com.github.jvilya.sonar.android.lint.sensor

import com.github.jvilya.sonar.android.lint.REPORT_PROPERTY_KEY
import com.github.jvilya.sonar.android.lint.REPOSITORY_KEY
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.rule.RuleKey
import org.sonar.api.utils.log.Loggers
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList
import java.util.function.Consumer
import javax.xml.stream.XMLStreamException

class AndroidLintSensor : Sensor {

    companion object {
        private val LOG = Loggers.get(AndroidLintSensor::class.java)
    }

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.name("Android Lint Sensor")
    }

    override fun execute(context: SensorContext) {
        val reportFiles = getReportFiles(context, REPORT_PROPERTY_KEY)
        reportFiles.forEach(Consumer { report: File ->
            importReport(report, context)
        })
    }

    private fun importReport(reportPath: File, context: SensorContext) {
        try {
            FileInputStream(reportPath).use { `in` ->
                LOG.info("Importing {}", reportPath)
                AndroidLintXmlReportReader.read(`in`) { id: String, file: String, line: String, message: String ->
                    saveIssue(
                        context, id, file, line, message)
                }
            }
        } catch (e: IOException) {
            LOG.error("No issues information will be saved as the report file '{}' can't be read.", reportPath, e)
        } catch (e: XMLStreamException) {
            LOG.error("No issues information will be saved as the report file '{}' can't be read.", reportPath, e)
        } catch (e: RuntimeException) {
            LOG.error("No issues information will be saved as the report file '{}' can't be read.", reportPath, e)
        }
    }

    private fun saveIssue(context: SensorContext, id: String, file: String, line: String, message: String) {
        if (id.isEmpty() || message.isEmpty() || file.isEmpty()) {
            LOG.debug("Missing information or unsupported file type for id:'{}', file:'{}', message:'{}'", id, file,
                message)
            return
        }
        val predicates = context.fileSystem().predicates()
        val inputFile = context.fileSystem().inputFile(predicates.or(
            predicates.hasAbsolutePath(file),
            predicates.hasRelativePath(file)))
        if (inputFile == null) {
            LOG.warn("No input file found for {}. No android lint issues will be imported on this file.", file)
            return
        }

        val ruleKey = RuleKey.of(REPOSITORY_KEY, id)

        val newIssue = context.newIssue()
            .forRule(ruleKey)

        val primaryLocation = newIssue.newLocation()
            .on(inputFile)
            .message(message)
        val lineNumber = line.toIntOrNull() ?: 0
        if (lineNumber > 0) {
            primaryLocation.at(inputFile.selectLine(lineNumber))
        }
        newIssue.at(primaryLocation)

        newIssue.save()
    }

    private fun getReportFiles(context: SensorContext, externalReportsProperty: String?): List<File> {
        val reportPaths = context.config().getStringArray(externalReportsProperty)
        if (reportPaths.isEmpty()) {
            return emptyList()
        }
        val result: MutableList<File> = ArrayList()
        for (reportPath in reportPaths) {
            val report = getIOFile(
                context.fileSystem().baseDir(), reportPath)
            result.add(report)
        }
        return result
    }

    /**
     * Returns a java.io.File for the given path.
     * If path is not absolute, returns a File with module base directory as parent path.
     */
    private fun getIOFile(baseDir: File, path: String): File {
        var file = File(path)
        if (!file.isAbsolute) {
            file = File(baseDir, path)
        }
        return file
    }
}