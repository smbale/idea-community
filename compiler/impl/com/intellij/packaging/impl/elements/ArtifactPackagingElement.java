package com.intellij.packaging.impl.elements;

import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.elements.ComplexPackagingElement;
import com.intellij.packaging.elements.PackagingElement;
import com.intellij.packaging.elements.PackagingElementResolvingContext;
import com.intellij.packaging.impl.ui.ArtifactElementPresentation;
import com.intellij.packaging.ui.PackagingEditorContext;
import com.intellij.packaging.ui.PackagingElementPresentation;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nik
 */
public class ArtifactPackagingElement extends ComplexPackagingElement<ArtifactPackagingElement> {
  private String myArtifactName;

  public ArtifactPackagingElement() {
    super(ArtifactElementType.ARTIFACT_ELEMENT_TYPE);
  }

  public ArtifactPackagingElement(String artifactName) {
    super(ArtifactElementType.ARTIFACT_ELEMENT_TYPE);
    myArtifactName = artifactName;
  }

  public List<? extends PackagingElement<?>> getSubstitution(@NotNull PackagingElementResolvingContext context) {
    final Artifact artifact = findArtifact(context);
    if (artifact != null) {
      final List<PackagingElement<?>> elements = new ArrayList<PackagingElement<?>>();
      elements.addAll(artifact.getRootElement().getChildren());
      return elements;
    }
    return null;
  }

  public PackagingElementPresentation createPresentation(PackagingEditorContext context) {
    return new ArtifactElementPresentation(myArtifactName, findArtifact(context));
  }

  public ArtifactPackagingElement getState() {
    return this;
  }

  public void loadState(ArtifactPackagingElement state) {
    myArtifactName = state.getArtifactName();
  }

  @Attribute("artifact-name")
  public String getArtifactName() {
    return myArtifactName;
  }

  @Nullable
  public Artifact findArtifact(@NotNull PackagingElementResolvingContext context) {
    return context.getArtifactModel().findArtifact(myArtifactName);
  }

  public void setArtifactName(String artifactName) {
    myArtifactName = artifactName;
  }
}