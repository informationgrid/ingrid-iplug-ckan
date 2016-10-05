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
package de.ingrid.iplug.ckan.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.controller.AbstractController;
import de.ingrid.iplug.ckan.CkanSearchPlug;

/**
 * Control the database parameter page.
 * 
 * @author joachim@wemove.com
 * 
 */
@Controller
@SessionAttributes("plugDescription")
public class CkanSettingsController extends AbstractController {

    public CkanSettingsController() {}

    @RequestMapping(value = { "/iplug-pages/welcome.html", "/iplug-pages/ckanSettings.html" }, method = RequestMethod.GET)
    public String getParameters(final ModelMap modelMap) {

        CkanConfig ckanConfig = new CkanConfig();
        ckanConfig.setBaseUrl( CkanSearchPlug.conf.ckanBaseUrl );
        ckanConfig.setQueryFilter( CkanSearchPlug.conf.ckanQueryFilter );
        // write object into session
        modelMap.addAttribute( "ckanConfig", ckanConfig );
        return AdminViews.CKAN_SETTINGS;
    }

    @RequestMapping(value = "/iplug-pages/ckanSettings.html", method = RequestMethod.POST)
    public String post(
            @ModelAttribute("ckanConfig") final CkanConfig ckanConfig) {

        CkanSearchPlug.conf.ckanBaseUrl = ckanConfig.getBaseUrl();
        CkanSearchPlug.conf.ckanQueryFilter = ckanConfig.getQueryFilter();

        return AdminViews.SAVE;
    }

}
