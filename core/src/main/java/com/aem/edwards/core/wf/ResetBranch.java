package com.aem.edwards.core.wf;/**
 * Created by Douglas Prevelige on 3/24/2024.
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
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(service = WorkflowProcess.class, property = {"process.label=Rest Branch Approval", "process.description=Description Value"})
public class ResetBranch implements WorkflowProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String TYPE_JCR_PATH = "JCR_PATH";
    private static final String TYPE_URL = "URL";

    public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
        WorkflowData workflowData = item.getWorkflowData();
        String path = Utils.getWorkflowPath(workflowData);
        log.info("*****  WF Step ResetBranch *********");


        if (path.length() > 0) {
            Session session = wfsession.adaptTo(Session.class);
            ResourceResolver resourceResolver = wfsession.adaptTo(ResourceResolver.class);
            resetPath(path, resourceResolver);
        }
    }
    private void resetPath(String path, ResourceResolver resolver) {
        Resource resource = resolver.getResource(path + "/jcr:content");
        try {
            Utils.resetApproval(resource);
            Node node = resource.adaptTo(Node.class);
            if (node.getPrimaryNodeType().isNodeType("cq:PageContent")) {
                Utils.resetChildrenApproval(resource);
            }
            resolver.commit();
        } catch (RepositoryException | PersistenceException e) {
            log.error("Error!",e);
        }
        Resource pageResource = resource.getParent();
        if (pageResource.hasChildren()) {
            for (Resource child : pageResource.getChildren()) {
                ValueMap vm = child.adaptTo(ValueMap.class);
                if (vm.get("jcr:primaryType", "").equals("cq:Page")) {
                    resetPath(child.getPath(), resolver);
                }
            }
        }
    }

}
