/*
 * Created by IntelliJ IDEA.
 * User: mike
 * Date: Jun 7, 2002
 * Time: 8:27:57 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.intellij;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassFinder {
  private final List<String> classNameList = new ArrayList<String>();
  private final int startPackageName;

  public ClassFinder(final File classPathRoot, final String packageRoot) throws IOException {
    startPackageName = classPathRoot.getAbsolutePath().length() + 1;
    String directoryOffset = packageRoot.replace('.', File.separatorChar);
    findAndStoreTestClasses(new File(classPathRoot, directoryOffset));
  }

  private String computeClassName(final File file) {
    String absPath = file.getAbsolutePath();
    if (absPath.endsWith("Test.class")) {
      String packageBase = absPath.substring(startPackageName, absPath.length() - ".class".length());
      return packageBase.replace(File.separatorChar, '.');
    }
    else {
      return null;
    }
  }

  private void findAndStoreTestClasses(final File current) throws IOException {
    if (current.isDirectory()) {
      for (File file : current.listFiles()) {
        findAndStoreTestClasses(file);
      }
    }
    else {
      String className = computeClassName(current);
      if (className != null) {
        classNameList.add(className);
      }
    }
  }

  public Collection<String> getClasses() {
    return classNameList;
  }
}