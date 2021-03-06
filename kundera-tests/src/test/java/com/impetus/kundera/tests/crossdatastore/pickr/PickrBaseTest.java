/*******************************************************************************
 * * Copyright 2012 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.kundera.tests.crossdatastore.pickr;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.cassandra.locator.SimpleStrategy;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.SchemaDisagreementException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.metadata.model.KunderaMetadata;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.impetus.kundera.tests.cli.CassandraCli;
import com.impetus.kundera.tests.crossdatastore.pickr.dao.Pickr;
import com.impetus.kundera.tests.crossdatastore.pickr.dao.PickrImpl;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * @author amresh.singh
 */
public abstract class PickrBaseTest
{
    protected static final boolean RUN_IN_EMBEDDED_MODE = true;

    protected static final boolean AUTO_MANAGE_SCHEMA = true;

    protected Pickr pickr;

    protected int photographerId;

    protected String pu = "piccandra,picmysql,picongo";

    private static Log log = LogFactory.getLog(PickrBaseTest.class);

    protected void setUp() throws Exception
    {
        if (RUN_IN_EMBEDDED_MODE)
        {
            startServer();
        }
        photographerId = 1;
        pickr = new PickrImpl(pu);
    }

    public void executeTests()
    {
        addPhotographer();
        getPhotographer();
        updatePhotographer();
        getAllPhotographers();
        deletePhotographer();
    }

    protected void tearDown() throws Exception
    {
        pickr.close();
        if (RUN_IN_EMBEDDED_MODE)
        {
            stopServer();
        }

        if (AUTO_MANAGE_SCHEMA)
        {
            CassandraCli.dropKeySpace("Pickr");
            truncateMongo();
        }
    }

    protected void addKeyspace(KsDef ksDef, List<CfDef> cfDefs) throws InvalidRequestException,
            SchemaDisagreementException, TException
    {
        ksDef = new KsDef("Pickr", SimpleStrategy.class.getSimpleName(), cfDefs);
        // Set replication factor
        if (ksDef.strategy_options == null)
        {
            ksDef.strategy_options = new LinkedHashMap<String, String>();
        }
        // Set replication factor, the value MUST be an integer
        ksDef.strategy_options.put("replication_factor", "1");
        CassandraCli.client.system_add_keyspace(ksDef);
    }

    /**
     * 
     */
    private void truncateMongo()
    {
        PersistenceUnitMetadata pUnitMetadata = KunderaMetadata.INSTANCE.getApplicationMetadata()
                .getPersistenceUnitMetadata("picongo");
        String host = pUnitMetadata != null ? pUnitMetadata.getProperty(PersistenceProperties.KUNDERA_NODES) : null;
        String port = pUnitMetadata != null ? pUnitMetadata.getProperty(PersistenceProperties.KUNDERA_PORT) : null;
        try
        {
            Mongo m = null;
            if (host != null && port != null)
            {
                m = new Mongo(host, Integer.parseInt(port));
                m.getDB(pUnitMetadata.getProperty(PersistenceProperties.KUNDERA_KEYSPACE)).dropDatabase();
            }
        }
        catch (NumberFormatException e)
        {
            log.error(e.getMessage());
        }
        catch (UnknownHostException e)
        {
            log.error(e.getMessage());
        }
        catch (MongoException e)
        {
            log.error(e.getMessage());
        }
    }

    protected abstract void addPhotographer();

    protected abstract void updatePhotographer();

    protected abstract void getPhotographer();

    protected abstract void getAllPhotographers();

    protected abstract void deletePhotographer();

    protected abstract void startServer() throws IOException, TException, InvalidRequestException,
            UnavailableException, TimedOutException, SchemaDisagreementException;

    protected abstract void stopServer();

}
