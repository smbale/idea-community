package com.intellij.testFramework;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.module.impl.ModuleImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.ModuleRootManagerImpl;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.JDOMException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ModuleTestCase extends IdeaTestCase {
  protected final Collection<Module> myModulesToDispose = new ArrayList<Module>();

  protected void setUp() throws Exception {
    super.setUp();
    myModulesToDispose.clear();
  }

  protected void tearDown() throws Exception {
    try {
      final ModuleManager moduleManager = ModuleManager.getInstance(myProject);
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        public void run() {
          for (Module module : myModulesToDispose) {
            String moduleName = module.getName();
            if (moduleManager.findModuleByName(moduleName) != null) {
              moduleManager.disposeModule(module);
            }
          }
        }
      });
    }
    finally {
      myModulesToDispose.clear();
      super.tearDown();
    }
  }

  protected Module createModule(final File moduleFile) {
    return createModule(moduleFile, StdModuleTypes.JAVA);
  }

  protected Module createModule(final File moduleFile, final ModuleType moduleType) {
    final String path = moduleFile.getAbsolutePath();
    return createModule(path, moduleType);
  }

  protected Module createModule(final String path) {
    return createModule(path, StdModuleTypes.JAVA);
  }

  protected Module createModule(final String path, final ModuleType moduleType) {
    Module module = ApplicationManager.getApplication().runWriteAction(
      new Computable<Module>() {
        public Module compute() {
          return ModuleManager.getInstance(myProject).newModule(path, moduleType);
        }
      }
    );

    myModulesToDispose.add(module);
    return module;
  }

  protected Module loadModule(final File moduleFile) {
    Module module = ApplicationManager.getApplication().runWriteAction(
      new Computable<Module>() {
        public Module compute() {
          try {
            return ModuleManager.getInstance(myProject).loadModule(moduleFile.getAbsolutePath());
          }
          catch (Exception e) {
            LOG.error(e);
            return null;
          }
        }
      }
    );

    myModulesToDispose.add(module);
    return module;
  }

  public Module loadModule(final String modulePath, Project project) throws InvalidDataException,
                                                                            IOException,
                                                                            JDOMException {
    return loadModule(new File(modulePath));
  }


  @Nullable
  protected ModuleImpl loadAllModulesUnder(VirtualFile rootDir) throws Exception {
    ModuleImpl module = null;
    final VirtualFile[] children = rootDir.getChildren();
    for (VirtualFile child : children) {
      if (child.isDirectory()) {
        final ModuleImpl childModule = loadAllModulesUnder(child);
        if (module == null) module = childModule;
      }
      else if (child.getName().endsWith(ModuleFileType.DOT_DEFAULT_EXTENSION)) {
        String modulePath = child.getPath();
        module = (ModuleImpl)loadModule(new File(modulePath));
        readJdomExternalizables(module);
      }
    }
    return module;
  }

  protected void readJdomExternalizables(ModuleImpl module) {
    final ProjectImpl project = (ProjectImpl)myProject;
    project.setOptimiseTestLoadSpeed(false);
    final ModuleRootManagerImpl moduleRootManager = (ModuleRootManagerImpl)ModuleRootManager.getInstance(module);
    module.getStateStore().initComponent(moduleRootManager);
    project.setOptimiseTestLoadSpeed(true);
  }

  protected Module createModuleFromTestData(final String dirInTestData, final String newModuleFileName, final ModuleType moduleType,
                                            final boolean addSourceRoot)
    throws IOException {
    final File dirInTestDataFile = new File(dirInTestData);
    assertTrue(dirInTestDataFile.isDirectory());
    final File moduleDir = createTempDirectory();
    FileUtil.copyDir(dirInTestDataFile, moduleDir);
    final Module module = createModule(moduleDir + "/" + newModuleFileName, moduleType);
    VirtualFile root = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(moduleDir);
    if (addSourceRoot) {
      PsiTestUtil.addSourceContentToRoots(module, root);
    }
    else {
      PsiTestUtil.addContentRoot(module, root);
    }
    return module;
  }
}