package com.dbsystel.maven.plugins.tibco.source;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.dbsystel.maven.plugins.tibco.source.alias.RepositoryModel;

/**
 * <p>
 * This goal modifies the ".aliaslib" files of the TIBCO BusinessWorks project sources to include transitive JARs
 * dependencies.
 * </p>
 * <p>
 * This step can be ignored by setting <i>includeTransitiveJARsInEAR</i> to <i>false</i>.
 * </p>
 * 
 */
@Mojo(name = "update-alias-lib", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true,
        executionStrategy = "once-per-session")
public class TibcoBwUpdateAliasesLibsMojo extends AbstractMojo {
    public final static String JAR_TYPE = "jar";

    private List<Dependency> jarDependencies;

    private List<File> aliaslibFiles; // list of ".aliaslib" files to modify

    /**
     * The Maven session.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The source enconding.
     */
    @Parameter(property = "project.build.sourceEncoding", required = true, readonly = true)
    protected String sourceEncoding;

    /**
     * Directory containing the processes and resource files that should be packed into the project library
     * 
     * @parameter default-value=${project.build.outputDirectory}
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File tibcoBuildDirectory;

    /**
     * A flag used to disable the source procedure. This is primarily intended for usage from the command line to
     * occasionally adjust the build.
     *
     */
    @Parameter(property = "tibco.source.skip", defaultValue = "false")
    private boolean skipSource;

    public boolean isSkipSource() {
        return skipSource;
    }

    /**
     * Whether to keep original entries in '.aliaslib' files when updating
     */
    @Parameter(required = false, defaultValue = "true")
    public Boolean keepOriginalAliasLib;

    /**
     * This method retrieves a list of ".aliaslib" files to process in the source directory (usually "target/src") which
     * is a copy of the actual source directory used to compile the TIBCO BusinessWorks EAR.
     * 
     * @throws IOException
     */
    private List<File> initFiles() throws IOException {

        if (tibcoBuildDirectory == null) {
            tibcoBuildDirectory = new File(".");
        }
        this.getLog().info("Searching for aliasLibs in " + tibcoBuildDirectory.getAbsolutePath());

        List<File> res = FileUtils.getFiles(tibcoBuildDirectory, "**/*.aliaslib", "");

        for (Iterator<File> it = res.iterator(); it.hasNext();) {
            File f = it.next();
            this.getLog().debug("Adding " + f.getAbsolutePath() + " for Update... ");
        }

        if (res.size() == 0) {
            this.getLog().debug("No '.aliaslib' files in..." + this.project.getArtifactId());
        }

        return res;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<HashMap<String, Object>> readXMLBean(RepositoryModel repositoryModel, File f)
            throws JAXBException, UnsupportedEncodingException {
        // retrieve the content of the XML Bean in the ".aliaslib" file

        this.getLog().info("RepositoryModel :" + repositoryModel.getRepository().getName().getName());

        String xmlBean = repositoryModel.getRepository().getName().getFILEALIASESLIST();

        // this XML bean is decoded to a Java object with the XMLDecoder
        XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xmlBean.getBytes(this.sourceEncoding)));
        ArrayList<HashMap<String, Object>> result = null;
        try {
            result = (ArrayList<HashMap<String, Object>>) d.readObject();
        } finally {
            d.close();
        }

        return result;
    }

    /**
     * 
     * @param repositoryModel
     * @param f
     * @param aliases
     * @throws JAXBException
     *             when error occured during parsing
     * @throws IOException
     *             when aliasfile not found
     */
    private void writeXMLBean(RepositoryModel repositoryModel, File f, ArrayList<HashMap<String, Object>> aliases)
            throws JAXBException, IOException {
        this.getLog().debug("DEBUG --> " + this.getClass().getName() + " update AliasLib... " + f.getAbsolutePath());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        XMLEncoder e = new XMLEncoder(os);
        e.writeObject(aliases);
        e.close();

        String xmlBean = os.toString(this.sourceEncoding);
        os.close();

        // put back the XML Bean in the ".aliaslib" file
        repositoryModel.getRepository().getName().setFILEALIASESLIST(xmlBean);
        repositoryModel.save();
        this.getLog().debug("AliasLib updated : " + repositoryModel.getRepository().getName().getFILEALIASESLIST());
    }

    /**
     * This method add an alias in the object used internally by TIBCO BusinessWorks.
     * 
     * @param list
     *            , an object used internally by TIBCO BusinessWorks.
     * @param aliasName
     *            , the name of the alias as normalized b {@link AbstractBWMojo}.
     */
    private void addAlias(ArrayList<HashMap<String, Object>> list, String aliasName) {
        this.getLog().debug(
                "DEBUG --> " + this.getClass().getName() + " adding alias " + aliasName + " for update aliasLib");
        for (HashMap<String, Object> h : list) {
            String name = (String) h.get("name");
            if (name != null && name.equals(aliasName)) {
                return; // avoid duplicates
            }
        }
        HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("isClasspathFile", Boolean.TRUE);
        h.put("name", aliasName);
        h.put("includeInDeployment", Boolean.TRUE);

        list.add(h);
    }

    /**
     * This method adds the JAR aliases to a ".aliaslib" file
     * 
     * @param f
     *            , the ".aliaslib" file to update
     * @throws MojoExecutionException
     *             when error occured during execution
     */
    public void processFile(File f) throws MojoExecutionException {
        try {
            this.getLog().debug(
                    "DEBUG --> " + this.getClass().getName() + "Preparing for update ... " + f.getAbsolutePath());
            RepositoryModel repositoryModel = new RepositoryModel(f);

            ArrayList<HashMap<String, Object>> aliases = readXMLBean(repositoryModel, f);

            this.getLog().info("Preparing ... " + aliases.toString() + " for update");

            // reset old references
            if (!keepOriginalAliasLib) {
                this.getLog().info("Reset old References in " + f.getAbsolutePath());
                //info( f.getAbsolutePath());
                aliases.clear();
            }

            this.getLog().info("Number of Dependencies : " + jarDependencies.size());

            // adding the JAR dependencies
            for (Dependency dependency : jarDependencies) {
                this.getLog().info(
                        "Adding or Replace Aliases " + aliases + " with " + dependency.getArtifactId() + " to "
                                + f.getAbsolutePath());
                addAlias(aliases, getJarAlias(dependency, false));
            }

            writeXMLBean(repositoryModel, f, aliases);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param jarDependency
     *            , a JAR dependency from Maven point-of-view
     * @param replaceDot
     *            , allows to replace dots in the version of the artifact by underscores. This is because Maven will do
     *            so on the generated artifact.
     * 
     * @return groupId:artifactId:version:jar of the JAR dependency
     */
    protected String getJarAlias(Dependency jarDependency, boolean replaceDot) {
        assert (jarDependency != null);
        String version = jarDependency.getVersion();
        //if (replaceDot) {
        //    version = version.replace('.', '_');
        //}

        //return jarDependency.getGroupId() + ":" + jarDependency.getArtifactId() + ":" + version + ":" + JAR_TYPE;
        return jarDependency.getArtifactId() + "." + JAR_TYPE;
    }

    /**
     * @return the list of Jar Dependencies from the POM project
     * @throws IOException
     */
    protected List<Dependency> getJarDependencies() throws IOException {
        this.getLog().info("Getting defined JAR Dependencies...");
        List<Dependency> dependencies = new ArrayList<Dependency>();

        for (Dependency dependency : project.getDependencies()) {
            this.getLog().info("Found Dependency:" + dependency.getArtifactId() + "." + dependency.getType());

            if (dependency.getType() == "jar")
                dependencies.add(dependency);

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
    private String getAlias(Dependency dependency) {
        StringBuffer buf = new StringBuffer();
        //buf.append(artifact.getGroupId().replace(".", "/"));
        //buf.append("/");
        buf.append(dependency.getArtifactId());
        buf.append("-");
        buf.append(dependency.getClassifier());
        buf.append(".");
        buf.append(dependency.getType());
        return buf.toString();
    }

    /**
     * 
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkipSource()) {
            this.getLog().info("Skipping update-alias-lib per configuration.");
            return;
        }
        try {
            aliaslibFiles = initFiles();
            jarDependencies = getJarDependencies();

            for (File f : aliaslibFiles) {
                processFile(f);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error occured:", e);
        }

    }
}
