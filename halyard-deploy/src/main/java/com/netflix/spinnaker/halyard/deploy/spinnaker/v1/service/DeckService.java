/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service;


import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.ApachePortsProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.ApacheSpinnakerProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.DeckProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
abstract public class DeckService extends SpinnakerService<DeckService.Deck> {
  final boolean safeToUpdate = true;
  final boolean monitored = true;

  @Autowired
  DeckProfileFactory deckProfileFactory;

  @Autowired
  ApachePortsProfileFactory apachePortsProfileFactory;

  @Autowired
  ApacheSpinnakerProfileFactory apacheSpinnakerProfileFactory;

  @Override
  public Class<Deck> getEndpointClass() {
    return Deck.class;
  }

  @Override
  public SpinnakerArtifact getArtifact() {
    return SpinnakerArtifact.DECK;
  }

  @Override
  public Type getType() {
    return Type.DECK;
  }

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration, SpinnakerRuntimeSettings endpoints) {
    List<Profile> result = new ArrayList<>();
    String htmlPath = "/opt/deck/html/";
    String portsPath = "/etc/apache2/";
    String sitePath = "/etc/apache2/sites-available/";
    String filename = "settings.js";
    String path = Paths.get(htmlPath, filename).toString();
    result.add(deckProfileFactory.getProfile(filename, path, deploymentConfiguration, endpoints));
    filename = "ports.conf";
    path = Paths.get(portsPath, filename).toString();
    result.add(apachePortsProfileFactory.getProfile("apache2/" + filename, path, deploymentConfiguration, endpoints));
    filename = "spinnaker.conf";
    path = Paths.get(sitePath, filename).toString();
    result.add(apacheSpinnakerProfileFactory.getProfile("apache2/" + filename, path, deploymentConfiguration, endpoints));

    return result;
  }

  protected DeckService() {
    super();
  }

  public interface Deck { }

  @EqualsAndHashCode(callSuper = true)
  @Data
  public static class Settings extends ServiceSettings {
    int port = 9000;
    String address = "localhost";
    String host = "0.0.0.0";
    String scheme = "http";
    String healthEndpoint = null;
    boolean enabled = true;

    public Settings() {}
  }
}