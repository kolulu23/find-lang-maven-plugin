package io.kolulu.findlang;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
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

    @Parameter
    String pattern;

    @Parameter(defaultValue = "true")
    Boolean skipComments;

    @Parameter(defaultValue = "report.csv")
    String reportFileName;

    @Override
    public void execute() throws MojoFailureException {
        if ("pom".equals(mavenProject.getPackaging())) {
            getLog().warn("Skip pom packaging artifact " + mavenProject.getId());
            return;
        }
        getLog().warn("Scanning files under path: " + sourceRoots.toString());
        try (
                SourceRootReader rootReader = createSourceReader();
                LanguageUsageCsvWriter writer = createUsageWriter()
        ) {
            LanguageUsageRegexFinder finder = createUsageFinder();
            for (SourceFileReader reader : rootReader.getReaders()) {
                List<LanguageUsage> usages = finder.process(reader).collect(Collectors.toList());
                if (!usages.isEmpty()) {
                    getLog().info("Found " + usages.size() + " usages in " + reader.getPath().getFileName());
                }
                writer.write(usages);
                reader.close();
            }
        } catch (IOException e) {
            throw new MojoFailureException(e);
        }
    }

    private SourceRootReader createSourceReader() {
        return new SourceRootReader(sourceRoots, getLog());
    }

    private LanguageUsageCsvWriter createUsageWriter() throws IOException {
        File report = new File(mavenProject.getBasedir(), reportFileName);
        return new LanguageUsageCsvWriter(report);
    }

    private LanguageUsageRegexFinder createUsageFinder() {
        LanguageUsageRegexFinder finder;
        if (pattern == null || pattern.isEmpty()) {
            // The default is to find any kind of chinese except comments
            finder = new LanguageUsageRegexFinder(Languages.CHINESE_LITERALS_ONLY)
                    .setSkipComments(true);
        } else {
            finder = new LanguageUsageRegexFinder(pattern)
                    .setSkipComments(skipComments);
        }
        return finder;
    }
}
