package com.aem.edwards.core.models;

/**
 * Created by Douglas Prevelige on 3/28/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.aem.edwards.core.Utils;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.ArrayList;
import java.util.List;

import static com.aem.edwards.core.Constants.VAL_APPROVED;
import static com.aem.edwards.core.Constants.VAL_NOTAPPROVED;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class},
        resourceType = {"metazine/components/poc/imagedeleg"})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageDeleg implements Image {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    @Via(type = ResourceSuperType.class)
    private Image imgae;

    @Self
    private SlingHttpServletRequest request;

    private String approvalStatus = VAL_NOTAPPROVED;
    private String disclaimer;
    private String ruleDescriptions = "";
    private String assetPath;
    private String assetTitle;
    private String titleLocation;
    private List<String> directions;
    private List<String> includes;
    private List<String> excludes;

    @PostConstruct
    private void initialize() {

        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        ValueMap vm = resource.adaptTo(ValueMap.class);
        String imagePath = Utils.getCompRefPath(vm);
        //log.info("imagePath: " + imagePath);
        //log.info("approvalStatus: " + approvalStatus);
        if (imagePath != null && imagePath.length() > 0) {
            assetPath = imagePath;
            Resource imageRes = resolver.getResource(imagePath + "/jcr:content");
            if (imageRes != null) {
                ValueMap imageVM = imageRes.adaptTo(ValueMap.class);
                if (Utils.isApproved(imageVM)) approvalStatus = VAL_APPROVED;
                disclaimer = Utils.getDisclaimer(imageVM);
                if (disclaimer != null && disclaimer.length() > 0) {
                    ruleDescriptions += "1-";
                } else {
                    ruleDescriptions += "0-";
                }

                ruleDescriptions += "0";
                directions = Utils.getListFromArrayProp(imageVM,"directions");
                includes = Utils.getListFromArrayProp(imageVM,"includes");
                excludes = Utils.getListFromArrayProp(imageVM,"excludes");
                assetTitle = Utils.getAssetTitle(resolver,assetPath);
                titleLocation = vm.get("titleLocation","bottom");
            }
        }

    }

    public String getTitleLocation() {
        return titleLocation;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getAssetTitle() {
        return assetTitle;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public List<String> getDirections() {
        return directions;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getRuleDescriptions() {
        return ruleDescriptions;
    }

    public String getSrc() {
        return imgae.getSrc();
    }

    public String getAlt() {
        return imgae.getAlt();
    }

    public String getTitle() {
        return imgae.getTitle();
    }

    public String getUuid() {
        return imgae.getUuid();
    }

    public String getLink() {
        return imgae.getLink();
    }

    public boolean displayPopupTitle() {
        return imgae.displayPopupTitle();
    }

    @JsonIgnore
    public String getFileReference() {
        return imgae.getFileReference();
    }

    @Deprecated
    @JsonIgnore
    public String getJson() {
        return imgae.getJson();
    }

    @NotNull
    public int[] getWidths() {
        return imgae.getWidths();
    }

    public String getSrcUriTemplate() {
        return imgae.getSrcUriTemplate();
    }

    public boolean isLazyEnabled() {
        return imgae.isLazyEnabled();
    }

    public List<ImageArea> getAreas() {
        //return imgae.getAreas();
        return new ArrayList<>();
    }

    @NotNull
    public String getExportedType() {
        return imgae.getExportedType();
    }

    public boolean isDecorative() {
        return imgae.isDecorative();
    }
}
