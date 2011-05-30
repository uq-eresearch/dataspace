package net.metadata.dataspace.solr;

import java.util.Date;
import java.util.Map;

/**
 * Author: alabri
 * Date: 30/05/11
 * Time: 10:56 AM
 */
public class SolrCommand {
    private String server;
    private int port;
    private String webapp;
    private String name;
    private Map<String, String> params;

    public SolrCommand(String server, int port, String webapp, String name, Map<String, String> params) {
        this.server = server;
        this.port = port;
        this.webapp = webapp;
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        String url = getServer() + ":" + getPort() + "/" + getWebapp() + "/" + getName() + "?";
        Map<String, String> params = getParams();
        for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
            url = url + stringStringEntry.getKey() + "=" + stringStringEntry.getValue() + "&";
        }
        url = url + "preventCache=" + (new Date()).getTime();
        return url;
    }
}
