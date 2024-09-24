package com.aem.edwards.core.models;/**
 * Created by Douglas Prevelige on 3/14/2024.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;


@ConsumerType
public interface IVANav extends ComponentExporter {

    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
    List<NavItem> getNavItems();
    String getLocation();
    @ConsumerType
    interface NavItem {
        String getLink();
        String getTitle();
        boolean isHome();
        boolean isCurrent();

    }
}
