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
package org.apache.karaf.features.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.karaf.features.Feature;
import org.apache.felix.utils.manifest.Clause;
import org.easymock.EasyMock;
import org.osgi.framework.BundleContext;

import static org.easymock.EasyMock.*;

/**
 * Test cases for {@link FeaturesServiceImpl}
 */
public class FeaturesServiceImplTest extends TestCase {
    
    File dataFile;

    protected void setUp() throws IOException {
        dataFile = File.createTempFile("features", null, null);
    }

    public void testGetFeature() throws Exception {
        final Map<String, Map<String, Feature>> features = new HashMap<String, Map<String,Feature>>();
        Map<String, Feature> versions = new HashMap<String, Feature>();
        FeatureImpl feature = new FeatureImpl("transaction");
        versions.put("1.0.0", feature);
        features.put("transaction", versions);
        final FeaturesServiceImpl impl = new FeaturesServiceImpl() {
            protected Map<String,Map<String,Feature>> getFeatures() throws Exception {
                return features;
            };
        };
        assertNotNull(impl.getFeature("transaction", FeatureImpl.DEFAULT_VERSION));
        assertSame(feature, impl.getFeature("transaction", FeatureImpl.DEFAULT_VERSION));
    }
    
    public void testGetFeatureStripVersion() throws Exception {
        final Map<String, Map<String, Feature>> features = new HashMap<String, Map<String,Feature>>();
        Map<String, Feature> versions = new HashMap<String, Feature>();
        FeatureImpl feature = new FeatureImpl("transaction");
        versions.put("1.0.0", feature);
        features.put("transaction", versions);
        final FeaturesServiceImpl impl = new FeaturesServiceImpl() {
            protected Map<String,Map<String,Feature>> getFeatures() throws Exception {
                return features;
            };
        };
        assertNotNull(impl.getFeature("transaction", "  1.0.0  "));
        assertSame(feature, impl.getFeature("transaction", "  1.0.0   "));
    }
    
    public void testGetFeatureNotAvailable() throws Exception {
        final Map<String, Map<String, Feature>> features = new HashMap<String, Map<String,Feature>>();
        Map<String, Feature> versions = new HashMap<String, Feature>();
        versions.put("1.0.0", new FeatureImpl("transaction"));
        features.put("transaction", versions);
        final FeaturesServiceImpl impl = new FeaturesServiceImpl() {
            protected Map<String,Map<String,Feature>> getFeatures() throws Exception {
                return features;
            };
        };
        assertNull(impl.getFeature("activemq", FeatureImpl.DEFAULT_VERSION));
    }
    
    public void testGetFeatureHighestAvailable() throws Exception {
        final Map<String, Map<String, Feature>> features = new HashMap<String, Map<String,Feature>>();
        Map<String, Feature> versions = new HashMap<String, Feature>();
        versions.put("1.0.0", new FeatureImpl("transaction", "1.0.0"));
        versions.put("2.0.0", new FeatureImpl("transaction", "2.0.0"));
        features.put("transaction", versions);
        final FeaturesServiceImpl impl = new FeaturesServiceImpl() {
            protected Map<String,Map<String,Feature>> getFeatures() throws Exception {
                return features;
            };
        };
        assertNotNull(impl.getFeature("transaction", FeatureImpl.DEFAULT_VERSION));
        assertSame("2.0.0", impl.getFeature("transaction", FeatureImpl.DEFAULT_VERSION).getVersion());
    }

    public void testStartDoesNotFailWithOneInvalidUri()  {
        BundleContext bundleContext = EasyMock.createMock(BundleContext.class);
        expect(bundleContext.getDataFile(EasyMock.<String>anyObject())).andReturn(dataFile).anyTimes();
        replay(bundleContext);
        FeaturesServiceImpl service = new FeaturesServiceImpl();
        service.setBundleContext(bundleContext);
        try {
            service.setUrls("mvn:inexistent/features/1.0/xml/features");
            service.start();
        } catch (Exception e) {
            fail(String.format("Service should not throw start-up exception but log the error instead: %s", e));
        }
    }

    public void testGetOptionalImportsOnly() {
        FeaturesServiceImpl service = new FeaturesServiceImpl();

        List<Clause> result = service.getOptionalImports("org.apache.karaf,org.apache.karaf.optional;resolution:=optional");
        assertEquals("One optional import expected", 1, result.size());
        assertEquals("org.apache.karaf.optional", result.get(0).getName());

        result = service.getOptionalImports(null);
        assertNotNull(result);
        assertEquals("No optional imports expected", 0, result.size());
    }
}
