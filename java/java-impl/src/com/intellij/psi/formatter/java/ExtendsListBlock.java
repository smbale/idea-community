package com.intellij.psi.formatter.java;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

public class ExtendsListBlock extends AbstractJavaBlock{
  public ExtendsListBlock(final ASTNode node, final Wrap wrap, final Alignment alignment, CodeStyleSettings settings) {
    super(node, wrap, alignment, Indent.getNoneIndent(), settings);
  }

  protected List<Block> buildChildren() {
    final ArrayList<Block> result = new ArrayList<Block>();
    ArrayList<Block> elementsExceptKeyword = new ArrayList<Block>();
    myChildAlignment = createChildAlignment();
    myChildIndent = Indent.getContinuationIndent();
    myUseChildAttributes = true;
    Wrap childWrap = createChildWrap();
    ASTNode child = myNode.getFirstChildNode();

    Alignment alignment = alignList() ? Alignment.createAlignment() : null;

    while (child != null) {
      if (!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0){
        if (ElementType.KEYWORD_BIT_SET.contains(child.getElementType())) {
          if (!elementsExceptKeyword.isEmpty()) {
            result.add(new SyntheticCodeBlock(elementsExceptKeyword, null,  mySettings, Indent.getNoneIndent(), null));
            elementsExceptKeyword = new ArrayList<Block>();
          }
          result.add(createJavaBlock(child, mySettings, myChildIndent, arrangeChildWrap(child, childWrap), alignment));
        } else {
          processChild(elementsExceptKeyword, child, myChildAlignment, childWrap, myChildIndent);

        }
      }
      child = child.getTreeNext();
    }
    if (!elementsExceptKeyword.isEmpty()) {
      result.add(new SyntheticCodeBlock(elementsExceptKeyword, alignment,  mySettings, Indent.getNoneIndent(), null));
    }

    return result;

  }

  private boolean alignList() {
    if (myNode.getElementType() == ElementType.EXTENDS_LIST) {
      return mySettings.ALIGN_MULTILINE_EXTENDS_LIST;
    } else if (myNode.getElementType() == ElementType.IMPLEMENTS_LIST) {
      return mySettings.ALIGN_MULTILINE_EXTENDS_LIST;
    } else if (myNode.getElementType() == ElementType.THROWS_LIST) {
      return mySettings.ALIGN_MULTILINE_THROWS_LIST;
    }
    return false;
  }

  protected Wrap getReservedWrap(final IElementType elementType) {
    return null;
  }

  protected void setReservedWrap(final Wrap reservedWrap, final IElementType operationType) {
  }
}