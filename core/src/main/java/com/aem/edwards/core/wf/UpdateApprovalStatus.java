package com.aem.edwards.core.wf;

/**
 * Created by Douglas Prevelige on 6/8/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.edwards.core.Utils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


@Component(service = WorkflowProcess.class, property = {"process.label=UpdateApprovalStatus", "process.description=Update Approval date time property lastApproved"})
public class UpdateApprovalStatus implements WorkflowProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
        WorkflowData workflowData = item.getWorkflowData();
        String path = Utils.getWorkflowPath(workflowData);
        log.info("*****  WF Step UpdateApprovalStatus *********");

        if (path.length() > 0) {
            Session session = wfsession.adaptTo(Session.class);
            ResourceResolver resourceResolver = wfsession.adaptTo(ResourceResolver.class);
            Resource resource = resourceResolver.getResource(path + "/jcr:content");
            if (resource != null) {
                try {
                    Utils.setApproval(resource);
                    Node node = resource.adaptTo(Node.class);
                    if (node.getPrimaryNodeType().isNodeType("cq:PageContent")) {
                        Utils.approveChildren(resource);
                    }
                    resourceResolver.commit();
                } catch (PersistenceException e) {
                    log.error("Error!", e);
                } catch (RepositoryException e) {
                    log.error("Error!", e);
                }
            } else {
                log.info("Could not set approval for " + path + "/jcr:content");
            }
        }
    }


}

