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
/**
 * 
 */
package de.ingrid.iplug.ckan.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.admin.elasticsearch.IndexInfo;
import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.iplug.ckan.index.mapper.IRecordMapper;
import de.ingrid.iplug.ckan.index.producer.IRecordSetProducer;
import de.ingrid.iplug.ckan.om.SourceRecord;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;

/**
 * Implements de.ingrid.admin.object.IDocumentProducer from the base webapp 
 * interface. It is triggered by the base webapp during indexing of a 
 * datasource. The DocumentProducer iterates over all records of the 
 * datasource and maps each record into a LuceneDocument.
 * This producer is configured with a IRecordSetProducer and one or more
 * IRecordMapper. 
 * @author joachim
 */
//@Service
public class CkanDocumentProducer implements IDocumentProducer {

    //@Autowired
    private IRecordSetProducer recordSetProducer = null;

    //@Autowired
    private List<IRecordMapper> recordMapperList = null;
    
    private IndexInfo indexInfo = null;

    final private static Log log = LogFactory.getLog(CkanDocumentProducer.class);
    
    public CkanDocumentProducer() {
        log.info("CkanDocumentProducer started.");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.admin.object.IDocumentProducer#hasNext()
     */
    @Override
    public boolean hasNext() {
        try {
            return recordSetProducer.hasNext();
        } catch (Exception e) {
            log.error("Error obtaining information about a next record. Skip all records.", e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.admin.object.IDocumentProducer#next()
     */
    @Override
    public ElasticDocument next() {
        ElasticDocument doc = new ElasticDocument();
        try {
            SourceRecord record = recordSetProducer.next();
            for (IRecordMapper mapper : recordMapperList) {
                long start = 0;
                if (log.isDebugEnabled()) {
                    start = System.currentTimeMillis();
                }
                mapper.map(record, doc);
                if (log.isDebugEnabled()) {
                    log.debug("Mapping of source record with " + mapper + " took: " + (System.currentTimeMillis() - start) + " ms.");
                }
            }
            return doc;
        } catch (Exception e) {
            log.error("Error obtaining next record.", e);
            return null;
        }
    }
    
    /**
     * Get a Elastic Search document by its given ID, which can be found
     * under the given field
     * @param id is the ID of the document
     * @param field is the column of the database where the field is stored
     * @return an Elastic Search document with the given ID
     */
    public ElasticDocument getById(String id, String field) {
        ElasticDocument doc = new ElasticDocument();
        // iterate through all docs to make sure connection is closed next time
        try {
            while (recordSetProducer.hasNext()) {
                SourceRecord next = recordSetProducer.next();
                if (id.equals( next.get( field ) )) {
                    for (IRecordMapper mapper : recordMapperList) {
                        long start = 0;
                        if (log.isDebugEnabled()) {
                            start = System.currentTimeMillis();
                        }
                        mapper.map(next, doc);
                        if (log.isDebugEnabled()) {
                            log.debug("Mapping of source record with " + mapper + " took: " + (System.currentTimeMillis() - start) + " ms.");
                        }
                    }
                    recordSetProducer.reset();
                    break; 
                }
                
            }
        } catch (Exception e) {
            log.error( "Exception occurred during getting document by ID and mapping it to lucene: " + e.getMessage() );
            e.printStackTrace();
            doc = null;
        }
        return doc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.utils.IConfigurable#configure(de.ingrid.utils.PlugDescription)
     */
    @Override
    public void configure(PlugDescription arg0) {
        log.info("CkanDocumentProducer: configuring...");
    }

    @Override
    public Integer getDocumentCount() {
        try {
            if (recordSetProducer.hasNext()) {
                return recordSetProducer.getDocCount();
            }
        } catch (Exception e) {}
        return null;
    }
    
    public IRecordSetProducer getRecordSetProducer() {
        return recordSetProducer;
    }

    public void setRecordSetProducer(IRecordSetProducer recordProducer) {
        this.recordSetProducer = recordProducer;
    }

    public List<IRecordMapper> getRecordMapperList() {
        return recordMapperList;
    }

    public void setRecordMapperList(List<IRecordMapper> recordMapperList) {
        this.recordMapperList = recordMapperList;
    }

    @Override
    public IndexInfo getIndexInfo() {
        return this.indexInfo;
    }

    public void setIndexInfo(IndexInfo indexInfo) {
        this.indexInfo = indexInfo;
    }

}
