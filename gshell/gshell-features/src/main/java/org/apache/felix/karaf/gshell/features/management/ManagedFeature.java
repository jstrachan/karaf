/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.karaf.gshell.features.management;

import java.util.List;

import org.apache.felix.karaf.gshell.features.Feature;
import org.apache.felix.karaf.gshell.features.FeaturesService;

/**
 * Managed Repository MBean
 */
public class ManagedFeature implements ManagedFeatureMBean {
    private Feature feature;
    private FeaturesService featuresService;
    private String id;

    public ManagedFeature(Feature feature, FeaturesService featuresService) {
        this.feature = feature;
        id = feature.getName() + "-" + feature.getVersion();
        this.featuresService = featuresService;
    }

    public String getId() {
        return id;    
    }

    public String getName() {
        return feature.getName();
    }

    public String getVersion() {
        return feature.getVersion();
    }

    public List<Feature> getDependencies() {
        return feature.getDependencies();
    }

    public List<String> getBundles() {
        return feature.getBundles();
    }

    public void installFeature() throws Exception {
        featuresService.installFeature(feature.getName(), feature.getVersion());
    }

    public void uninstallFeature() throws Exception {
        featuresService.uninstallFeature(feature.getName(), feature.getVersion());
    }

}
