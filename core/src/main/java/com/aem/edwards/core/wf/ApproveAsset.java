package com.aem.edwards.core.wf;/**
 * Created by Douglas Prevelige on 4/1/2024.
 * Non-production code for POC purposes only.
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.edwards.core.Utils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class, property = {"process.label=Approve Asset", "process.description=Description Value"})
public class ApproveAsset implements WorkflowProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String TYPE_JCR_PATH = "JCR_PATH";
    private static final String TYPE_URL = "URL";

    public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
        WorkflowData workflowData = item.getWorkflowData();
        String path = Utils.getWorkflowPath(workflowData);
        log.info("*****  WF Step ApproveAsset *********");


        if (path.length() > 0) {
            ResourceResolver resourceResolver = wfsession.adaptTo(ResourceResolver.class);
            Resource resource = resourceResolver.getResource(path + "/jcr:content");
            Utils.setApproval(resource);
        }
    }


}
