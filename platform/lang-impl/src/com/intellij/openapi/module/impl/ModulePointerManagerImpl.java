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
package com.intellij.openapi.module.impl;

import com.intellij.ProjectTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModulePointer;
import com.intellij.openapi.module.ModulePointerManager;
import com.intellij.openapi.project.ModuleAdapter;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nik
 */
public class ModulePointerManagerImpl extends ModulePointerManager {
  private Map<String, ModulePointerImpl> myUnresolved = new HashMap<String, ModulePointerImpl>();
  private Map<Module, ModulePointerImpl> myPointers = new HashMap<Module, ModulePointerImpl>();
  private final Project myProject;

  public ModulePointerManagerImpl(Project project) {
    myProject = project;
    project.getMessageBus().connect().subscribe(ProjectTopics.MODULES, new ModuleAdapter() {
      @Override
      public void beforeModuleRemoved(Project project, Module module) {
        final ModulePointerImpl pointer = myPointers.remove(module);
        if (pointer != null) {
          pointer.moduleRemoved(module);
          myUnresolved.put(pointer.getModuleName(), pointer);
        }
      }

      @Override
      public void moduleAdded(Project project, Module module) {
        final ModulePointerImpl pointer = myUnresolved.remove(module.getName());
        if (pointer != null) {
          pointer.moduleAdded(module);
          myPointers.put(module, pointer);
        }
      }
    });
  }

  @Override
  public ModulePointer create(Module module) {
    ModulePointerImpl pointer = myPointers.get(module);
    if (pointer == null) {
      pointer = new ModulePointerImpl(module);
      myPointers.put(module, pointer);
    }
    return pointer;
  }

  @Override
  public ModulePointer create(String moduleName) {
    final Module module = ModuleManagerImpl.getInstance(myProject).findModuleByName(moduleName);
    if (module != null) {
      return create(module);
    }

    ModulePointerImpl pointer = myUnresolved.get(moduleName);
    if (pointer == null) {
      pointer = new ModulePointerImpl(moduleName);
      myUnresolved.put(moduleName, pointer);
    }
    return pointer;
  }
}