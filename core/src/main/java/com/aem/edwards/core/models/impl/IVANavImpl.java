package com.aem.edwards.core.models.impl;
/**
 * Created by Douglas Prevelige on 3/14/2024.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.aem.edwards.core.models.IVANav;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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

@Model(adaptables = SlingHttpServletRequest.class, adapters = {IVANav.class, ComponentExporter.class}, resourceType = IVANavImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class IVANavImpl implements IVANav {

    public static final String RESOURCE_TYPE = "iva/components/navigation";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Self
    private SlingHttpServletRequest request;

    private List<NavItem> navItems;
    private String location;


    @PostConstruct
    private void initialize() {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        //log.info("pathinfo " + request.getPathInfo());
        ValueMap vm = resource.getValueMap();
        location = vm.get("location", "1");
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);
        //log.info("Current Page: " + currentPage.getPath());
        if (currentPage.getPath().startsWith("/conf/")) {
            //log.info("in conf");
            String referer = request.getPathInfo();
            if (referer != null) {
                referer = referer.substring(referer.indexOf("/content"));
                if (referer.endsWith(".html")) referer = referer.substring(0, referer.length() - 5);
                //log.info("Referer: " + referer);
                currentPage = pageManager.getPage(referer);
                if (currentPage == null) {
                    //log.info("currentPage is null");
                    currentPage = pageManager.getContainingPage(resource);
                }
            }
        }
        //log.info("Current Page: " + currentPage.getPath());

        Page parentPage = currentPage.getParent();
        Page rootPage = getRoot(parentPage);
        int level = currentPage.getDepth() - rootPage.getDepth();
        //log.info("Level: " + level);

        if (level == 1) {
            if (location.equals("bottom")) {
                navItems = getNavList(parentPage, currentPage.getPath());
            } else if (location.equals("middle")) {
                navItems = getNavList(currentPage, currentPage.getPath());
            } else {
                navItems = new ArrayList<>();
            }
        } else if (level == 2) {
            if (location.equals("bottom")) {
                navItems = getNavList(parentPage.getParent(), parentPage.getPath());
            } else if (location.equals("middle")) {
                navItems = getNavList(currentPage.getParent(), currentPage.getPath());
            } else {
                navItems = getNavList(currentPage, currentPage.getPath());
            }
        } else if (level == 3) {
            if (location.equals("bottom")) {
                navItems = getNavList(parentPage.getParent().getParent(), parentPage.getParent().getPath());
            } else if (location.equals("middle")) {
                navItems = getNavList(currentPage.getParent().getParent(), parentPage.getPath());
            } else {
                navItems = getNavList(parentPage, currentPage.getPath());
            }
        } else {
            navItems = new ArrayList<>();
        }
    }

    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    @Override
    public List<NavItem> getNavItems() {
        return navItems;
    }

    @Override
    public String getLocation() {
        return location;
    }

    class NavItemImpl implements NavItem {
        private String link;
        private String title;
        private boolean home;
        private boolean current;

        NavItemImpl(String link, String title, boolean home, boolean current) {
            this.link = link;
            this.title = title;
            this.home = home;
            this.current = current;
        }

        @Override
        public String getLink() {
            return link;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public boolean isHome() {
            return home;
        }

        @Override
        public boolean isCurrent() {
            return current;
        }
    }
    private Page getRoot(Page page) {
        Page temp = page;
        boolean found =false;
        while(temp != null && !found) {
            ValueMap vm = temp.getProperties();
            if (vm.containsKey("navRoot")) {
                found = true;
            } else {
                temp = temp.getParent();
            }
        }
        if (!found) {
            return page;
        }
        return temp != null ? temp : page;
    }
    private List<NavItem> getNavList(Page page, String currentPagePath) {
        List<NavItem> navItems = new ArrayList<>();
        Iterator<Page> pit = page.listChildren();
        if (pit != null) {
            while (pit.hasNext()) {
                Page childPage = pit.next();
                String title = childPage.getNavigationTitle();
                if (title == null || title.length() == 0) title = childPage.getPageTitle();
                if (title == null || title.length() == 0) title = childPage.getTitle();
                if (title == null || title.length() == 0) title = childPage.getName();

                boolean home = childPage.getName().startsWith("home");
                boolean current = currentPagePath.equals(childPage.getPath());
                //log.info("Location: " + location + "  |  Title: " + title + "  |  Home: " + home + "  |  Current: " + current);
                String childLink = getChildLink(childPage);
                NavItem navItem = new NavItemImpl(
                        childLink,
                        title,
                        home,
                        current);
                navItems.add(navItem);
            }
        }
        return navItems;
    }
    private String getChildLink(Page page) {
        Iterator<Page> pit = page.listChildren();
        if (pit != null && pit.hasNext()) {
            Page childPage = pit.next();
            return getChildLink(childPage);
        } else {
            return page.getPath() + ".html";
        }
    }
}
