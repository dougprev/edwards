package com.aem.edwards.core;

/**
 * Created by Douglas Prevelige on 3/28/2023.
 * Non-production code for POC purposes only.
 */
public class Constants {

    public static final String TYPE_JCR_PATH = "JCR_PATH";
    public static final String TYPE_URL = "URL";

    public static final String PROP_LASTAPPROVED = "lastApproved";
    public static final String PROP_LASTMODIFIEDjcr = "jcr:lastModified";
    public static final String PROP_LASTMODIFIEDcq = "cq:lastModified";

    public static final String VAL_APPROVED = "pocApproved";
    public static final String VAL_NOTAPPROVED = "pocNotApproved";

    public static final String ASSET_META = "jcr:content/metadata";
    public static final String ASSET_ORIGINALCONTENT = "jcr:content/renditions/original/jcr:content";

    // contentapi
    public final static String PARENTFOLDER = "parentFolder";
    public final static String TITLE = "title";
    public final static String NAME = "name";
    public final static String SOURCEPATH = "sourcePath";
    public final static String TEMPLATEPATH = "templatePath";

    public final static String PROPERTIES = "properties";
    public final static String PROP_NAME = "propName";
    public final static String PROP_VALUE = "propValue";

    public final static String VARIANTS = "variants";
    public final static String VAR_NAME = "variantName";
    public final static String VAR_TITLE = "variantTitle";
    public final static String VAR_TEMPLATE = "variantTemplate";
    public final static String VAR_LIVECOPY = "isLiveCopy";
    public final static String VAR_ROLLOUT = "rolloutConfigs";

    public final static String TYPE = "type";
    public final static String ACTION = "action";
    public final static String TYPE_XF = "xf";
    public final static String TYPE_PAGE = "page";
    public final static String TYPE_CF = "cf";
    public final static String TYPE_CFM = "cfm";
    public final static String TYPE_WF = "wf";
    public final static String TYPE_FOLDER = "folder";
    public final static String ACTION_CREATE = "create";
    public final static String ACTION_DELETE = "delete";
    public final static String ACTION_COPY = "copy";
    public final static String ACTION_SET = "set";

    public final static String ELEMENTS = "cfElements";
    public final static String EL_NAME = "elementName";
    public final static String EL_VALUE = "elementValue";
    public final static String EL_CONTENTTYPE = "elementContentType";

    public final static String CFMFIELDS = "cfmFields";
    public final static String CFM_LABEL = "fieldLabel";
    public final static String CFM_NAME = "name";
    public final static String CFM_METATYPE = "metaType";
    public final static String CFM_EMPTY = "emptyText";
    public final static String CFM_MIMETYPE = "default-mime-type";
    public final static String CFM_DESCRIPTION = "fieldDescription";
    public final static String CFM_DEFVALUE = "value";
    public final static String CFM_MULTI = "isMultifield";
    public final static String CFM_MAXLEN = "maxLength";
    public final static String CFM_ROOT = "rootPath";
    public final static String CFM_OPTIONS = "optionsmultifield";
    public final static String CFM_OPT_VALUE = "fieldValue";
    public final static String CFM_OPT_LABEL = "fieldLabel";

    public final static String SUCCESS = "success";

    public final static String MODELS_PATH = "/var/workflow/models";

    public final static String ATTR_ORIGREF = "origref";
}
