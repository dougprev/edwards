package com.aem.edwards.core;

/**
 * Created by Douglas Prevelige on 5/22/2023.
 * Non-production code for POC purposes only.
 */

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;


public class Props {
    private final static String ASSET_METADATA = "jcr:content/metadata";
    private final static String PAGE_METADATA = "jcr:content";
    public final static String PROP_STATUS = "pocstatus";
    public final static String PROP_PROJID = "projID";
    public final static String PROP_REQID = "reqID";
    public final static String PROP_PDF = "pdf";
    public final static String PROP_ASSETPATH = "assetPath";
    public final static String VALUE_PENDING = "Pending";
    public final static String VALUE_APPROVED = "Approved";
    public final static String VALUE_REJECTED = "Rejected";

    public static void updateAssetPath(String path, Resource resource) {
        updateProp(PAGE_METADATA,PROP_ASSETPATH,path,resource);
    }
    public static String getAssetPath(Resource resource) {
        return getPropValue(PAGE_METADATA,PROP_ASSETPATH,resource);
    }
    public static void  updateAssetStatus(String status, Resource resource) {
        updateProp(ASSET_METADATA,PROP_STATUS,status,resource);
    }
    public static void  updatePageStatus(String status, Resource resource) {
        updateProp(PAGE_METADATA,PROP_STATUS,status,resource);
    }
    public static String getAssetStatus(Resource resource) {
        return getPropValue(ASSET_METADATA, PROP_STATUS, resource);
    }
    public static String getPageStatus(Resource resource) {
        return getPropValue(PAGE_METADATA, PROP_STATUS, resource);
    }
    public static void addProjIDAsset(String projID, Resource resource) {
        updateProp(ASSET_METADATA,PROP_PROJID,projID,resource);
        updateProp(PAGE_METADATA,PROP_PROJID,projID,resource.getParent());
    }
    public static void addProjIDPage(String projID, Resource resource) {
        updateProp(PAGE_METADATA,PROP_PROJID,projID,resource);
        updateProp(PAGE_METADATA,PROP_PROJID,projID,resource.getParent());
    }
    public static void addAssetReqID(String reqID, Resource resource) {
        updateProp(ASSET_METADATA,PROP_REQID,reqID,resource);
    }
    public static void addPageReqID(String reqID, Resource resource) {
        updateProp(PAGE_METADATA,PROP_REQID,reqID,resource);
    }
    public static void addAssetPDF(String pdfpath, Resource resource) {
        updateProp(ASSET_METADATA,PROP_PDF,pdfpath,resource);
    }
    public static void addPagePDF(String pdfpath, Resource resource) {
        updateProp(PAGE_METADATA,PROP_PDF,pdfpath,resource);
    }
    public static String getAssetProjID(Resource resource) {
        String projID = getPropValue(ASSET_METADATA,PROP_PROJID, resource);
        if (projID.length()<1) {
            //must look at parents but folder for asset
            String metaPath = PAGE_METADATA;
            while (resource != null) {
                resource = resource.getParent();
                if (resource != null) {
                    projID = getPropValue(metaPath,PROP_PROJID, resource);
                    if (projID.length() >0) {
                        break;
                    }
                }
            }
        }
        return projID;
    }
    public static String getPageProjID(Resource resource) {
        String metaPath = PAGE_METADATA;
        String projID = "";
        if (resource != null) {
            projID = getPropValue(metaPath, PROP_PROJID, resource);
            if (projID.length() < 1) {
                while (resource != null) {
                    resource = resource.getParent();
                    if (resource != null) {
                        projID = getPropValue(metaPath, PROP_PROJID, resource);
                        if (projID.length() > 0) {
                            break;
                        }
                    }
                }
            }
        }
        return projID;
    }

    public static String getAssetReqID(Resource resource) {
        return getPropValue(ASSET_METADATA, PROP_STATUS, resource);
    }

    public static String getPageReqID(Resource resource) {

        return getPropValue(PAGE_METADATA, PROP_STATUS, resource);
    }

    private static String getPropValue(String metaPath, String propName, Resource resource) {
        if(resource != null ) {
            Resource childResource = resource.getChild(metaPath);
            if (childResource != null) {
                ValueMap vm = resource.getChild(metaPath).getValueMap();
                String val = vm.get(propName,"");
                return val;
            }
        }
        return "";
    }

    private static void updateProp(String metaPath, String propName, String value, Resource resource) {
        ModifiableValueMap mvm = resource.getChild(metaPath).adaptTo(ModifiableValueMap.class);
        mvm.put(propName,value);
    }
}


