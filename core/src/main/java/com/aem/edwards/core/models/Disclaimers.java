package com.aem.edwards.core.models;
/**
 * Created by Douglas Prevelige on 3/31/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.cq.export.json.ComponentExporter;
import org.osgi.annotation.versioning.ConsumerType;


@ConsumerType
public interface Disclaimers extends ComponentExporter {


    String getDisclaimers();
    String getUlid();

    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
