package org.jenkins.plugins.wechatbot;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.jenkins.plugins.wechatbot.util.ConfigUtil;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信消息通知配置描述
 *
 * @author yulinying
 * @since 2019-11-05
 */
@Extension
public class NotifierDescriptor extends BuildStepDescriptor<Publisher> {
    
    private final static transient Logger LOGGER = LoggerFactory.getLogger(NotifierDescriptor.class);
    
    /**
     * 默认企业微信 WebHook 地址
     */
    private String webhookUrl = "";
    
    /**
     * 默认通知用户ID，使用 "," 分隔
     */
    private String memberId = "";
    
    /**
     * 默认通知用户手机，使用 "," 分隔
     */
    private String memberMobile = "";
    
    /**
     * 默认环境名称
     */
    private String topicName = "测试环境";
    
    /**
     * 通知用户ID分组配置：<分组名称：memberId>
     */
    private Map<String, String> memberIdGroup = new HashMap<>();
    
    /**
     * 通知用户手机号码分组配置：<分组名称：memberMobile>
     */
    private Map<String, String> memberMobileGroup = new HashMap<>();
    
    /**
     * 是否自定义构建阶段通知
     */
    
    private boolean customNotify = false;
    
    /**
     * 开始构建时 @人
     */
    private boolean notifyStart = true;
    
    /**
     * 成功构建时 @人
     */
    private boolean notifySuccess = true;
    
    /**
     * 构建失败时 @人
     */
    private boolean notifyFail = true;
    
    /**
     * 暂停所有通知
     */
    private boolean closeAll = false;
    
    /**
     * 是否开启对 jenkins 构建发起人的通知
     */
    private boolean notifyCauseUser = false;
    
    /**
     * jenkins userId 对企业微信 mentioned_mobile_list 的映射
     */
    private Map<String, String> userIdMobileMap = new HashMap<>();
    
    /**
     * jenkins userName 对企业微信 mentioned_mobile_list 的映射
     */
    private Map<String, String> userNameMobileMap = new HashMap<>();
    
    
    public NotifierDescriptor() {
        super(WechatBotNotifier.class);
        load();
    }
    
    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }
    
    /**
     * 数据配置
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        setWebhookUrl(json.getString("webhookUrl"));
        setMemberId(json.getString("memberId"));
        setMemberMobile(json.getString("memberMobile"));
        setMemberIdGroup(json.getString("memberIdGroup"));
        setMemberMobileGroup(json.getString("memberMobileGroup"));
        setCloseAll(json.getBoolean("closeAll"));
        setCustomNotify(json.get("customNotify") != null);
        if (isCustomNotify() && json.get("customNotify") instanceof JSONObject) {
            JSONObject jsonObject = json.getJSONObject("customNotify");
            setNotifyStart(jsonObject.getBoolean("notifyStart"));
            setNotifySuccess(jsonObject.getBoolean("notifySuccess"));
            setNotifyFail(jsonObject.getBoolean("notifyFail"));
        }
        setNotifyCauseUser(json.get("notifyCauseUser") != null);
        if (json.get("notifyCauseUser") instanceof JSONObject) {
            JSONObject jsonObject = json.getJSONObject("notifyCauseUser");
            setUserIdMobileMapStr(jsonObject.getString("userIdMobileMap"));
            setUserNameMobileMapStr(jsonObject.getString("userNameMobileMap"));
        }
        save();
        return super.configure(req, json);
    }
    
    /**
     * 插件名称
     */
    @Nonnull
    @Override
    public String getDisplayName() {
        return "企业微信-群机器人消息通知";
    }
    
    public String getMemberIdGroupStr() {
        return ConfigUtil.convertKvMapToString(getMemberIdGroup());
    }
    
    public String getMemberMobileGroupStr() {
        return ConfigUtil.convertKvMapToString(getMemberMobileGroup());
    }
    
    public String getUserIdMobileMapStr(){
        return ConfigUtil.convertKvMapToString(getUserIdMobileMap());
    }
    
    public String getUserNameMobileMapStr(){
        return ConfigUtil.convertKvMapToString(getUserNameMobileMap());
    }
    
    public void setMemberIdGroup(String groupStr) {
        setMemberIdGroup(ConfigUtil.convertKvStringToMap(groupStr));
    }
    
    public void setMemberMobileGroup(String groupStr) {
        setMemberMobileGroup(ConfigUtil.convertKvStringToMap(groupStr));
    }
    
    public void setUserIdMobileMapStr(String strMap) {
        setUserIdMobileMap(ConfigUtil.convertKvStringToMap(strMap));
    }
    
    public void setUserNameMobileMapStr(String strMap) {
        setUserNameMobileMap(ConfigUtil.convertKvStringToMap(strMap));
    }
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberMobile() {
        return memberMobile;
    }
    
    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public Map<String, String> getMemberIdGroup() {
        return memberIdGroup;
    }
    
    public void setMemberIdGroup(Map<String, String> memberIdGroup) {
        this.memberIdGroup = memberIdGroup;
    }
    
    public Map<String, String> getMemberMobileGroup() {
        return memberMobileGroup;
    }
    
    public void setMemberMobileGroup(Map<String, String> memberMobileGroup) {
        this.memberMobileGroup = memberMobileGroup;
    }
    
    
    public boolean isNotifyStart() {
        return notifyStart;
    }
    
    public void setNotifyStart(boolean notifyStart) {
        this.notifyStart = notifyStart;
    }
    
    public boolean isNotifySuccess() {
        return notifySuccess;
    }
    
    public void setNotifySuccess(boolean notifySuccess) {
        this.notifySuccess = notifySuccess;
    }
    
    public boolean isNotifyFail() {
        return notifyFail;
    }
    
    public void setNotifyFail(boolean notifyFail) {
        this.notifyFail = notifyFail;
    }
    
    public boolean isCustomNotify() {
        return customNotify;
    }
    
    public void setCustomNotify(boolean customNotify) {
        this.customNotify = customNotify;
    }
    
    public boolean isCloseAll() {
        return closeAll;
    }
    
    public void setCloseAll(boolean closeAll) {
        this.closeAll = closeAll;
    }
    
    public boolean isNotifyCauseUser() {
        return notifyCauseUser;
    }
    
    public void setNotifyCauseUser(boolean notifyCauseUser) {
        this.notifyCauseUser = notifyCauseUser;
    }
    
    public Map<String, String> getUserIdMobileMap() {
        return userIdMobileMap;
    }
    
    public void setUserIdMobileMap(Map<String, String> userIdMobileMap) {
        this.userIdMobileMap = userIdMobileMap;
    }
    
    public Map<String, String> getUserNameMobileMap() {
        return userNameMobileMap;
    }
    
    public void setUserNameMobileMap(Map<String, String> userNameMobileMap) {
        this.userNameMobileMap = userNameMobileMap;
    }
}
