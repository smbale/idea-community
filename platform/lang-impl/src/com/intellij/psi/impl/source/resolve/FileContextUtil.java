/*
 * @author max
 */
package com.intellij.psi.impl.source.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPsiElementPointer;

public class FileContextUtil {
  public static final Key<SmartPsiElementPointer> INJECTED_IN_ELEMENT = Key.create("injectedIn");

  public static PsiElement getFileContext(PsiFile file) {
    SmartPsiElementPointer pointer = file.getUserData(INJECTED_IN_ELEMENT);
    return pointer == null ? null : pointer.getElement();
  }

  public static PsiFile getContextFile(PsiElement element){
    if (!element.isValid()) return null;
    PsiFile file = element.getContainingFile();
    if (file == null) return null;
    PsiElement context = file.getContext();
    if (context == null){
      return file;
    }
    else{
      return getContextFile(context);
    }
  }
}