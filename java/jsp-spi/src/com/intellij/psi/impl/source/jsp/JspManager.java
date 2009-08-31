/*
 * Copyright (c) 2000-2007 JetBrains s.r.o. All Rights Reserved.
 */
package com.intellij.psi.impl.source.jsp;

import com.intellij.lang.jsp.IBaseJspManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author peter
 */
public abstract class JspManager implements IBaseJspManager {

  public static final Key<VirtualFile[]> DIRECTORIES_KEY = Key.create("TagDirOriginalDirs");

  public static JspManager getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, JspManager.class);
  }

  @NotNull
  public abstract Set<String> getNamespacesByTagName(@NotNull String tagName, @NotNull JspFile context, final boolean showProgress);
  @NotNull
  public abstract Set<String> getNamespacesByFunctionName(@NotNull String tagName, @NotNull JspFile context, final boolean showProgress);

  /**
   * Returns possible tag names for given context JSP file.
   * @param context context JSP file.
   * @return set of tag names
   */
  @NotNull
  public abstract MultiMap<String,String> getAvailableTagNames(@NotNull final JspFile context);

  @NotNull
  public abstract List<Pair<String,String>> getAvailableFunctions(@NotNull final JspFile context);

  @Nullable
  public abstract String getPrefixForNamespace(@NotNull String namespaceUri, final @NotNull JspFile context);

  @Nullable
  public abstract String getDefaultPrefix(@NotNull XmlFile taglibFile);

  public abstract String[] getPossibleTldUris(JspFile file);

  public abstract String[] getPossibleTldUris(@NotNull Module module);

  public abstract Collection<XmlFile> getPossibleTldFiles(@NotNull Module module);

  @Nullable
  public abstract String getTaglibUri(@NotNull XmlFile taglibFile);

  @Nullable
  public abstract XmlFile getTldFileByUri(@NonNls String uri, @NotNull JspFile jspFile);

  @Nullable
  public abstract XmlFile getTldFileByUri(@NonNls String uri, @Nullable Module module, @Nullable JspFile jspFile);

  public abstract boolean isJsp_2_1_OrBetter(final @NotNull PsiFile context);
}