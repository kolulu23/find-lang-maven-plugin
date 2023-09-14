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

    /**
     * The root project reference, does not need to be set by user
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject mavenProject;

    /**
     * Since we are using process-sources stage, for simplicity reasons we only scan ${project.compileSourceRoots}
     */
    @Parameter(defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
    List<String> sourceRoots;

    /**
     * Available configs: <br/>
     * <ul>
     *     <li>searchPattern: The main search usePattern, default to a built-in language setting</li>
     *     <li>filterPatterns: Patterns used to further filter out language usages in source code</li>
     *     <li>skipComments: Skip comments in source code, default to true</li>
     * </ul>
     */
    @Parameter
    private LanguagePatternConfig languagePatternConfig;

    /**
     * The directory report file should be located in, must be a directory, not a file
     */
    @Parameter(defaultValue = "${project.build.directory}")
    String reportDirectory;

    /**
     * Report file name, it can be located under {@link #reportDirectory}, simple names is preferred
     */
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
        File reportDir = new File(reportDirectory);
        if (!reportDir.exists()) {
            getLog().warn("Report directory not exist, it will be created soon");
            if (!reportDir.mkdirs()) {
                getLog().error("Can not create report directory, please turn on stack trace to debug");
            }
        }
        File report = new File(reportDirectory, reportFileName);
        getLog().warn("Report file will be generated at " + report.getAbsolutePath());
        return new LanguageUsageCsvWriter(report);
    }

    private LanguageUsageRegexFinder createUsageFinder() {
        LanguageUsageRegexFinder finder = LanguageUsageFinderFactory.regexFinder(languagePatternConfig);
        getLog().info(finder.debug());
        return finder;
    }
}
