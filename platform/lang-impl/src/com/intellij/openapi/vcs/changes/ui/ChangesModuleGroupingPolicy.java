package com.intellij.openapi.vcs.changes.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;

/**
 * @author yole
 */
public class ChangesModuleGroupingPolicy implements ChangesGroupingPolicy {
  private final Project myProject;
  private final DefaultTreeModel myModel;
  private final HashMap<Module, ChangesBrowserNode> myModuleCache = new HashMap<Module, ChangesBrowserNode>();

  public static final String PROJECT_ROOT_TAG = "<Project Root>";

  public ChangesModuleGroupingPolicy(final Project project, final DefaultTreeModel model) {
    myProject = project;
    myModel = model;
  }

  @Nullable
  public ChangesBrowserNode getParentNodeFor(final ChangesBrowserNode node, final ChangesBrowserNode rootNode) {
    ProjectFileIndex index = ProjectRootManager.getInstance(myProject).getFileIndex();
    final FilePath path = TreeModelBuilder.getPathForObject(node.getUserObject());
    VirtualFile vFile = path.getVirtualFile();
    if (vFile != null && vFile == index.getContentRootForFile(vFile)) {
      Module module = index.getModuleForFile(vFile);
      return getNodeForModule(module, rootNode);
    }
    return null;
  }

  private ChangesBrowserNode getNodeForModule(Module module, ChangesBrowserNode root) {
    ChangesBrowserNode node = myModuleCache.get(module);
    if (node == null) {
      if (module == null) {
        node = ChangesBrowserNode.create(myProject, PROJECT_ROOT_TAG);
      }
      else {
        node = new ChangesBrowserModuleNode(module);
      }
      myModel.insertNodeInto(node, root, root.getChildCount());
      myModuleCache.put(module, node);
    }
    return node;
  }
}