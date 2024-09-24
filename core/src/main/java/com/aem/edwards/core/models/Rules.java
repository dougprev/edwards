package com.aem.edwards.core.models;/**
 * Created by Douglas Prevelige on 3/31/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;


@ConsumerType
public interface Rules extends ComponentExporter {

    String getUlid();
    List<Rule> getRules();

    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    @ConsumerType
    interface Rule {
        String getAssetPath();
        String getDisclaimer();
        String getUpperItems();
        String getLowerItems();
        String getFooter();
    }

}
