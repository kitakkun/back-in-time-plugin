import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import util.libs

with(pluginManager) {
    apply("org.jlleitschuh.gradle.ktlint")
}

configure<KtlintExtension> {
    version.set(libs.findVersion("ktlint").get().requiredVersion)
    ignoreFailures.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
}
