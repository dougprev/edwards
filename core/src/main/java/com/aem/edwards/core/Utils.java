package com.aem.edwards.core;

/**
 * Created by Douglas Prevelige on 3/28/2023.
 * Non-production code for POC purposes only.
 */

import com.adobe.granite.workflow.exec.WorkflowData;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.aem.edwards.core.Constants.*;

public class Utils {

    public static String getCompRefPath(ValueMap vm) {

        String retString = vm.get("fragmentPath",String.class); //content fragment
        if (retString == null) {
            retString = vm.get("fragmentVariationPath",String.class); //experience fragment
        }
        if (retString == null) {
            retString = vm.get("fileReference",String.class); //asset
        }

        return retString;
    }

    public static boolean isApproved(ValueMap vm) {
        Calendar lastModified = vm.get(PROP_LASTMODIFIEDjcr,Calendar.class);
        if (lastModified == null) lastModified = vm.get(PROP_LASTMODIFIEDcq,Calendar.class);
        Calendar lastApproved = vm.get(PROP_LASTAPPROVED,Calendar.class);
        if (lastApproved != null && lastModified != null) {
            return (lastApproved.after(lastModified));
        } else {
            return false;
        }
    }

    public static String getDisclaimer(ValueMap vm) {
        String retString = vm.get("disclaimer",String.class);
        if (retString == null) {
            retString = vm.get("disclaimer",String.class);
        }
        if (retString == null) {
            retString = vm.get("metadata/disclaimer",String.class);
        }
        if (retString == null) retString = "";
        return retString;
    }

    public static String getWorkflowPath(WorkflowData workflowData) {
        String path = "";
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            path = workflowData.getPayload().toString();
        } else if (workflowData.getPayloadType().equals(TYPE_URL)) {
            String url = workflowData.getPayload().toString();
            String encPath = url.substring(url.indexOf("?itemid=") + "?itemid=".length(), url.lastIndexOf("&"));
            try {
                path = URLDecoder.decode(encPath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Error!" + e.getMessage());
            }
        }
        return path;
    }

    public static String nodify(String title) {
        String retVal = "_";
        if (title != null) {
            retVal = title.toLowerCase().replace(" ","-").replace("'","").replace(":","_");
        }
        return retVal;
    }

    public static String getAssetTitle(ResourceResolver resolver, String assetPath) {
        String retVal = "";
        Resource resAsset = resolver.getResource(assetPath);
        if (resAsset != null) {
            ValueMap vm = resAsset.getValueMap();
            retVal = vm.get("jcr:content/metadata/footerTitle","");
            if (retVal.length() == 0) {
                retVal = vm.get("jcr:content/metadata/jcr:title","");
            }
            if (retVal.length() == 0) {
                retVal = vm.get("jcr:content/metadata/dc:title","");
            }
            if (retVal.length() == 0) {
                retVal = vm.get("jcr:content/jcr:title","");
            }
            if (retVal.length() == 0) {
                retVal = resAsset.getName();
            }
        }
        return retVal;
    }

    public static List<String> getListFromArrayProp(ValueMap vm, String propName) {
        List<String> retList = new ArrayList<>();

        String[] arVal = vm.get(propName,String[].class);
        if (arVal == null) {
            arVal = vm.get("metadata/"+propName,String[].class);
        }
        if (arVal != null) retList = Arrays.asList(arVal);

        return retList;
    }

    public static String UriEncode(CharSequence input, boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(toHexUTF8(ch));
            }
        }
        return result.toString();
    }

    public static String toHexUTF8(char ch) {
        if (ch == ' ') return "%20";
        return String.format("%04x",ch).toUpperCase();
    }

    static public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void approveChildren(Resource resource) {
        if (resource.hasChildren()) {
            Iterable<Resource> rit = resource.getChildren();
            for (Resource childRes : rit) {
                ValueMap vm = childRes.adaptTo(ValueMap.class);
                String assetPath = Utils.getCompRefPath(vm);
                if (assetPath != null) {
                    ResourceResolver resolver = resource.getResourceResolver();
                    Resource assetRes = resolver.getResource(assetPath + "/jcr:content");
                    setApproval(assetRes);
                    //if (!assetPath.contains("/dam/") {  assume exp frag and go into that
                    //approve children assetRes
                }
                approveChildren(childRes);
            }
        }
    }

    public static void setApproval(Resource resource) {
        ModifiableValueMap modifiableVM = resource.adaptTo(ModifiableValueMap.class);
        Calendar currentTime = Calendar.getInstance();
        modifiableVM.put(PROP_LASTAPPROVED, currentTime);
    }
    public static void resetChildrenApproval(Resource resource) {
        if (resource.hasChildren()) {
            Iterable<Resource> rit = resource.getChildren();
            for (Resource childRes : rit) {
                ValueMap vm = childRes.adaptTo(ValueMap.class);
                String assetPath = Utils.getCompRefPath(vm);
                if (assetPath != null) {
                    ResourceResolver resolver = resource.getResourceResolver();
                    Resource assetRes = resolver.getResource(assetPath + "/jcr:content");
                    resetApproval(assetRes);
                    //if (!assetPath.contains("/dam/") {  assume exp frag and go into that
                    //approve children assetRes
                }
                resetChildrenApproval(childRes);
            }
        }
    }

    public static void resetApproval(Resource resource) {
        ModifiableValueMap modifiableVM = resource.adaptTo(ModifiableValueMap.class);
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(1970,0,1,0,0,0);
        modifiableVM.put(PROP_LASTAPPROVED, currentTime);
    }

}
