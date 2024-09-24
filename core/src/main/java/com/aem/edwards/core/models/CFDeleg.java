package com.aem.edwards.core.models;

/**
 * Created by Douglas Prevelige on 3/28/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.aem.edwards.core.Utils;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static com.aem.edwards.core.Constants.VAL_APPROVED;
import static com.aem.edwards.core.Constants.VAL_NOTAPPROVED;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ContentFragment.class, DAMContentFragment.class, ComponentExporter.class},
        resourceType = {"metazine/components/poc/cfdeleg","iva/components/contentfragment"})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CFDeleg implements ContentFragment, DAMContentFragment {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    @Via(type = ResourceSuperType.class)
    private ContentFragment contentFragment;

    @Self
    private SlingHttpServletRequest request;

    private String approvalStatus = VAL_NOTAPPROVED;
    private String disclaimer;
    private boolean footerRequired;
    private String ruleDescriptions = "";
    private String assetPath;
    private String assetTitle;
    private Map<String,String> itemMap;
    private Map<String,String> labelMap;
    private List<String> directions;
    private List<String> includes;
    private List<String> excludes;

    @PostConstruct
    private void initialize() {
        itemMap = new HashMap<>();
        labelMap = new HashMap<>();
        ResourceResolver resolver = request.getResourceResolver();

        Resource resource = request.getResource();
        log.info("resource is null: " + (resource ==null));

        ValueMap vm = resource.adaptTo(ValueMap.class);
        String cfPath = Utils.getCompRefPath(vm);
        footerRequired = vm.get("footerRequired",false);
        log.info("cfPath: " + cfPath);
        if (cfPath != null && cfPath.length() > 0) {
            assetPath = cfPath;
            assetTitle = Utils.getAssetTitle(resolver,assetPath);
            Resource cfRes = resolver.getResource(cfPath + "/jcr:content");
            if (cfRes != null) {
                ValueMap cfVM = cfRes.adaptTo(ValueMap.class);
                if (Utils.isApproved(cfVM)) approvalStatus = VAL_APPROVED;
                disclaimer = Utils.getDisclaimer(cfVM);
                if (disclaimer != null && disclaimer.length() > 0) {
                    ruleDescriptions += "1-";
                } else {
                    ruleDescriptions += "0-";
                }
                if (footerRequired) {
                    ruleDescriptions += "1";
                } else {
                    ruleDescriptions += "0";
                }

                directions = Utils.getListFromArrayProp(cfVM,"directions");
                includes = Utils.getListFromArrayProp(cfVM,"includes");
                excludes = Utils.getListFromArrayProp(cfVM,"excludes");

            }

        }
    }

    public String getAssetPath() {
        return assetPath;
    }

    public boolean isFooterRequired() {
        return footerRequired;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getRuleDescriptions() {
        return ruleDescriptions;
    }

    public Map<String,String> getItemMap() {
        List<DAMContentElement> els = contentFragment.getElements();
        for (DAMContentElement el : els) {
            labelMap.put(el.getName(),el.getTitle());
            if (el.getDataType().equals("calendar")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = el.getValue(Calendar.class).getTime();
                String formatted = sdf.format(date);
                itemMap.put(el.getName(), formatted);
            } else {
                String elValue = el.getValue() != null ? el.getValue().toString() : "";
                itemMap.put(el.getName(), elValue);
            }
        }
        return itemMap;
    }

    public Map<String, String> getLabelMap() {
        itemMap = new HashMap<>();
        labelMap = new HashMap<>();
        List<DAMContentElement> els = contentFragment.getElements();
        for (DAMContentElement el : els) {
            labelMap.put(el.getName(),el.getTitle());
            if (el.getDataType().equals("calendar")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = el.getValue(Calendar.class).getTime();
                String formatted = sdf.format(date);
                itemMap.put(el.getName(), formatted);
            } else {
                String elValue = el.getValue() != null ? el.getValue().toString() : "";
                itemMap.put(el.getName(), elValue);
            }
        }
        return labelMap;
    }

    public String getAssetTitle() {
        return assetTitle;
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

    @Override
    @JsonIgnore
    @NotNull
    public String getGridResourceType() {
        return contentFragment.getGridResourceType();
    }

    @Override
    @NotNull
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return contentFragment.getExportedItems();
    }

    @Override
    @NotNull
    public String[] getExportedItemsOrder() {
        return contentFragment.getExportedItemsOrder();
    }

    @Override
    @NotNull
    public String getExportedType() {
        return contentFragment.getExportedType();
    }

    @Override
    @Nullable
    public String[] getParagraphs() {
        return contentFragment.getParagraphs();
    }

    @Override
    @Nullable
    public String getTitle() {
        return contentFragment.getTitle();
    }

    @Override
    @JsonIgnore
    @NotNull
    public String getName() {
        return contentFragment.getName();
    }

    @Override
    @Nullable
    public String getDescription() {
        return contentFragment.getDescription();
    }

    @Override
    @JsonProperty("model")
    @Nullable
    public String getType() {
        return contentFragment.getType();
    }

    @Override
    @JsonIgnore
    @Nullable
    public List<DAMContentElement> getElements() {
        return contentFragment.getElements();
    }

    @Override
    @JsonProperty("elements")
    @NotNull
    public Map<String, DAMContentElement> getExportedElements() {
        return contentFragment.getExportedElements();
    }

    @Override
    @JsonProperty("elementsOrder")
    @NotNull
    public String[] getExportedElementsOrder() {
        return contentFragment.getExportedElementsOrder();
    }

    @Override
    @JsonIgnore
    @Nullable
    public List<Resource> getAssociatedContent() {
        return contentFragment.getAssociatedContent();
    }

    @Override
    @JsonIgnore
    @NotNull
    public String getEditorJSON() {
        return contentFragment.getEditorJSON();
    }


}
