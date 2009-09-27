// Generated on Mon Mar 17 18:02:09 MSK 2008
// DTD/Schema  :    http://maven.apache.org/POM/4.0.0

package org.jetbrains.idea.maven.dom.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.MavenDomElement;

import java.util.List;

/**
 * http://maven.apache.org/POM/4.0.0:notifiersElemType interface.
 */
public interface MavenDomNotifiers extends MavenDomElement {

  /**
   * Returns the list of notifier children.
   *
   * @return the list of notifier children.
   */
  @NotNull
  List<MavenDomNotifier> getNotifiers();

  /**
   * Adds new child to the list of notifier children.
   *
   * @return created child
   */
  MavenDomNotifier addNotifier();
}