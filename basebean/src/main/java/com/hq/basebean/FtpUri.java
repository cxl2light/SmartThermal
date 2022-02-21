package com.hq.basebean;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.StringTokenizer;

/**
 * Created on 2020/5/25
 * author :
 * desc :
 */
public class FtpUri {
    private String ip;
    private int port = 21;
    private String name = "anonymous";
    private String pwd = "";

    public FtpUri() {
    }

    public FtpUri(String ip, int port, String name, String pwd) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.pwd = pwd;
    }

    @Nullable
    public static FtpUri parse(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        final FtpUri ftpUri = new FtpUri();
        if (uri.contains("@")) {
            String userInfo = uri.split("@")[0];
            userInfo = userInfo.substring(userInfo.lastIndexOf("/") + 1);
            if (userInfo.contains(":")) {
                ftpUri.name = userInfo.split(":")[0];
                ftpUri.pwd = userInfo.split(":")[1];
            } else {
                ftpUri.name = userInfo;
            }
            parseIp(ftpUri, uri.split("@")[1]);
        } else {
            final String tmp = "://";
            String ipInfo = uri.substring(uri.indexOf(tmp) + tmp.length());
            parseIp(ftpUri, ipInfo);
        }
        return ftpUri;
    }

    private static void parseIp(@NonNull FtpUri ftpUri, @NonNull String ipInfo) {
        if (ipInfo.contains(":")) {
            ftpUri.ip = ipInfo.split(":")[0];
            final String port = ipInfo.split(":")[1];
            if (port.contains("/")) {
                parsePort(ftpUri, port.substring(0, port.indexOf("/")));
            } else {
                parsePort(ftpUri, port);
            }
        } else {
            if (ipInfo.contains("/")) {
                ftpUri.ip = ipInfo.substring(0, ipInfo.indexOf("/"));
            } else {
                ftpUri.ip = ipInfo;
            }
        }
    }

    private static void parsePort(@NonNull FtpUri ftpUri, String port) {
        if (TextUtils.isEmpty(port)) {
            return;
        }
        try {
            ftpUri.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static FtpUri parse(@NonNull Uri uri) {
        final FtpUri ftpUri = new FtpUri();
        ftpUri.ip = uri.getHost();
        final int port = uri.getPort();
        if (port > 0) {
            ftpUri.port = port;
        }
        final String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            final StringTokenizer tok = new StringTokenizer(userInfo, ":@");
            if (tok.countTokens() > 0) {
                ftpUri.name = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    ftpUri.pwd = tok.nextToken();
                }
            }
        }
        return ftpUri;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
