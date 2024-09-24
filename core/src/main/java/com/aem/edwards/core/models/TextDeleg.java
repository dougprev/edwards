package com.aem.edwards.core.models;

/**
 * Created by Douglas Prevelige on 4/24/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Text;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.aem.edwards.core.Utils;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
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

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Text.class, ComponentExporter.class},
        resourceType = {"metazine/components/poc/textdeleg"})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TextDeleg implements Text {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    @Via(type = ResourceSuperType.class)
    private Text text;

    @Self
    private SlingHttpServletRequest request;

    private String disclaimer;

    @PostConstruct
    private void initialize() {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        ValueMap vm = resource.adaptTo(ValueMap.class);
        disclaimer = Utils.getDisclaimer(vm);
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    @Override
    public String getText() {
        return text.getText();
    }

    @Override
    public boolean isRichText() {
        return text.isRichText();
    }

    @Nullable
    @Override
    public String getId() {
        return text.getId();
    }

    @Nullable
    @Override
    public ComponentData getData() {
        return text.getData();
    }

    @Nullable
    @Override
    public String getAppliedCssClasses() {
        return text.getAppliedCssClasses();
    }

    @NotNull
    @Override
    public String getExportedType() {
        return text.getExportedType();
    }
}
