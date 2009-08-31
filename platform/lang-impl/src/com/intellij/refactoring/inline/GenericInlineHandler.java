package com.intellij.refactoring.inline;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.lang.refactoring.InlineHandlers;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.ui.ConflictsDialog;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.HashSet;

import java.util.*;

/**
 * @author ven
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public class GenericInlineHandler {

  public static boolean invoke(final PsiElement element, final Editor editor, final InlineHandler languageSpecific) {
    final PsiReference invocationReference = TargetElementUtilBase.findReference(editor);
    final InlineHandler.Settings settings = languageSpecific.prepareInlineElement(element, editor, invocationReference != null);
    if (settings == null) return false;

    final Collection<PsiReference> allReferences =
      settings.isOnlyOneReferenceToInline() ? Collections.singleton(invocationReference) : ReferencesSearch.search(element).findAll();
    final Map<Language, InlineHandler.Inliner> inliners = new HashMap<Language, InlineHandler.Inliner>();

    final Set<String> conflicts = new HashSet<String>();
    for (PsiReference ref : allReferences) {
      final Language language = ref.getElement().getLanguage();
      if (inliners.containsKey(language)) continue;

      InlineHandler.Inliner inliner = null;
      final List<InlineHandler> handlers = InlineHandlers.getInlineHandlers(language);
      for (InlineHandler handler : handlers) {
        inliner = handler.createInliner(element);
        if (inliner != null) {
          inliners.put(language, inliner);
          break;
        }
      }
      if (inliner == null) {
        conflicts.add("Cannot inline reference from " + language.getID());
      }
    }

    for (PsiReference reference : allReferences) {
      collectConflicts(reference, element, inliners, conflicts);
    }

    final Project project = element.getProject();
    if (!conflicts.isEmpty()) {
      if (ApplicationManager.getApplication().isUnitTestMode()) {
        throw new ConflictsFoundInTestException("Refactoring cannot be performed:" + conflicts.iterator().next());
      } else {
        final ConflictsDialog conflictsDialog = new ConflictsDialog(project, conflicts);
        conflictsDialog.show();
        if (!conflictsDialog.isOK()) return true;
      }
    }

    HashSet<PsiElement> elements = new HashSet<PsiElement>();
    for (PsiReference reference : allReferences) {
      PsiElement refElement = reference.getElement();
      if (refElement != null) {
        elements.add(refElement);
      }
    }
    if (!settings.isOnlyOneReferenceToInline()) {
      elements.add(element);
    }

    if (!CommonRefactoringUtil.checkReadOnlyStatusRecursively(project, elements)) {
      return true;
    }
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        final String subj = element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : "element";

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
          public void run() {
            for (PsiReference reference : allReferences) {
              inlineReference(reference, element, inliners);
            }

            if (!settings.isOnlyOneReferenceToInline()) {
              languageSpecific.removeDefinition(element);
            }
          }
        }, RefactoringBundle.message("inline.command", subj), null);
      }
    });
    return true;
  }

  private static void collectConflicts(final PsiReference reference,
                                       final PsiElement element,
                                       final Map<Language, InlineHandler.Inliner> inliners,
                                       final Set<String> conflicts) {
    final Language language = reference.getElement().getLanguage();
    final InlineHandler.Inliner inliner = inliners.get(language);
    if (inliner != null) {
      final Collection<String> refConflicts = inliner.getConflicts(reference, element);
      if (refConflicts != null) {
        conflicts.addAll(refConflicts);
      }
    }
  }

  private static void inlineReference(final PsiReference reference,
                                      final PsiElement element,
                                      final Map<Language, InlineHandler.Inliner> inliners) {
    final Language language = reference.getElement().getLanguage();
    final InlineHandler.Inliner inliner = inliners.get(language);
    if (inliner != null) {
      inliner.inlineReference(reference, element);
    }
  }

  public static class ConflictsFoundInTestException extends RuntimeException {
    public ConflictsFoundInTestException(String message) {
      super(message);
    }
  }
}