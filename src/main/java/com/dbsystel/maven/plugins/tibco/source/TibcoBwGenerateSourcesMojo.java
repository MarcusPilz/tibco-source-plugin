package com.dbsystel.maven.plugins.tibco.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwSourceMojo;

@Mojo(name = "generate-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true,
        executionStrategy = "once-per-session", requiresDependencyResolution = ResolutionScope.TEST)
public class TibcoBwGenerateSourcesMojo extends AbstractTibcoBwSourceMojo {
    /**
     * Location of the Designer5.prefs (usually in ~/.TIBCO)
     * 
     * @parameter default-value="${user.home}/.TIBCO/Designer5.prefs"
     */
    @Parameter(defaultValue = "${user.home}/.TIBCO/Designer5.prefs", required = true, readonly = true)
    private File designerPrefsFile;

    /**
     * Location of the fileAliases
     * 
     * @parameter default-value="${project.build.directory}/fileAliases.properties"
     */
    @Parameter(defaultValue = "${project.build.directory}/fileAliases.properties")
    private File aliasesFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().debug("DEBUG --> " + this.getClass().getName() + ":: execute::generate-sources");
        if (skip) {
            getLog().info(SKIPPING);
            return;
        }
        if (!outputDirectory.exists()) {
            FileUtils.mkdir(outputDirectory.getAbsolutePath());
        }

        generateFileAliases();
        if (this.getProject().getDependencies().size() > 0) {
            generateDotDesignTimeLibs();
            updateDesignerPrefs();
        }
    }

    /**
     * Generate a temporary fileAliases.properties file which can be used from within tibco to import the file aliases
     * 
     * @throws MojoExecutionException
     */
    private void generateFileAliases() throws MojoExecutionException {
        getLog().debug(
                "DEBUG --> " + this.getClass().getName() + "::generateFileAlias" + aliasesFile + " in : "
                        + getProjectLocation());

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(aliasesFile));

            for (Artifact artifact : project.getArtifacts()) {
                String alias = getAlias(artifact);
                StringBuffer sbAlias = new StringBuffer();

                sbAlias.append("tibco.alias." + alias + "=" + artifact.getFile().getAbsolutePath());

                //getLog().debug(artifact.getArtifactId());
                //sbAlias.append(artifact.getArtifactId());
                //sbAlias.append("-");

                // String version = artifact.getVersion();

                // if (version.contains("-")) {
                //   getLog().debug(version.substring(0, version.indexOf("-")));
                //   sbAlias.append(version.substring(0, version.indexOf("-")));
                //    sbAlias.append("-SNAPSHOT");
                //} else {
                //    sbAlias.append(version);
                //}
                //sbAlias.append("." + artifact.getType());
                String alias2 = sbAlias.toString();
                alias2 = alias2.replace("\\", "\\\\");
                this.getLog().debug(
                        "DEBUG --> " + this.getClass().getName() + " write ::" + alias2 + " to AliasFile!");
                writer.write(alias2 + "\n");

            }
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't create aliases file!");
        }
    }

    /**
     * Generate the .designtimelibs file with the dependencies of the pom file (and all the transitive dependencies)
     * 
     * @throws MojoExecutionException
     */
    private void generateDotDesignTimeLibs() throws MojoExecutionException {
        File dotDesignTimeLibsFile = null;
        this.getLog().debug("DEBUG:: Parameter generateDotDesignTimeLibs : " + getProjectLocation());
        if (getProjectLocation().equals(".")
                || getProjectLocation().equals(this.project.getBasedir().getAbsolutePath())) {
            dotDesignTimeLibsFile = new File(project.getBasedir().getAbsolutePath(), ".designtimelibs");
        } else {
            dotDesignTimeLibsFile = new File(getProjectLocation(), ".designtimelibs");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dotDesignTimeLibsFile));
            writer.write("#Design time libraries\n");
            writer.write("#Format: #=File Alias=Description\n");
            //writer.write("#" + new Date().toString() + "\n");
            int index = 0;
            for (Artifact artifact : project.getArtifacts()) {
                if ("projlib".equals(artifact.getType())) {
                    String alias = getAlias(artifact);
                    if (SystemUtils.IS_OS_WINDOWS) {
                        //@TODO find a better solution the replace().replace() 
                        writer.write(index++ + "=" + alias + "\\="
                                + artifact.getFile().getAbsolutePath().replace("\\", "\\\\").replace(":\\\\", ":\\")
                                + "\n");
                    } else {
                        writer.write(index++ + "=" + alias + "\\=" + artifact.getFile().getAbsolutePath() + "\n");
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't create .designtimelibs file!\n Reason :" + e.getMessage());
        }
    }

    /**
     * Update the designer preference file with the preferences that don't exist
     * 
     * @throws MojoExecutionException
     */
    private void updateDesignerPrefs() throws MojoExecutionException {
        getLog().debug("DEBUG --> Updating ... designer prefs in: " + designerPrefsFile);
        LinkedHashMap<String, String> prefs = readDesignerPrefs();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(designerPrefsFile, true));

            int index = prefs.size();
            for (Artifact artifact : project.getArtifacts()) {
                String alias = getAlias(artifact);
                if (!prefs.containsKey(alias)) {
                    if (SystemUtils.IS_OS_WINDOWS) {
                        //@TODO find a better solution the replace().replace() 
                        writer.write("filealias.pref." + index++ + "=" + alias + "\\="
                                + artifact.getFile().getAbsolutePath().replace("\\", "\\\\").replace(":\\\\", ":\\")
                                + "\n");
                    } else {
                        writer.write("filealias.pref." + index++ + "=" + alias + "\\="
                                + artifact.getFile().getAbsolutePath() + "\n");
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't create .designtimelibs file!\n Reason :" + e.getMessage());
        }
    }

    /**
     * Read the designer preferences
     * 
     * @return a map of which the key is an alias and the value is a location on disk where the alias can be found.
     * 
     * @throws MojoExecutionException
     */
    private LinkedHashMap<String, String> readDesignerPrefs() throws MojoExecutionException {
        getLog().debug("DEBUG --> " + this.getClass().getName() + " read designer prefs in: " + designerPrefsFile);
        // Since the preferences can be out of order, we first read them into a map
        HashMap<Integer, String> designerPrefsMap = new HashMap<Integer, String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(designerPrefsFile));

            String line;
            Pattern pattern = Pattern.compile("filealias\\.pref\\.(\\d+?)=(.*)");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    designerPrefsMap.put(Integer.valueOf(matcher.group(1)), matcher.group(2));
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't create .designtimelibs file!");
        }

        Pattern pattern = Pattern.compile("(.*?)\\\\=(.*)");
        LinkedHashMap<String, String> designerPrefs = new LinkedHashMap<String, String>(designerPrefsMap.size());
        for (int index = 0; index < designerPrefsMap.size(); index++) {
            Matcher matcher = pattern.matcher(designerPrefsMap.get(index));
            if (matcher.find()) {
                designerPrefs.put(matcher.group(1), matcher.group(2));
            }
        }
        return designerPrefs;
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
        //buf.append("-");
        //buf.append(artifact.getBaseVersion());
        buf.append(".");
        buf.append(artifact.getType());
        getLog().debug(this.getClass().getName() + "::getAlias() return :" + buf.toString());
        return buf.toString();
    }

    @Override
    protected String getClassifier() {
        this.getLog().debug(this.getClass().getName() + "...getClassifier for ... " + this.project.getArtifactId());
        return null;
    }

    @Override
    protected List<String> getSources(MavenProject p) throws MojoExecutionException {
        this.getLog().debug(this.getClass().getName() + "...getSources for ..." + p.getArtifactId());
        return null;
    }

    @Override
    protected List<Resource> getResources(MavenProject p) throws MojoExecutionException {
        this.getLog().debug(this.getClass().getName() + "...getResources for ..." + p.getArtifactId());
        return p.getResources();
    }
}
