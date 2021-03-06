/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.psi.impl.file;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.util.IncorrectOperationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PsiFileImplUtil {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.impl.file.PsiFileImplUtil");

  public static PsiFile setName(final PsiFile file, String newName) throws IncorrectOperationException {
    VirtualFile vFile = file.getViewProvider().getVirtualFile();
    PsiManagerImpl manager = (PsiManagerImpl)file.getManager();

    try{
      final FileType newFileType = FileTypeManager.getInstance().getFileTypeByFileName(newName);
      if (FileTypes.UNKNOWN.equals(newFileType) || newFileType.isBinary()) {
        // before the file becomes unknown or a binary (thus, not openable in the editor), save it to prevent data loss
        final FileDocumentManager fdm = FileDocumentManager.getInstance();
        final Document doc = fdm.getCachedDocument(vFile);
        if (doc != null) {
          EditorSettingsExternalizable editorSettings = EditorSettingsExternalizable.getInstance();
          
          String trailer = editorSettings.getStripTrailingSpaces();
          boolean ensureEOLonEOF = editorSettings.isEnsureNewLineAtEOF();
          editorSettings.setStripTrailingSpaces(EditorSettingsExternalizable.STRIP_TRAILING_SPACES_NONE);
          editorSettings.setEnsureNewLineAtEOF(false);
          try {
            fdm.saveDocument(doc);
          }
          finally {
            editorSettings.setStripTrailingSpaces(trailer);
            editorSettings.setEnsureNewLineAtEOF(ensureEOLonEOF);
          }
        }
      }

      vFile.rename(manager, newName);
    }
    catch(IOException e){
      throw new IncorrectOperationException(e.toString(),e);
    }

    return file.getViewProvider().isPhysical() ? manager.findFile(vFile) : file;
  }

  public static void checkSetName(PsiFile file, String name) throws IncorrectOperationException {
    VirtualFile vFile = file.getVirtualFile();
    VirtualFile parentFile = vFile.getParent();
    if (parentFile == null) return;
    VirtualFile child = parentFile.findChild(name);
    if (child != null && !child.equals(vFile)){
      throw new IncorrectOperationException("File " + child.getPresentableUrl() + " already exists.");
    }
  }

  public static void doDelete(final PsiFile file) throws IncorrectOperationException {
    final PsiManagerImpl manager = (PsiManagerImpl)file.getManager();

    final VirtualFile vFile = file.getVirtualFile();
    try{
      vFile.delete(manager);
    }
    catch(IOException e){
      throw new IncorrectOperationException(e.toString(),e);
    }
  }

  public static PsiFile[] getPsiFilesByVirtualFiles(VirtualFile[] files, PsiManager manager) {
    List<PsiFile> psiFiles = new ArrayList<PsiFile>();
    for (VirtualFile file : files) {
      PsiFile psiFile = manager.findFile(file);
      if (psiFile != null) {
        psiFiles.add(psiFile);
      }
    }
    return psiFiles.toArray(new PsiFile[psiFiles.size()]);
  }

  public static PsiFile[] getPsiFilesByVirtualFiles(List<VirtualFile> files, PsiManager manager) {
    List<PsiFile> psiFiles = new ArrayList<PsiFile>();

    for (VirtualFile file : files) {
      PsiFile psiFile = manager.findFile(file);
      if (psiFile != null) {
        psiFiles.add(psiFile);
      }
    }
    return psiFiles.toArray(new PsiFile[psiFiles.size()]);
  }
}
