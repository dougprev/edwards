package com.aem.edwards.core.models.impl;
/**
 * Created by Douglas Prevelige on 3/31/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.aem.edwards.core.models.Disclaimers;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Iterator;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Disclaimers.class, ComponentExporter.class}, resourceType = DisclaimersImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DisclaimersImpl implements Disclaimers {

    public static final String RESOURCE_TYPE = "metazine/components/poc/disclaimers";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    private SlingHttpServletRequest request;

    private String disclaimers;
    private String ulid;

    @PostConstruct
    private void initialize() {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resThis = request.getResource();
        ulid = resThis.getPath().replace("/","").replace(":","");
        Resource parentRes = resThis.getParent();
        while (parentRes != null) {
            String parentName = parentRes.getName();
            if (parentName.equalsIgnoreCase("root")) {
                disclaimers = traverseNodes(parentRes, "");
                break;
            }
            parentRes = parentRes.getParent();
        }
    }

    String traverseNodes(Resource resource, String inputString) {
        String returnString = inputString;
        ValueMap vm = resource.adaptTo(ValueMap.class);
        String resType = vm.get("sling:resourceType","");
        if (resType.equalsIgnoreCase("pfizer/components/poc/delegxf")) {
            String resPath = vm.get("fragmentVariationPath","");
            returnString = returnString + "<li>" + resPath + "</li>";
        } else if (resType.equalsIgnoreCase("pfizer/components/poc/delegcf")) {
            String resPath = vm.get("fragmentPath","");
            returnString = returnString + "<li>" + resPath + "</li>";
        } else if (resType.equalsIgnoreCase("pfizer/components/poc/delegimage")) {
            String resPath = vm.get("fileReference","");
            returnString = returnString + "<li>" + resPath + "</li>";
        } else if (resource.hasChildren()) {
            Iterator<Resource> rit = resource.listChildren();
            while (rit.hasNext()) {
                Resource child = rit.next();
                returnString = traverseNodes(child, returnString);
            }
        }
        return returnString;
    }

    @Override
    public String getDisclaimers() {
        return disclaimers;
    }

    @Override
    public String getUlid() {
        return ulid;
    }

    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}

