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
package de.ingrid.iplug.ckan.record.producer;

import de.ingrid.iplug.ckan.om.SourceRecord;
import de.ingrid.utils.ElasticDocument;

/**
 * Defines all aspects a record producer must implement. The record producer is
 * used to retrieve ONE record from the data source, based on a 
 * LuceneDocument for further processing.
 * 
 * @author joachim@wemove.com
 * 
 */
public interface IRecordProducer {


    /**
     * Get a record from the data source. How the record must be derived from
     * the fields of the lucene document.
     * 
     * @param doc
     * @param ds
     * @return
     */
    SourceRecord getRecord(ElasticDocument doc);

}
