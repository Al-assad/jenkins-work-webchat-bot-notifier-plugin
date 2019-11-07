package org.jenkins.plugins.wechatbot.util;

import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * jenkins 辅助工具
 *
 * @author yulinying
 * @since 2019-11-07
 */
public class JenkinsUtil {
    
    /**
     * 获取 task 控制台地址
     */
    public static String getConsoleUrl(@Nonnull AbstractBuild<?, ?> build) {
        StringBuilder urlBuilder = new StringBuilder();
        String jenkinsUrl = Jenkins.getInstance().getRootUrl();
        if (jenkinsUrl != null && jenkinsUrl.length() > 0 && !jenkinsUrl.endsWith("/")) {
            jenkinsUrl = jenkinsUrl + "/";
        }
        if(StringUtils.isNotEmpty(jenkinsUrl)){
            String buildUrl = build.getUrl();
            urlBuilder.append(jenkinsUrl);
            if(!jenkinsUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append(buildUrl);
            if(!buildUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append("console");
        }
        return urlBuilder.toString();
    }
    
    
    
}
