/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.eventadmin.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.*;

/**
 * The optional meta type provider for the event admin config.
 */
public class MetaTypeProviderImpl
    implements MetaTypeProvider, ManagedService
{
    private final int cacheSize;
    private final int threadPoolSize;
    private final int timeout;
    private final boolean requireTopic;

    private final ManagedService delegatee;

    public MetaTypeProviderImpl(final Configuration config,
                                final ManagedService delegatee)
    {
        this.cacheSize = config.getCacheSize();
        this.threadPoolSize = config.getThreadPoolSize();
        this.timeout = config.getTimeout();
        this.requireTopic = config.getRequireTopic();
        this.delegatee = delegatee;
    }

    private ObjectClassDefinition ocd;

    public void updated(Dictionary properties) throws ConfigurationException
    {
        this.delegatee.updated(properties);
    }

    /**
     * @see org.osgi.service.metatype.MetaTypeProvider#getLocales()
     */
    public String[] getLocales()
    {
        return null;
    }

    /**
     * @see org.osgi.service.metatype.MetaTypeProvider#getObjectClassDefinition(java.lang.String, java.lang.String)
     */
    public ObjectClassDefinition getObjectClassDefinition( String id, String locale )
    {
        if ( !Configuration.PID.equals( id ) )
        {
            return null;
        }

        if ( ocd == null )
        {
            final ArrayList adList = new ArrayList();

            adList.add( new AttributeDefinitionImpl( Configuration.PROP_CACHE_SIZE, "Cache Size",
                    "The size of various internal caches. The default value is 30. Increase in case " +
                    "of a large number (more then 100) of services. A value less then 10 triggers the " +
                    "default value.", this.cacheSize) );

            adList.add( new AttributeDefinitionImpl( Configuration.PROP_THREAD_POOL_SIZE, "Thread Pool Size",
                "The size of the thread pool. The default value is 10. Increase in case of a large amount " +
                "of synchronous events where the event handler services in turn send new synchronous events in " +
                "the event dispatching thread or a lot of timeouts are to be expected. A value of " +
                "less then 2 triggers the default value. A value of 2 effectively disables thread pooling.",
                this.threadPoolSize ) );

            adList.add( new AttributeDefinitionImpl( Configuration.PROP_TIMEOUT, "Timeout",
                    "The black-listing timeout in milliseconds. The default value is 5000. Increase or decrease " +
                    "at own discretion. A value of less then 100 turns timeouts off. Any other value is the time " +
                    "in milliseconds granted to each event handler before it gets blacklisted",
                    this.timeout ) );

            adList.add( new AttributeDefinitionImpl( Configuration.PROP_REQUIRE_TOPIC, "Require Topic",
                    "Are event handlers required to be registered with a topic? " +
                    "This is enabled by default. The specification says that event handlers " +
                    "must register with a list of topics they are interested in. Disabling this setting " +
                    "will enable that handlers without a topic are receiving all events " +
                    "(i.e., they are treated the same as with a topic=*).",
                    this.requireTopic ) );

            ocd = new ObjectClassDefinition()
            {

                private final AttributeDefinition[] attrs = ( AttributeDefinition[] ) adList
                    .toArray( new AttributeDefinition[adList.size()] );


                public String getName()
                {
                    return "Apache Felix Event Admin Implementation";
                }


                public InputStream getIcon( int arg0 )
                {
                    return null;
                }


                public String getID()
                {
                    return Configuration.PID;
                }


                public String getDescription()
                {
                    return "Configuration for the Apache Felix Event Admin Implementation." +
                           " This configuration overwrites configuration defined in framework properties of the same names.";
                }


                public AttributeDefinition[] getAttributeDefinitions( int filter )
                {
                    return ( filter == OPTIONAL ) ? null : attrs;
                }
            };
        }

        return ocd;
    }

    class AttributeDefinitionImpl implements AttributeDefinition
    {

        private final String id;
        private final String name;
        private final String description;
        private final int type;
        private final String[] defaultValues;
        private final int cardinality;
        private final String[] optionLabels;
        private final String[] optionValues;


        AttributeDefinitionImpl( final String id, final String name, final String description, final boolean defaultValue )
        {
            this( id, name, description, BOOLEAN, new String[]
                { String.valueOf(defaultValue) }, 0, null, null );
        }

        AttributeDefinitionImpl( final String id, final String name, final String description, final int defaultValue )
        {
            this( id, name, description, INTEGER, new String[]
                { String.valueOf(defaultValue) }, 0, null, null );
        }

        AttributeDefinitionImpl( final String id, final String name, final String description, final int type,
            final String[] defaultValues, final int cardinality, final String[] optionLabels,
            final String[] optionValues )
        {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.defaultValues = defaultValues;
            this.cardinality = cardinality;
            this.optionLabels = optionLabels;
            this.optionValues = optionValues;
        }


        public int getCardinality()
        {
            return cardinality;
        }


        public String[] getDefaultValue()
        {
            return defaultValues;
        }


        public String getDescription()
        {
            return description;
        }


        public String getID()
        {
            return id;
        }


        public String getName()
        {
            return name;
        }


        public String[] getOptionLabels()
        {
            return optionLabels;
        }


        public String[] getOptionValues()
        {
            return optionValues;
        }


        public int getType()
        {
            return type;
        }


        public String validate( String arg0 )
        {
            return null;
        }
    }
}