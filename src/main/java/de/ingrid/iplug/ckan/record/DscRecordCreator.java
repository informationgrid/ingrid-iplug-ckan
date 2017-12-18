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
package de.ingrid.iplug.ckan.record;

import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ingrid.iplug.ckan.record.mapper.IIdfMapper;
import de.ingrid.iplug.ckan.record.producer.IRecordProducer;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.dsc.Record;

/**
 * This class manages to get a {@link Record} from a data source based on data
 * from a lucene document.
 * <p/>
 * The Class can be configured with a data source specific record producer
 * implementing the {@link IRecordProducer} interface and a list of IDF (InGrid
 * Detaildata Format) mapper, implementing the {@link IIdfMapper} interface.
 * <p/>
 * The IDF data can optionally be compressed using a {@link GZIPOutputStream} by
 * setting the property {@link compressed} to true.
 * 
 * @author joachim@wemove.com
 * 
 */
// @Service
public class DscRecordCreator {

    protected static final Logger log = LogManager.getLogger( DscRecordCreator.class );

    // @Autowired
    private IRecordProducer recordProducer = null;

    // @Autowired
    private List<IIdfMapper> record2IdfMapperList = null;

    private boolean compressed = false;

    /**
     * Retrieves a record with an IDF document in property "data". The property
     * "compressed" is set to "true" if the IDF document is compressed, "false"
     * if the IDF document is not compressed.
     * 
     * @param idxDoc
     * @return
     * @throws Exception
     */
    public Record getRecord(ElasticDocument idxDoc) throws Exception {
//        String data;

//        if (idxDoc.containsKey( IdfProducerDocumentMapper.DOCUMENT_FIELD_IDF )) {
//            if (log.isDebugEnabled()) {
//                log.debug( "Use content of index field 'idf'." );
//            }
//            data = (String) idxDoc.get( IdfProducerDocumentMapper.DOCUMENT_FIELD_IDF );
//        } else {
//
//
//        }
        Record record = new Record();

        return record;
    }

    public IRecordProducer getRecordProducer() {
        return recordProducer;
    }

    public void setRecordProducer(IRecordProducer recordProducer) {
        this.recordProducer = recordProducer;
    }

    public List<IIdfMapper> getRecord2IdfMapperList() {
        return record2IdfMapperList;
    }

    public void setRecord2IdfMapperList(List<IIdfMapper> record2IdfMapperList) {
        this.record2IdfMapperList = record2IdfMapperList;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

}
