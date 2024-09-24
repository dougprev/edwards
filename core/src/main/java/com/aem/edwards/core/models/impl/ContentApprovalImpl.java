package com.aem.edwards.core.models.impl;/**
 * Created by Douglas Prevelige on 8/29/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.aem.edwards.core.Utils;
import com.aem.edwards.core.models.ContentApproval;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static com.aem.edwards.core.Constants.VAL_APPROVED;
import static com.aem.edwards.core.Constants.VAL_NOTAPPROVED;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ContentApproval.class, ComponentExporter.class}, resourceType = ContentApprovalImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentApprovalImpl implements ContentApproval {

    public static final String RESOURCE_TYPE = "metazine/components/poc/hero";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String approvalStatus = VAL_NOTAPPROVED;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    private void initialize() {
        Resource resource = request.getResource();
        ValueMap vm = resource.adaptTo(ValueMap.class);
        if (Utils.isApproved(vm)) approvalStatus = VAL_APPROVED;
    }

    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
