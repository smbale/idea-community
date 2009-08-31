/*
 * Copyright 2000-2007 JetBrains s.r.o.
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
package com.intellij.openapi.application;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class PathMacros {

  public static PathMacros getInstance() {
    return ApplicationManager.getApplication().getComponent(PathMacros.class);
  }

  public abstract Set<String> getAllMacroNames();

  public abstract String getValue(String name);

  public abstract String getDescription(String name);

  public abstract void setMacro(String name, String value, @Nullable String description);

  public abstract void removeMacro(String name);

  public abstract Set<String> getUserMacroNames();

  public abstract Set<String> getSystemMacroNames();

  public abstract void removeAllMacros();
}