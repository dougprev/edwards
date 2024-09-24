package com.aem.edwards.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Locale;

/**
 * Created by Douglas Prevelige on 3/10/2023.
 * Non-production code for POC purposes only.
 */
public class InstructionHelper {
    private static final Logger log = LoggerFactory.getLogger(InstructionHelper.class);

    public static String pathCheck(String path) {
        if (path == null || path.length() ==0) return path;
        if (path.endsWith("/")) return path.substring(0,path.length()-1);
        return path;
    }

    public static String getTitle(JsonObject joInstruction) {
        String pageTitle = notBlank(joInstruction,Constants.TITLE) ?
                joInstruction.getString(Constants.TITLE) :
                joInstruction.getString(Constants.NAME);
        return pageTitle;
    }
    public static String getName(JsonObject joInstruction) {
        String pageName = notBlank(joInstruction,Constants.NAME) ?
                joInstruction.getString(Constants.NAME) :
                titleToName(joInstruction.getString(Constants.TITLE));
        return pageName;
    }
    public static String titleToName(String title) {
        return title.
                toLowerCase(Locale.ROOT).
                replace(" ","-").
                replace("'","").
                replace("&","-");
    }

    public static String checkCreateElements(JsonObject joInstruction) {
        if (joInstruction.containsKey(Constants.PARENTFOLDER) &&
                joInstruction.containsKey(Constants.TEMPLATEPATH) &&
                (joInstruction.containsKey(Constants.TITLE) || joInstruction.containsKey(Constants.NAME))) {
            if (StringUtils.isNotBlank(joInstruction.getString(Constants.PARENTFOLDER)) &&
                    StringUtils.isNotBlank(joInstruction.getString(Constants.TEMPLATEPATH)) &&
                    (notBlank(joInstruction,Constants.TITLE) || notBlank(joInstruction,Constants.NAME))
            ) {
                return Constants.SUCCESS;
            } else {
                return "Missing value for mandatory Create property.";
            }
        } else {
            return "Missing mandatory Create property.";
        }
    }
    public static String checkCopyElements(JsonObject joInstruction) {
        if (joInstruction.containsKey(Constants.PARENTFOLDER) &&
                joInstruction.containsKey(Constants.SOURCEPATH) &&
                (joInstruction.containsKey(Constants.TITLE) || joInstruction.containsKey(Constants.NAME))) {
            if (notBlank(joInstruction,Constants.PARENTFOLDER) &&
                    notBlank(joInstruction,Constants.SOURCEPATH) &&
                    (notBlank(joInstruction,Constants.TITLE) || notBlank(joInstruction,Constants.NAME))
            ) {
                return Constants.SUCCESS;
            } else {
                return "Missing value for mandatory Copy property.";
            }
        } else {
            return "Missing mandatory Copy property.";
        }
    }

    public static boolean notBlank(JsonObject jo, String propName) {
        if (!jo.containsKey(propName)) return false;
        if (jo.get(propName).getValueType() == JsonValue.ValueType.ARRAY) {
            return !jo.getJsonArray(propName).isEmpty();
        }
        return StringUtils.isNotBlank(jo.getString(propName));
    }
}
