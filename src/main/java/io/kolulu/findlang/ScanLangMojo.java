package io.kolulu.findlang;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scan project source files with language regex matcher.
 *
 * @author liutianlu
 * <br/>Created 2023/9/13 14:01
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ScanLangMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    MavenProject mavenProject;

    @Parameter(defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
    List<String> sourceRoots;

    @Override
    public void execute() throws MojoFailureException {
        if ("pom".equals(mavenProject.getPackaging())) {
            getLog().warn("Skip pom packaging artifact " + mavenProject.getId());
            return;
        }
        getLog().warn("Scanning files under path: " + sourceRoots.toString());
        File report = new File(mavenProject.getBasedir(), "report.csv");

        try (
                SourceRootReader rootReader = new SourceRootReader(sourceRoots, getLog());
                LanguageUsageCsvWriter writer = new LanguageUsageCsvWriter(report)
        ) {
            writer.getWriter().write(LanguageUsage.CSV_HEADER + "\n");
            RegexLanguageMatcher matcher = new RegexLanguageMatcher(Languages.CHINESE);
            for (SourceFileReader reader : rootReader.getReaders()) {
                getLog().info("Write " + reader.getPath() + " to report");
                writer.write(matcher.process(reader).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new MojoFailureException(e);
        }
    }
}
