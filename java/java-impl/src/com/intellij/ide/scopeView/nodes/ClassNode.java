package com.intellij.ide.scopeView.nodes;

import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.presentation.java.ClassPresentationUtil;

/**
 * User: anna
 * Date: 30-Jan-2006
 */
public class ClassNode extends BasePsiNode<PsiClass> implements Comparable<ClassNode>{
  public ClassNode(final PsiClass aClass) {
    super(aClass);
  }

  public String toString() {
    final PsiClass aClass = (PsiClass)getPsiElement();
    return aClass != null && aClass.isValid() ? ClassPresentationUtil.getNameForClass(aClass, false) : "";
  }

  public int getWeight() {
    return 4;
  }

  @Override
  public boolean isDeprecated() {
    final PsiClass psiClass = (PsiClass)getPsiElement();
    return psiClass != null && psiClass.isDeprecated();
  }

  public int compareTo(final ClassNode o) {
    return ClassTreeNode.getClassPosition((PsiClass)getPsiElement()) - ClassTreeNode.getClassPosition((PsiClass)((ClassNode)o).getPsiElement());
  }
}