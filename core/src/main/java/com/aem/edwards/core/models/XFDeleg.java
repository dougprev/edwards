package com.aem.edwards.core.models;

/**
 * Created by Douglas Prevelige on 3/28/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.aem.edwards.core.Utils;
import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Calendar;

import static com.aem.edwards.core.Constants.*;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ExperienceFragment.class, ComponentExporter.class}, resourceType = XFDeleg.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class XFDeleg implements ExperienceFragment {
    public static final String RESOURCE_TYPE = "metazine/components/poc/xfdeleg";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    @Via(type = ResourceSuperType.class)
    private ExperienceFragment experienceFragment;

    @Self
    private SlingHttpServletRequest request;

    private String approvalStatus = VAL_NOTAPPROVED;
    private String disclaimer = "";

    @PostConstruct
    private void initialize() {

        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        ValueMap vm = resource.adaptTo(ValueMap.class);
        String xfPath = Utils.getCompRefPath(vm);
        log.info("xfPath: " + xfPath);
        if (xfPath != null && xfPath.length() > 0) {
            Resource xfRes = resolver.getResource(xfPath + "/jcr:content");
            if (xfRes != null) {
                ValueMap xfVM = xfRes.adaptTo(ValueMap.class);
                Calendar lastApproved = xfVM.get(PROP_LASTAPPROVED,Calendar.class);
                if (Utils.isApproved(xfVM)) approvalStatus = VAL_APPROVED;
                disclaimer = Utils.getDisclaimer(xfVM);
            } else {
                log.info("xfRes is null");
            }

        }

    }


    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getDisclaimer() {
        return disclaimer ;
    }

    @Override
    public String getLocalizedFragmentVariationPath() {
        return experienceFragment.getLocalizedFragmentVariationPath();
    }

    @Override
    public String getName() {
        return experienceFragment.getName();
    }

    @Override
    @NotNull
    public String getExportedType() {
        return request.getResource().getResourceType(); //experienceFragment.getExportedType();
    }
}
