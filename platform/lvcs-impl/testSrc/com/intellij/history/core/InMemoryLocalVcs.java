package com.intellij.history.core;

import com.intellij.history.core.storage.Content;
import com.intellij.history.core.storage.Storage;

import java.io.IOException;

public class InMemoryLocalVcs extends LocalVcs {
  public InMemoryLocalVcs() {
    this(new InMemoryStorage());
  }

  public InMemoryLocalVcs(Storage s) {
    super(s);
  }

  @Override
  protected Content createContentFrom(ContentFactory f) {
    try {
      if (f == null || f.getBytes() == null) return null;
      return f.createContent(myStorage);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected boolean contentWasNotChanged(String path, ContentFactory f) {
    if (f == null) return false;
    if (getEntry(path).getContent() == null) return false;
    return super.contentWasNotChanged(path, f);
  }
}