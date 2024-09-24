package com.aem.edwards.core;

/**
 * Created by Douglas Prevelige on 5/22/2023.
 * Non-production code for POC purposes only.
 */

import com.aem.edwards.core.models.ServiceDoc;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RESTAPI {

    private static final Logger log = LoggerFactory.getLogger(RESTAPI.class);

    public final static String STRINGFIELDNAME = "_STRINGRESPONSE_";

    public static Map<String,Object> callGet(String url, Map<String,String> headers) {

        Map<String,Object> responseMap = null;

        CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && headers.size()>0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpGet.addHeader(key,headers.get(key));
            }
        }

        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int resp = statusLine.getStatusCode();
            log.info("GET response code: " + resp);

            HttpEntity respEntity = response.getEntity();
            if (respEntity != null && resp <400) {
                responseMap = loadResponseMap(respEntity);
                //responseString = EntityUtils.toString(respEntity);
                //log.info("respString: " +responseString);
            }
            //logHeaders(response.getAllHeaders());
        } catch (UnsupportedEncodingException e) {
            log.error("Error!", e);
        } catch (IOException e) {
            log.error("Error!", e);
        }
        return responseMap;
    }

    public static Map<String,Object> callPostBody(String url, Map<String,String> headers, String body) {
        Map<String,Object> responseMap = null;

        CloseableHttpClient httpClient = createAcceptSelfSignedCertificateClient();
        HttpPost httpPost = getPost(url,headers);

        try {
            StringEntity stringEntity = new StringEntity(body);
            httpPost.setEntity(stringEntity);
            //log.info("***  calling exec Post Body");
            responseMap = execPost(httpPost,httpClient);

        } catch (UnsupportedEncodingException e) {
            log.error("Error!", e);
        } catch (IOException e) {
            log.error("Error!", e);
        }
        return responseMap;
    }

    public static Map<String,Object> callPostFields(String url, Map<String,String> headers, Map<String,Object> fields) {
        Map<String,Object> responseMap = null;

        CloseableHttpClient httpClient = createAcceptSelfSignedCertificateClient();
        HttpPost httpPost = getPost(url,headers);

        try {
            if (fields !=null && fields.size() > 0) {
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                Set<String> keys = fields.keySet();
                for (String key : keys) {
                    Object obj = fields.get(key);
                    if (obj instanceof String) {
                        StringBody paramValue = new StringBody((String) obj, ContentType.TEXT_PLAIN);
                        entityBuilder.addPart(key, paramValue);
                    } else if (obj instanceof byte[]) {
                        ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) obj);
                        entityBuilder.addBinaryBody(key,bais);
                    } else if (obj instanceof InputStream) {
                        entityBuilder.addBinaryBody(key,(InputStream) obj);
                    }
                }
                httpPost.setEntity(entityBuilder.build());
            }

            responseMap = execPost(httpPost,httpClient);

        } catch (UnsupportedEncodingException e) {
            log.error("Error!", e);
        } catch (IOException e) {
            log.error("Error!", e);
        }
        return responseMap;
    }

    private static HttpPost getPost(String url, Map<String,String>headers) {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null && headers.size()>0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpPost.addHeader(key,headers.get(key));
            }
        }
        return httpPost;
    }
    private static Map<String,Object> execPost(HttpPost httpPost, CloseableHttpClient httpClient) throws IOException {
        Map<String,Object> responseMap = null;
        //logHeaders(httpPost.getAllHeaders());
        CloseableHttpResponse response = httpClient.execute(httpPost);

        StatusLine statusLine = response.getStatusLine();
        int resp = statusLine.getStatusCode();
        log.info("*********  EXEC POST  ************");
        log.info("POST response code: " + resp);
        //logHeaders(response.getAllHeaders());
        HttpEntity respEntity = response.getEntity();
        if (respEntity != null && respEntity.getContentLength()>0) {
            responseMap = loadResponseMap(respEntity);

        } else {
            log.info("response entitiy is NULL");
        }
        //logHeaders(response.getAllHeaders());
        log.info("*********  EXEC POST COMPLETE ************");

        return responseMap;
    }

    private static Map<String, Object> loadResponseMap(HttpEntity httpEntity) {
        HashMap<String,Object> responseMap = new HashMap<>();
        String respContentType = httpEntity.getContentType() != null ? httpEntity.getContentType().getValue() : "";
        if (respContentType.startsWith("application/json") || respContentType.startsWith("text/")) {
            try {
                String responseString = EntityUtils.toString(httpEntity);
                responseMap.put(STRINGFIELDNAME,responseString);
                log.info("responseString:  " + responseString);
            } catch (IOException e) {
                log.error("Error!",e);
            }
        } else if (respContentType.length()>0) {
            try {
                ByteArrayDataSource bads = new ByteArrayDataSource(httpEntity.getContent(), "multipart/form-data");
                MimeMultipart multipart = new MimeMultipart(bads);

                int count = multipart.getCount();
                log.info("bodypart count " + count);
                for (int i = 0; i < count; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String[] dispHeaders = bodyPart.getHeader("Content-Disposition");
                    String partName = "part"+i;
                    if (dispHeaders != null) {
                        String headerVal = dispHeaders[0];
                        log.info("ContentDispositionHeader: " + headerVal);
                        partName = headerVal.substring(headerVal.indexOf("\"")+1,headerVal.lastIndexOf("\""));
                    }
                    log.info("partName: " + partName);
                    String bodyPartContentType = bodyPart.getContentType();
                    //if (bodyPartContentType.startsWith("text/") || bodyPartContentType.equals("application/json")) {
                    log.info("contentType: " + bodyPart.getContentType());
                    log.info("disposition: " + bodyPart.getDisposition());
                    ServiceDoc serviceDoc = new ServiceDoc();
                    serviceDoc.setContentType(bodyPart.getContentType());
                    serviceDoc.setContent(IOUtils.toByteArray(bodyPart.getInputStream()));
                    serviceDoc.setLocation(partName);
                    responseMap.put(partName,serviceDoc);
                    /*} else if (bodyPart.isMimeType("application/octet-stream")) {
                        log.info("contentType " + bodyPart.getContentType());
                        log.info("disposition: " + bodyPart.getDisposition());
                        ServiceDoc serviceDoc = new ServiceDoc();
                        serviceDoc.setContentType(bodyPart.getContentType());
                        serviceDoc.setContent(IOUtils.toByteArray(bodyPart.getInputStream()));
                        serviceDoc.setLocation(partName);
                        responseMap.put(partName,serviceDoc);
                    } else {
                        log.info("other contentType " + bodyPart.getContentType());
                    }*/
                }
            } catch (IOException e) {
                log.error("Error!",e);
            } catch (MessagingException e) {
                log.error("Error!",e);
            }
        }
        return responseMap;
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient() {

        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLConnectionSocketFactory connectionFactory = null;
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            // we can optionally disable hostname verification.
            // if you don't want to further weaken the security, you don't have to include this.
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

            // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
            // and allow all hosts verifier.
            connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error!", e);
        } catch (KeyManagementException e) {
            log.error("Error!", e);
        } catch (KeyStoreException e) {
            log.error("Error!", e);
        }

        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        return HttpClients
                .custom()
                .setSSLSocketFactory(connectionFactory)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private static void logHeaders(Header[] respHeaders) {

        if (respHeaders != null) {
            log.info("Response Headers");
            for (int i = 0; i < respHeaders.length; i++) {
                Header header = respHeaders[i];
                log.info(header.getName() + " : " + header.getValue());
            }
        }
    }

}


