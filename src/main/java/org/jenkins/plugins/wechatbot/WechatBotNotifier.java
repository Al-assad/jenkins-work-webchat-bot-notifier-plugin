package org.jenkins.plugins.wechatbot;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import org.apache.commons.collections4.CollectionUtils;
import org.jenkins.plugins.wechatbot.dto.ConfigDTO;
import org.jenkins.plugins.wechatbot.sender.BotSender;
import org.jenkins.plugins.wechatbot.util.ConfigUtil;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

/**
 * 企业微信群通知触发器
 *
 * @author yulinying
 * @since 2019-11-07
 */
public class WechatBotNotifier extends Notifier {
    
    /**
     * 企业微信 WebHook 地址
     */
    private String webhookUrl = "";
    
    /**
     * 通知用户ID，使用 "," 分隔
     */
    private String memberId = "";
    
    /**
     * 通知用户手机，使用 "," 分隔
     */
    private String memberMobile = "";
    
    /**
     * 通知用户分组名
     */
    private String memberGroup = "";
    
    /**
     * 环境名称
     */
    private String topicName = "";
    
//    public NotifierDescriptor descriptor;
    
    
    @Override
    public NotifierDescriptor getDescriptor() {
        return (NotifierDescriptor) super.getDescriptor();
    }
    
    @DataBoundConstructor
    public WechatBotNotifier() {
    }
    
    /**
     * 开始构建插入点
     */
    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (getDescriptor().isCloseAll()) {
            return true;
        }
        if (build == null){
            return true;
        }
        NotifierDescriptor descriptor =  getDescriptor();
        Cause.UserIdCause cause = build.getCause(Cause.UserIdCause.class);
        // 合并当前实例和全局的配置
        ConfigDTO config = ConfigUtil.mergeConfig(descriptor, this, null, true, cause);
        if (config == null || CollectionUtils.isEmpty(config.getWebhookUrls())) {
            return true;
        }
        // 填充 jenkins 环境相关的参数
        config = ConfigUtil.configJenkinsEnvParamWhenBuildStart(config, build);
        // 发送企业微信群通知
        BotSender.ofBuildStart(config, listener).send();
        return super.prebuild(build, listener);
    }
    

    
    /**
     * 构建结束插入点
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (build.isBuilding()){
            return true;
        }
        if (getDescriptor().isCloseAll()) {
            return true;
        }
        // 合并当前实例和全局的配置
        NotifierDescriptor descriptor = getDescriptor();
        Cause.UserIdCause cause = build.getCause(Cause.UserIdCause.class);
        ConfigDTO config = ConfigUtil.mergeConfig(descriptor, this, build.getResult(), false, cause);
        if (config == null || CollectionUtils.isEmpty(config.getWebhookUrls())) {
            return true;
        }
        // 填充 jenkins 环境相关的参数
        config = ConfigUtil.configJenkinsEnvParamWhenBuildOver(config, build);
        // 发送企业微信群通知
        BotSender.ofBuildOver(config, listener).send();
        return super.perform(build, launcher, listener);
    }
    
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
    
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    @DataBoundSetter
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    @DataBoundSetter
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberMobile() {
        return memberMobile;
    }
    
    @DataBoundSetter
    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }
    
    public String getMemberGroup() {
        return memberGroup;
    }
    
    @DataBoundSetter
    public void setMemberGroup(String memberGroup) {
        this.memberGroup = memberGroup;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    @DataBoundSetter
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
}
