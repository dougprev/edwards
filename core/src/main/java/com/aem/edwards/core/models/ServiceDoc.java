package com.aem.edwards.core.models;

/**
 * Created by Douglas Prevelige on 5/22/2023.
 * Non-production code for POC purposes only.
 */

import java.nio.charset.StandardCharsets;

public class ServiceDoc {
    private byte[] content;
    private String location;
    private String contentType;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentAsString() {
        if (content != null) {
            return new String(content, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}


