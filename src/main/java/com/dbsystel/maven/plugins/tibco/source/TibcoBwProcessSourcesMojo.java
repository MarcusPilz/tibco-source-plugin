package com.dbsystel.maven.plugins.tibco.source;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwSourceMojo;

@Mojo(name = "process-sources", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true,
        executionStrategy = "once-per-session", requiresDependencyResolution = ResolutionScope.TEST)
public class TibcoBwProcessSourcesMojo extends AbstractTibcoBwSourceMojo {

    /**
     * 
     */
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (isSkipSource()) {
            this.getLog().info("Skipping process-sources per configuration...");
            return;
        }
        this.getLog().debug(this.getClass().getName() + "::projectLocation::" + getProjectLocation());
        this.getLog().debug(
                this.getClass().getName() + "::tibcoBuildDirectory::" + tibcoBuildDirectory.getAbsolutePath());
        File sourceDirectory = null;
        File destinationDirectory = tibcoBuildDirectory;
        //File destinationDirectory = new File(tibcoBuildDirectory + File.separator + this.project.getArtifactId());
        if (getProjectLocation().equals(".")
                || getProjectLocation().equals(this.project.getBasedir().getAbsolutePath())) {
            sourceDirectory = new File(project.getBasedir().getAbsolutePath() + File.separator + "src"
                    + File.separator + "main" + File.separator + "bw");
        } else {
            sourceDirectory = new File(getProjectLocation());

        }

        if (!destinationDirectory.exists()) {
            FileUtils.mkdir(destinationDirectory.getAbsolutePath());
        }

        getLog().info(
                "Copying sources from " + sourceDirectory.getAbsolutePath() + " to "
                        + destinationDirectory.getAbsolutePath());

        try {
            String inc = new String("**/**");
            String ex = new String(File.separator + "Deployments");
            FileUtils.copyDirectoryStructure(sourceDirectory, destinationDirectory);
            //FileUtils.copyDirectoryStructure(sourceDirectory, destinationDirectory);
            //FileUtils.copyDirectory(sourceDirectory, destinationDirectory, inc, ex);
        } catch (IOException e) {
            throw new MojoExecutionException("Can't copy sources to: " + destinationDirectory);
        }
    }

    @Override
    protected String getClassifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<String> getSources(MavenProject p) throws MojoExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<Resource> getResources(MavenProject p) throws MojoExecutionException {
        // TODO Auto-generated method stub
        return null;
    }
}
