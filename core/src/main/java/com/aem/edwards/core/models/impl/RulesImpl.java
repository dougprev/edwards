package com.aem.edwards.core.models.impl;/**
 * Created by Douglas Prevelige on 3/31/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.aem.edwards.core.models.Rules;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Rules.class, ComponentExporter.class}, resourceType = RulesImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class RulesImpl implements Rules {

    public static final String RESOURCE_TYPE = "";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    private SlingHttpServletRequest request;

    private String ulid;
    private List<Rules.Rule> rules;

    @PostConstruct
    private void initialize() {

        log.info("initializing rules model");

        ResourceResolver resolver = request.getResourceResolver();
        Resource resThis = request.getResource();

        //find correct id of claims
        //travel up til find root or experience fragment
        Resource parent = resThis.getParent();
        while (parent != null) {
            String resType = parent.getResourceType();
            if (resType != null && (resType.contains("/delegxf") || parent.getName().equals("root"))) {
                break;
            }
            parent = parent.getParent();
        }
        if (parent!=null) {
            if (parent.hasChildren()) {
                ulid = findDisclaimer(parent);
            }
        }
        rules = new ArrayList<>();
        log.info("This Resource: " + resThis.getPath());
        if (resThis.hasChildren()) {
            Iterable<Resource> rit = resThis.getChildren();
            for (Resource child : rit) {
                log.info("child: " + child.getName());
                ValueMap vm = child.adaptTo(ValueMap.class);
                String assetPath = vm.get("assetPath","");
                String disclaimer = vm.get("disclaimer","");
                String upperItems = vm.get("upperItems","");
                String lowerItems = vm.get("lowerItems","");
                String footer = vm.get("footer","");
                RuleImpl rule = new RuleImpl(assetPath,disclaimer,upperItems,lowerItems,footer);
                rules.add(rule);
            }
        }


    }

    public class RuleImpl implements Rules.Rule {
        private String assetPath;
        private String disclaimer;
        private String upperItems;
        private String lowerItems;
        private String footer;

        public RuleImpl(String assetPath, String disclaimer, String upperItems, String lowerItems, String footer) {
            this.assetPath = assetPath;
            this.disclaimer = disclaimer;
            this.upperItems = upperItems;
            this.lowerItems = lowerItems;
            this.footer = footer;
        }

        @Override
        public String getAssetPath() {
            return assetPath;
        }

        @Override
        public String getDisclaimer() {
            return disclaimer;
        }

        @Override
        public String getUpperItems() {
            return upperItems;
        }

        @Override
        public String getLowerItems() {
            return lowerItems;
        }

        @Override
        public String getFooter() {
            return footer;
        }
    }


    private String findDisclaimer(Resource resource) {
        String retString = null;
        if (resource.hasChildren()) {
            Iterator<Resource> rit = resource.listChildren();
            if (rit != null) {
                while (rit.hasNext() && retString == null) {
                    Resource child = rit.next();
                    if (child.getResourceType().contains("poc/disclaimers")) {
                        retString = child.getPath().replace("/", "").replace(":","");
                        break;
                    } else {
                        if (child.hasChildren()) {
                            retString = findDisclaimer(child);
                        }
                    }
                }
            }
        }
        return retString;
    }

    @Override
    public List<Rule> getRules() {
        return rules;
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
