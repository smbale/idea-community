/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package com.intellij.util.ui;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class ColorIcon extends EmptyIcon {
  private final Color myColor;

  public ColorIcon(int size, @NotNull Color color) {
    super(size, size);
    myColor = color;
  }

  public Color getIconColor() {
    return myColor;
  }

  @Override
  public void paintIcon(final Component component, final Graphics g, final int i, final int j) {
    final int iconWidth = getIconWidth();
    final int iconHeight = getIconHeight();
    g.setColor(getIconColor());
    g.fillRect(i, j, iconWidth, iconHeight);
  }
}
