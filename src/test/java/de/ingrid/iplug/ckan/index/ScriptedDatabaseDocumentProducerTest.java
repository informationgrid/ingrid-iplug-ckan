/*
 * **************************************************-
 * InGrid-iPlug CKAN
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.ckan.index;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.ingrid.admin.elasticsearch.StatusProvider;
import de.ingrid.iplug.ckan.CkanSearchPlug;
import de.ingrid.iplug.ckan.Configuration;
import de.ingrid.iplug.ckan.index.CkanDocumentProducer;
import de.ingrid.iplug.ckan.index.mapper.IRecordMapper;
import de.ingrid.iplug.ckan.index.mapper.ScriptedDocumentMapper;
import de.ingrid.iplug.ckan.index.producer.CkanRecordSetProducer;

public class ScriptedDatabaseDocumentProducerTest {
    
    @Mock StatusProvider statusProvider;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks( this );
        Configuration conf = new Configuration();
        
        conf.ckanBaseUrl = "https://ckan.govdata.de/api/";
        conf.ckanQueryFilter = "groups:transport_verkehr";
        conf.subgroups = new String[] {"waters"};
        
        CkanSearchPlug.conf = conf;
    }

    @Test
    public void mapCkanDocToElasticsearch() throws Exception {

        CkanRecordSetProducer p = new CkanRecordSetProducer();
        p.setStatusProvider( statusProvider );
        p.configure( null );

        ScriptedDocumentMapper m = new ScriptedDocumentMapper();
        Resource[] mappingScripts = {
            new FileSystemResource( "src/main/resources/mapping/ckan_to_lucene.js" )
        };
        m.setMappingScripts(mappingScripts);
        m.setCompile(false);

        List<IRecordMapper> mList = new ArrayList<IRecordMapper>();
        mList.add(m);
        
        CkanDocumentProducer dp = new CkanDocumentProducer();
        dp.setRecordSetProducer(p);
        dp.setRecordMapperList(mList);

        if (dp.hasNext()) {
            // only check first dataset
            if (dp.hasNext()) {
                Map<String, Object> doc = dp.next();
                assertNotNull(doc);
                
                Collection<String> keys = Arrays.asList( "id", "title", "notes", "tags" );
                assertTrue( doc.keySet().containsAll( keys ) );
            }
        } else {
            fail("No document produced");
        }
    }
    
}
