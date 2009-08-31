package com.intellij.openapi.vcs.impl.projectlevelman;

import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.vcs.*;
import com.intellij.util.containers.Convertor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class OptionsAndConfirmations {
  private final Map<String, VcsShowOptionsSettingImpl> myOptions;
  private final Map<String, VcsShowConfirmationOptionImpl> myConfirmations;

  public OptionsAndConfirmations() {
    myOptions = new LinkedHashMap<String, VcsShowOptionsSettingImpl>();
    myConfirmations = new LinkedHashMap<String, VcsShowConfirmationOptionImpl>();
  }

  public void init(final Convertor<String, VcsShowConfirmationOption.Value> initOptions) {
    createSettingFor(VcsConfiguration.StandardOption.ADD);
    createSettingFor(VcsConfiguration.StandardOption.REMOVE);
    createSettingFor(VcsConfiguration.StandardOption.CHECKOUT);
    createSettingFor(VcsConfiguration.StandardOption.UPDATE);
    createSettingFor(VcsConfiguration.StandardOption.STATUS);
    createSettingFor(VcsConfiguration.StandardOption.EDIT);

    myConfirmations.put(VcsConfiguration.StandardConfirmation.ADD.getId(), new VcsShowConfirmationOptionImpl(
      VcsConfiguration.StandardConfirmation.ADD.getId(),
      VcsBundle.message("label.text.when.files.created.with.idea", ApplicationNamesInfo.getInstance().getProductName()),
      VcsBundle.message("radio.after.creation.do.not.add"), VcsBundle.message("radio.after.creation.show.options"),
      VcsBundle.message("radio.after.creation.add.silently")));

    myConfirmations.put(VcsConfiguration.StandardConfirmation.REMOVE.getId(), new VcsShowConfirmationOptionImpl(
      VcsConfiguration.StandardConfirmation.REMOVE.getId(),
      VcsBundle.message("label.text.when.files.are.deleted.with.idea", ApplicationNamesInfo.getInstance().getProductName()),
      VcsBundle.message("radio.after.deletion.do.not.remove"), VcsBundle.message("radio.after.deletion.show.options"),
      VcsBundle.message("radio.after.deletion.remove.silently")));

    restoreReadConfirm(VcsConfiguration.StandardConfirmation.ADD, initOptions);
    restoreReadConfirm(VcsConfiguration.StandardConfirmation.REMOVE, initOptions);
  }

  private void restoreReadConfirm(final VcsConfiguration.StandardConfirmation confirm,
                                  final Convertor<String, VcsShowConfirmationOption.Value> initOptions) {
    final VcsShowConfirmationOption.Value initValue = initOptions.convert(confirm.getId());
    if (initValue != null) {
      getConfirmation(confirm).setValue(initValue);
    }
  }

  @NotNull
  public VcsShowConfirmationOptionImpl getConfirmation(VcsConfiguration.StandardConfirmation option) {
    return myConfirmations.get(option.getId());
  }

  private void createSettingFor(final VcsConfiguration.StandardOption option) {
    if (!myOptions.containsKey(option.getId())) {
      myOptions.put(option.getId(), new VcsShowOptionsSettingImpl(option));
    }
  }

  @NotNull
  public VcsShowSettingOption getOptions(VcsConfiguration.StandardOption option) {
    return myOptions.get(option.getId());
  }

  public List<VcsShowOptionsSettingImpl> getAllOptions() {
    return new ArrayList<VcsShowOptionsSettingImpl>(myOptions.values());
  }

  public List<VcsShowConfirmationOptionImpl> getAllConfirmations() {
    return new ArrayList<VcsShowConfirmationOptionImpl>(myConfirmations.values());
  }

  @NotNull
  public VcsShowSettingOption getOrCreateCustomOption(@NotNull String vcsActionName, @NotNull AbstractVcs vcs) {
    final VcsShowOptionsSettingImpl option = getOrCreateOption(vcsActionName);
    option.addApplicableVcs(vcs);
    return option;
  }

  private VcsShowOptionsSettingImpl getOrCreateOption(String actionName) {
    if (!myOptions.containsKey(actionName)) {
      myOptions.put(actionName, new VcsShowOptionsSettingImpl(actionName));
    }
    return myOptions.get(actionName);
  }

  // open for serialization purposes
  Map<String, VcsShowOptionsSettingImpl> getOptions() {
    return myOptions;
  }

  // open for serialization purposes
  Map<String, VcsShowConfirmationOptionImpl> getConfirmations() {
    return myConfirmations;
  }
}