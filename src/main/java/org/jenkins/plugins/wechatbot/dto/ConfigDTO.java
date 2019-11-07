package org.jenkins.plugins.wechatbot.dto;

import hudson.model.Result;

import java.util.Set;

/**
 * 配置中转对象
 *
 * @author yulinying
 * @since 2019-11-07
 */
public class ConfigDTO {
    /**
     * 企业微信 WebHook 地址
     */
    private Set<String> webhookUrls;
    
    /**
     * 通知用户ID
     */
    private Set<String> memberIds;
    
    /**
     * 通知用户手机
     */
    private Set<String> memberMobiles;
    
    /**
     * 环境名称
     */
    private String topicName = "";
    
    /**
     * 项目名称
     */
    private String projectName = "";
    
    /**
     * 本次构建的控制台地址
     */
    private String consoleUrl = "";
    
    /**
     * 执行结果
     */
    private Result buildResult;
    
    /**
     * 预计时间/执行用时， 单位 ms
     */
    private long durationTime;
    
    /**
     * 构建者id
     */
    private String causeUserId;
    
    /**
     * 构建者名称
     */
    private String causeUserName;
    
    
    public Set<String> getWebhookUrls() {
        return webhookUrls;
    }
    
    public void setWebhookUrls(Set<String> webhookUrls) {
        this.webhookUrls = webhookUrls;
    }
    
    public Set<String> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(Set<String> memberIds) {
        this.memberIds = memberIds;
    }
    
    public Set<String> getMemberMobiles() {
        return memberMobiles;
    }
    
    public void setMemberMobiles(Set<String> memberMobiles) {
        this.memberMobiles = memberMobiles;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getConsoleUrl() {
        return consoleUrl;
    }
    
    public void setConsoleUrl(String consoleUrl) {
        this.consoleUrl = consoleUrl;
    }
    
    public Result getBuildResult() {
        return buildResult;
    }
    
    public void setBuildResult(Result buildResult) {
        this.buildResult = buildResult;
    }
    
    public long getDurationTime() {
        return durationTime;
    }
    
    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }
    
    public String getCauseUserId() {
        return causeUserId;
    }
    
    public void setCauseUserId(String causeUserId) {
        this.causeUserId = causeUserId;
    }
    
    public String getCauseUserName() {
        return causeUserName;
    }
    
    public void setCauseUserName(String causeUserName) {
        this.causeUserName = causeUserName;
    }
}
