package com.dbsystel.maven.plugins.tibco.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

public abstract class AbstractTibcoBwSourceMojo extends AbstractMojo {
    public final static String SKIPPING = "Skipping...";

    public final static String JAR_TYPE = "jar";

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/*" };

    private static final String[] DEFAULT_EXCLUDES = new String[] {};

    @Parameter(property = "localRepository", defaultValue = "${settings.localRepository}")
    MavenArtifactRepository localRepo;

    /**
     * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the library.
     *
     */
    @Parameter
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the library.
     */
    @Parameter
    private String[] excludes;

    /**
     * A flag used to disable the source procedure. This is primarily intended for usage from the command line to
     * occasionally adjust the build.
     *
     * @since 2.2
     */
    @Parameter(property = "skip", defaultValue = "false")
    public boolean skip;

    /**
     * Directory containing the processes and resource files that should be packaged into the project library.
     *
     * 
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = false, readonly = true)
    protected File tibcoBuildDirectory;

    public boolean isSkipSource() {
        return skip;
    }

    /**
     * Location of the vcrepo.dat
     */
    @Parameter(property = "projectLocation", defaultValue = "${project.basedir}", required = false, readonly = true)
    private String projectLocation;

    protected String getProjectLocation() {
        return projectLocation;
    }

    protected void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    /**
     * Provides a reference to the settings file.
     * 
     * @required
     * @readOnly
     */
    @Parameter(property = "settings", readonly = true, required = true, defaultValue = "${settings}")
    private Settings settings;

    /**
     * The maven project
     * 
     *
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Directory containing the generated file aliases. The output directory into which to copy the resources.
     *
     *
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}", readonly = true,
            required = true)
    protected File outputDirectory;

    /**
     * The Maven session.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * @return The current project.
     */
    protected MavenProject getProject() {
        return project;
    }

    /**
     * @param project
     *            {@link MavenProject}
     */
    protected void setProject(MavenProject project) {
        this.project = project;
    }

    /**
     * Contains the full list of projects in the reactor.
     */
    @Parameter(defaultValue = "${reactorProjects}", readonly = true)
    protected List<MavenProject> reactorProjects;

    /**
     * The filename to be used for the generated archive file. For the source:jar goal, "-sources" is appended to this
     * filename. For the source:test-jar goal, "-test-sources" is appended.
     */
    @Parameter(defaultValue = "${project.build.finalName}")
    protected String finalName;

    /**
     * Specifies whether or not to attach the artifact to the project
     */
    @Parameter(property = "tibco.source.attach", defaultValue = "true")
    private boolean attach;

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * @return the wanted classifier, ie <code>sources</code> or <code>test-sources</code>
     */
    protected abstract String getClassifier();

    /**
     * @param p
     *            {@link MavenProject} not null
     * @return the compile or test sources
     * @throws MojoExecutionException
     *             in case of an error.
     */
    protected abstract List<String> getSources(MavenProject p) throws MojoExecutionException;

    /**
     * @param p
     *            {@link MavenProject} not null
     * @return the compile or test resources
     * @throws MojoExecutionException
     *             in case of an error.
     */
    protected abstract List<Resource> getResources(MavenProject p) throws MojoExecutionException;

    protected String getAliasPath(Artifact artifact) {
        String ret = null;
        DefaultArtifactHandler dah = new DefaultArtifactHandler();
        Artifact result = localRepo.find(new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(),
                artifact.getVersion(), artifact.getScope(), artifact.getType(), artifact.getClassifier(), dah));
        ret = result.getFile().getAbsolutePath();
        return ret;
    }

    /**
     * @return the list of Jar Dependencies from the POM project
     * @throws IOException
     */
    protected List<Dependency> getJarDependencies() throws IOException {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        for (Artifact artifact : project.getArtifacts()) {
            this.getLog().info(
                    "Found Dependency:" + artifact.getGroupId() + artifact.getArtifactId() + artifact.getClassifier()
                            + artifact.getType());

            String alias = getAlias(artifact);

        }
        return dependencies;
    }

    /**
     * Create the alias for an maven artifact
     * 
     * @param artifact
     * 
     * @return a <code>String</code>
     */
    private String getAlias(Artifact artifact) {
        StringBuffer buf = new StringBuffer();
        //buf.append(artifact.getGroupId().replace(".", "/"));
        //buf.append("/");
        buf.append(artifact.getArtifactId());
        buf.append("-");
        buf.append(artifact.getBaseVersion());
        buf.append(".");
        buf.append(artifact.getType());
        return buf.toString();
    }

    /** {@inheritDoc} */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /** {@inheritDoc} */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    private File getTibcoBuildDirectory() {
        return tibcoBuildDirectory;
    }

    private void setTibcoBuildDirectory(File tibcoBuildDirectory) {
        this.tibcoBuildDirectory = tibcoBuildDirectory;
    }

}
