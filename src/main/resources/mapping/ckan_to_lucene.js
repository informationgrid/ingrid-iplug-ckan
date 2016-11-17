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
if (javaVersion.indexOf( "1.8" ) === 0) {
    load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.ckan.om);
importPackage(Packages.de.ingrid.geo.utils.transformation);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.tools);
importPackage(Packages.java.lang);
importPackage(Packages.org.json.simple);

if (log.isDebugEnabled()) {
  log.debug("Mapping source record to lucene document: " + sourceRecord.toString());
}

if (!(sourceRecord instanceof JSONObject)) {
  throw new IllegalArgumentException("Record is no JSONObject!");
}

// ---------- MAP RECORD INTO INDEX ----------

// extract id of the record and read record(s) from database
// var objId = sourceRecord.get("id");

//IDX.add("id", objId);
//IDX.add("title", sourceRecord.get("title"));
//IDX.add("summary", sourceRecord.get("notes"));
//IDX.add("tags", sourceRecord.get("tags"));
IDX.addAll(sourceRecord);

// set/override subgroups for mcloud use
var extras = sourceRecord.get("extras");
if (extras) {
    extras.put("subgroups", subgroups);
    luceneDoc.remove("extras");
    luceneDoc.put("extras", extras);
}
