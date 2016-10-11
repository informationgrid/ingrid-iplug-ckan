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
package de.ingrid.iplug.ckan.index.producer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import de.ingrid.admin.elasticsearch.StatusProvider;
import de.ingrid.iplug.ckan.CkanSearchPlug;
import de.ingrid.iplug.ckan.om.SourceRecord;
import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.PlugDescription;

/**
 * Takes care of selecting all source record Ids from a database. The SQL 
 * statement is configurable via Spring.
 * 
 * The database connection is configured via the PlugDescription.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class CkanRecordSetProducer implements
        IRecordSetProducer, IConfigurable {

    private static final int PAGING_SIZE = 1000;

    @Autowired
    private StatusProvider statusProvider;
    
    private String apiBaseUrl;
    private String queryFilter;
    
    private boolean hasOneAlready = false;
    
    Iterator<String> recordIdIterator = null;
    private int numRecords;

    final private static Log log = LogFactory
            .getLog(CkanRecordSetProducer.class);

    public CkanRecordSetProducer() {}

    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.iplug.dsc.index.IRecordProducer#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (recordIdIterator == null) {
            createRecordIdsFromSearch();
            statusProvider.addState( "FETCH", "Found " + numRecords + " records.");
        }
        if (recordIdIterator.hasNext() && !hasOneAlready) {
            return true;
        } else {
            reset();
            return false;
        }
    }
    
    /**
     * Closes the connection to the database and resets the iterator for the records. 
     * After a reset, the hasNext() function will start from the beginning again.
     */
    @Override
    public void reset() {
        recordIdIterator =  null;
        hasOneAlready = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.iplug.dsc.index.IRecordProducer#next()
     */
    @Override
    public SourceRecord next() {
        String id = recordIdIterator.next();
        SourceRecord sourceRecord = new SourceRecord( id );
        
        try {
            JSONObject json = requestJsonUrl(getApiBaseUrl() + "action/package_show?id=" + id);
            sourceRecord.put( "json", json.get( "result" ) );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // hasOneAlready = true;
        return sourceRecord;
    }

    @Override
    public void configure(PlugDescription plugDescription) {
        this.apiBaseUrl = CkanSearchPlug.conf.ckanBaseUrl;
        this.queryFilter = CkanSearchPlug.conf.ckanQueryFilter;
    }

    @SuppressWarnings("unchecked")
    private void createRecordIdsFromSearch() {
        try {
            
            JSONArray completeResult = new JSONArray();
            int page = 0;
            boolean hasMoreIDs = true;
            String searchUrl = getApiBaseUrl() + "search/dataset?rows=" + PAGING_SIZE + "&q=" + getQueryFilter();
            while (hasMoreIDs) {
                int offset = PAGING_SIZE * page;
                String offsetSearchUrl = searchUrl + "&offset=" + offset;
                if (log.isDebugEnabled()) {
                    log.debug("Requesting URL: " + offsetSearchUrl);
                }
                
                
                JSONObject parse = requestJsonUrl( offsetSearchUrl );
                Long count = (Long) parse.get( "count" );
                this.numRecords = count.intValue();
                JSONArray results = (JSONArray) parse.get( "results" );
                completeResult.addAll( results );
                if (count - offset - PAGING_SIZE < 0) {
                    hasMoreIDs = false;
                }
                page++;
            }
            
            this.recordIdIterator = completeResult.iterator();
           
        } catch (Exception e) {
            log.error("Error creating record ids.", e);
        }
    }
    
    private JSONObject requestJsonUrl(String url) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod( url );
        getMethod.addRequestHeader( "User-Agent", "Request-Promise" );
        client.executeMethod( getMethod );
        String json = getMethod.getResponseBodyAsString();
        log.debug( "response: " + json );
        return (JSONObject) new JSONParser().parse( json );
    }
    

    @Override
    public int getDocCount() {
        return numRecords;
    }

    public void setStatusProvider(StatusProvider statusProvider) {
        this.statusProvider = statusProvider;
    }

    public String getApiBaseUrl() {
        if (!apiBaseUrl.endsWith( "/" )) {
            return apiBaseUrl + "/";
        }
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getQueryFilter() throws UnsupportedEncodingException {
        return URLEncoder.encode( queryFilter, "UTF8" );
    }

    public void setQueryFilter(String queryFilter) {
        this.queryFilter = queryFilter;
    }

}
