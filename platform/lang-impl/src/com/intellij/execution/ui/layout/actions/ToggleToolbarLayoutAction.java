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

package com.intellij.execution.ui.layout.actions;

import com.intellij.execution.ui.layout.impl.RunnerContentUi;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.Nullable;

public class ToggleToolbarLayoutAction extends ToggleAction {

  public void update(final AnActionEvent e) {
    if (getRunnerUi(e) == null) {
      e.getPresentation().setEnabled(false);
    } else {
      super.update(e);
    }
  }

  public boolean isSelected(final AnActionEvent e) {
    final RunnerContentUi ui = getRunnerUi(e);
    return ui != null ? ui.isHorizontalToolbar() : false;
  }

  public void setSelected(final AnActionEvent e, final boolean state) {
    getRunnerUi(e).setHorizontalToolbar(state);
  }

  @Nullable
  public static RunnerContentUi getRunnerUi(final AnActionEvent e) {
    return RunnerContentUi.KEY.getData(e.getDataContext());
  }

}
