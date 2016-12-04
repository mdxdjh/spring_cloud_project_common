package com.ellis.commons.net;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpInvokeUtil
{
    private static final Logger log = LoggerFactory.getLogger(HttpInvokeUtil.class);

    private static final int CONN_TIME_OUT = 3 * 1000;
    private static final int SOCKET_TIME_OUT = 5 * 1000;

    /**
     * http Post 请求
     * 
     * @param url
     *        请求路径
     * @param paramMap
     *        请求参数
     * @param encode
     *        请求参数的url编码，及返回数据的编码
     * @return
     */
    public static String httpPost(String url, Map<String, String> paramMap, String encode)
    {
        try
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(getPostParam(paramMap, encode));

            return httpPost(httpPost, encode);
        }
        catch (Throwable e)
        {
            log.error("http simple post paramMap is error", e);
        }

        return "";
    }

    /**
     * http Post 请求
     * 
     * @param url
     *        请求路径
     * @param text
     *        请求参数
     * @param encode
     *        请求参数的url编码，及返回数据的编码
     * @return
     */
    public static String httpPost(String url, String text, String encode)
    {
        try
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(text, encode));

            return httpPost(httpPost, encode);
        }
        catch (Throwable e)
        {
            log.error("http simple post text is error", e);
        }

        return "";
    }

    // post请求参数，请求参数使用UTF-8编码
    public static HttpEntity getPostParam(Map<String, String> paramMap, String encode) throws Exception
    {
        List<NameValuePair> nvps = new ArrayList<>();
        if (paramMap != null && !paramMap.isEmpty())
        {
            for (Map.Entry<String, String> entry : paramMap.entrySet())
            {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return new UrlEncodedFormEntity(nvps, Charset.forName(encode));
    }

    public static String httpPost(HttpPost post, String encode)
    {
        // try-with-resource
        HttpResponse response;
        try (CloseableHttpClient httpClient = HttpClients.createDefault())
        { // 链接超时，请求超时设置
            RequestConfig reqConf = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONN_TIME_OUT).build();
            post.setConfig(reqConf);
            response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity(), encode);
        }
        catch (Throwable e)
        {
            log.error("http post request error", e);
        }

        return "";
    }

    /**
     * http Post 请求包括上传文件,除了文件参数之外,还可以有其他参数
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String httpPostFile(String url, Map<String, String> paramMap, String fileFormName, String filePath,
            String encode)
    {
        if (StringUtils.isBlank(url))
        {
            throw new RuntimeException("url参数错误");
        }

        CloseableHttpClient client = null;
        CloseableHttpResponse resp = null;
        try
        {
            // 请求参数构建
            HttpEntity entity = getPostParam(paramMap, fileFormName, filePath);
            // 链接超时，请求超时设置
            RequestConfig reqConf = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONN_TIME_OUT).build();
            client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost(url);
            post.setEntity(entity);// 设置参数
            post.setConfig(reqConf);// 设置配置
            resp = client.execute(post);

            return handlerResponse(resp, encode);
        }
        catch (Throwable e)
        {
            log.error("http post request error", e);
        }
        finally
        {
            try
            {
                if (client != null)
                {
                    client.close();
                }
                if (resp != null)
                {
                    resp.close();
                }
            }
            catch (IOException e)
            {
                log.error("close muitl file post http client is error", e);
            }
        }
        return "";
    }

    /**
     * htppGET请求，返回字符
     * 
     * @param url
     * @param paramMap
     * @return String
     * @throws Exception
     */
    public static String httpGet(String url, Map<String, String> paramMap)
    {
        if (StringUtils.isBlank(url))
        {
            throw new RuntimeException("url参数错误");
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse resp = null;
        try
        {
            String encodedUrl = encodeGetUrl(url, paramMap);
            HttpGet httpGet = new HttpGet(encodedUrl);
            RequestConfig reqConf = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT)
                    .setConnectTimeout(CONN_TIME_OUT).build();
            httpGet.setConfig(reqConf);
            // 发起请求 并返回请求的响应
            resp = httpClient.execute(httpGet);
            // 返回响应结果
            return handlerResponse(resp, "UTF-8");
        }
        catch (Throwable e)
        {
            throw new RuntimeException("url: " + url, e);
        }
        finally
        {
            try
            {
                httpClient.close();
                if (resp != null)
                {
                    resp.close();
                }
            }
            catch (IOException e)
            {
                log.error("close http Client is error", e);
            }
        }
    }

    public static HttpEntity streamHttpGet(String url, Map<String, String> paramMap)
    {
        if (StringUtils.isBlank(url))
        {
            throw new RuntimeException("url参数错误");
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse resp;
        try
        {
            HttpGet httpGet = new HttpGet(encodeGetUrl(url, paramMap));
            // 发起请求 并返回请求的响应
            resp = httpClient.execute(httpGet);
            // 直接返回 Inputstream 会产生 Attempted read from closed stream.错误。流会关闭。
            return resp.getEntity();
        }
        catch (Throwable e)
        {
            log.error("http get request error", e);
        }

        return null;
    }

    /**
     * 生成满足Get请求方式的URI规范路径, 如果参数paramMap不为空, 则转换编码后和url进行拼接, 如下格式URL都可以自动处理:
     * <ul>
     */
    private static String encodeGetUrl(String url, Map<String, String> paramMap) throws URISyntaxException
    {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (MapUtils.isNotEmpty(paramMap))
        {
            for (Map.Entry<String, String> entry : paramMap.entrySet())
            {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder.toString();
    }

    // post请求参数，请求参数使用UTF-8编码
    private static HttpEntity getPostParam(Map<String, String> paramMap, String fileName, String filePath)
            throws Exception
    {
        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
        if (paramMap != null && !paramMap.isEmpty())
        {
            for (Map.Entry<String, String> entry : paramMap.entrySet())
            {
                String parmName = entry.getKey();
                String parmValue = entry.getValue();

                reqEntity.addTextBody(parmName, parmValue, ContentType.create("text/plain", Consts.UTF_8));
            }
        }

        if (StringUtils.isNotBlank(filePath))
        {
            reqEntity.addBinaryBody(fileName, new File(filePath));
        }

        return reqEntity.build();
    }

    // 处理返回结果
    private static String handlerResponse(HttpResponse resp, String encode) throws Exception
    {
        StatusLine statusLine = resp.getStatusLine();
        if (statusLine.getStatusCode() >= 300)
        {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        // 获取响应对象
        HttpEntity resEntity = resp.getEntity();
        if (resEntity == null)
        {
            throw new ClientProtocolException("response contains no content");
        }
        long len = resEntity.getContentLength();
        // chunked has no length
        // if (len < 0 || len > 2048 * 2)
        // {
        // throw new ClientProtocolException("the server resp too long, len : " + len);
        // }
        String resContent = EntityUtils.toString(resEntity, Charset.forName(encode));
        resContent = resContent == null ? "" : resContent;
        log.debug("handler response" + resContent);
        EntityUtils.consume(resEntity); // 销毁

        return resContent;
    }

}
