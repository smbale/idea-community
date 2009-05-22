package com.intellij.compiler.ant.artifacts;

import com.intellij.compiler.ant.BuildProperties;
import com.intellij.compiler.ant.GenerationOptions;
import com.intellij.compiler.ant.GenerationUtils;
import com.intellij.compiler.ant.Generator;
import com.intellij.compiler.ant.taskdefs.Property;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectEx;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.packaging.elements.ArtifactAntGenerationContext;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author nik
 */
public class ArtifactAntGenerationContextImpl implements ArtifactAntGenerationContext {
  @NonNls public static final String ARTIFACTS_TEMP_DIR_PROPERTY = "artifacts.temp.dir";
  private Map<Artifact, String> myArtifact2Target = new THashMap<Artifact, String>();
  private List<Generator> myBeforeBuildGenerators = new ArrayList<Generator>();
  private List<Generator> myAfterBuildGenerators = new ArrayList<Generator>();
  private Set<String> myTempFileNames = new THashSet<String>();
  private Set<String> myProperties = new LinkedHashSet<String>();
  private final Project myProject;
  private GenerationOptions myGenerationOptions;
  private List<Generator> myBeforeCurrentArtifact = new ArrayList<Generator>();
  private Set<Artifact> myArtifactsToClean = new THashSet<Artifact>();

  public ArtifactAntGenerationContextImpl(Project project, GenerationOptions generationOptions) {
    myProject = project;
    myGenerationOptions = generationOptions;
    for (Artifact artifact : ArtifactManager.getInstance(project).getArtifacts()) {
      final String outputPath = artifact.getOutputPath();
      if (!StringUtil.isEmpty(outputPath) && artifact.isClearOutputDirectoryOnRebuild()) {
        myArtifactsToClean.add(artifact);
      }
    }
  }

  public String getConfiguredArtifactOutputProperty(@NotNull Artifact artifact) {
    return "artifact.output." + BuildProperties.convertName(artifact.getName());
  }

  public String getArtifactOutputProperty(@NotNull Artifact artifact) {
    if (shouldBuildIntoTempDirectory(artifact)) {
      return "artifact.temp.output." + BuildProperties.convertName(artifact.getName());
    }
    return getConfiguredArtifactOutputProperty(artifact);
  }

  public boolean shouldBuildIntoTempDirectory(@NotNull Artifact artifact) {
    return !myArtifactsToClean.contains(artifact);
  }

  public String getCleanTargetName(@NotNull Artifact artifact) {
    return "clean.artifact." + BuildProperties.convertName(artifact.getName());
  }

  public String getTargetName(@NotNull Artifact artifact) {
    String target = myArtifact2Target.get(artifact);
    if (target == null) {
      target = generateTargetName(artifact.getName());
      myArtifact2Target.put(artifact, target);
    }
    return target;
  }

  private static String generateTargetName(String artifactName) {
    return "artifact." + BuildProperties.convertName(artifactName);
  }

  public String getSubstitutedPath(String path) {
    return GenerationUtils.toRelativePath(path, VfsUtil.virtualToIoFile(myProject.getBaseDir()), BuildProperties.getProjectBaseDirProperty(), myGenerationOptions,
                                          ((ProjectEx)myProject).isSavePathsRelative());
  }

  public void runBeforeCurrentArtifact(Generator generator) {
    myBeforeCurrentArtifact.add(generator);
  }

  public void runBeforeBuild(Generator generator) {
    myBeforeBuildGenerators.add(generator);
  }

  public void runAfterBuild(Generator generator) {
    myAfterBuildGenerators.add(generator);
  }

  public String createNewTempFileProperty(String basePropertyName, String baseFileName) {
    String tempFileName = baseFileName;
    int i = 2;
    while (myTempFileNames.contains(tempFileName)) {
      tempFileName = baseFileName + i++;
    }

    String propertyName = basePropertyName;
    i = 2;
    while (myProperties.contains(propertyName)) {
      propertyName = basePropertyName + i++;
    }

    runBeforeBuild(new Property(propertyName, BuildProperties.propertyRelativePath(ARTIFACTS_TEMP_DIR_PROPERTY, tempFileName)));
    myTempFileNames.add(tempFileName);
    myProperties.add(propertyName);
    return propertyName;
  }

  public Generator[] getAndClearBeforeCurrentArtifact() {
    final Generator[] generators = myBeforeCurrentArtifact.toArray(new Generator[myBeforeCurrentArtifact.size()]);
    myBeforeCurrentArtifact.clear();
    return generators;
  }

  public String getModuleOutputPath(String moduleName) {
    return BuildProperties.getOutputPathProperty(moduleName);
  }

  public List<Generator> getBeforeBuildGenerators() {
    return myBeforeBuildGenerators;
  }

  public List<Generator> getAfterBuildGenerators() {
    return myAfterBuildGenerators;
  }
}