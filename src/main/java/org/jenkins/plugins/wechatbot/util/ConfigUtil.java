package org.jenkins.plugins.wechatbot.util;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Result;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jenkins.plugins.wechatbot.NotifierDescriptor;
import org.jenkins.plugins.wechatbot.WechatBotNotifier;
import org.jenkins.plugins.wechatbot.dto.ConfigDTO;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配置处理辅助工具类
 *
 * @author yulinying
 * @since 2019-11-05
 */
public class ConfigUtil {
    
    /**
     * key-value Map 转化为字符串形式
     */
    public static String convertKvMapToString(Map<String, String> kvMap) {
        if (MapUtils.isEmpty(kvMap)) {
            return "";
        }
        return kvMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("\n"));
    }
    
    /**
     * key-value line 格式字符串转化为 Map 结构
     */
    public static Map<String, String> convertKvStringToMap(String kvString) {
        if (StringUtils.isEmpty(kvString)) {
            return new HashMap<>(0);
        }
        String[] kvParts = kvString.split("\n");
        if (ArrayUtils.isEmpty(kvParts)) {
            return new HashMap<>(0);
        }
        Map<String, String> result = new HashMap<>(kvParts.length);
        for (String kvPart : kvParts) {
            String[] kv = kvPart.trim().split("=");
            if (ArrayUtils.isEmpty(kv)) {
                continue;
            }
            if (kv.length < 2) {
                result.put(kv[0].trim(), "");
            } else {
                result.put(kv[0].trim(), kv[1].trim());
            }
        }
        return result;
    }
    
    
    /**
     * 合并全局和当前环境的配置信息
     */
    @Nonnull
    public static ConfigDTO mergeConfig(NotifierDescriptor descriptor,
                                        WechatBotNotifier notifier,
                                        Result buildResult,
                                        boolean isBuildStart,
                                        Cause.UserIdCause cause) {
        ConfigDTO config = new ConfigDTO();
        // 合并 wehookUrl 设置
        config.setWebhookUrls(convertParamStrToSet(descriptor.getWebhookUrl() + "," + notifier.getWebhookUrl()));
        // 启用构建 @人
        if (isShouldAt(descriptor, buildResult, isBuildStart)) {
            // 合并通知 UserID 设置
            config.setMemberIds(convertParamStrToSet(
                    descriptor.getMemberId() + "," + notifier.getMemberId() + "," + descriptor.getMemberIdGroup().get(notifier.getMemberGroup())));
            // 合并通知手机号码设置
            config.setMemberMobiles(convertParamStrToSet(
                    descriptor.getMemberMobile() + "," + notifier.getMemberMobile() + "," + descriptor.getMemberMobileGroup().get(notifier.getMemberGroup())));
            // 构建发起者通知
            if (descriptor.isNotifyCauseUser()) {
                config = configNotifyCauseUser(config, descriptor, cause);
            }
        }
        // 设置环境名称
        config.setTopicName(mergeTopicName(descriptor, notifier));
        // 设置构建者信息
        config.setCauseUserId(cause.getUserId());
        config.setCauseUserName(cause.getUserName());
        return config;
    }
    
    /**
     * 判断是否应该 @ 人
     */
    private static boolean isShouldAt(@Nonnull NotifierDescriptor descriptor,  Result buildResult, boolean isBuildStart) {
        if (isBuildStart && descriptor.isNotifyStart()) {
            return true;
        }
        if (!isBuildStart) {
            if (buildResult == null) {
                return false;
            }
            if (Result.SUCCESS.equals(buildResult) && descriptor.isNotifySuccess()) {
                return true;
            }
            if (!Result.SUCCESS.equals(buildResult) && descriptor.isNotifyFail()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 开始构建阶段，向配置传输对象添加 jenkins 相关配置
     */
    public static ConfigDTO configJenkinsEnvParamWhenBuildStart(@Nonnull ConfigDTO configDTO, @Nonnull AbstractBuild<?, ?> build) {
        // 获取项目名称
        configDTO.setProjectName(build.getProject().getFullDisplayName());
        // 获取控制台地址
        configDTO.setConsoleUrl(JenkinsUtil.getConsoleUrl(build));
        // 预计用时
        configDTO.setDurationTime(build.getProject().getEstimatedDuration());
        return configDTO;
    }
    
    /**
     * 构建结束阶段，向配置传输对象添加 jenkins 相关配置
     */
    public static ConfigDTO configJenkinsEnvParamWhenBuildOver(@Nonnull ConfigDTO configDTO, AbstractBuild<?, ?> build) {
        // 获取项目名称
        configDTO.setProjectName(build.getProject().getFullDisplayName());
        // 获取控制台地址
        configDTO.setConsoleUrl(JenkinsUtil.getConsoleUrl(build));
        // 构建用时
        configDTO.setDurationTime(build.getTimeInMillis() - build.getStartTimeInMillis());
        // 构建结果
        configDTO.setBuildResult(build.getResult());
        return configDTO;
    }
    
    /**
     * 将使用 "," 分隔的字符串结构化为 set
     */
    private static Set<String> convertParamStrToSet(String str) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isEmpty(str)) {
            return result;
        }
        String[] subs = str.split(",");
        if (ArrayUtils.isEmpty(subs)) {
            return result;
        }
        return Arrays.stream(subs).map(String::trim).filter(e -> StringUtils.isNotEmpty(e) && !"null".equals(e)).collect(Collectors.toSet());
    }
    
    /**
     * 合并获取环境名称，顺序：job设置 -> global设置 -> 默认文本
     */
    private static String mergeTopicName(@Nonnull NotifierDescriptor descriptor, @Nonnull WechatBotNotifier notifier) {
        if (StringUtils.isNotEmpty(notifier.getTopicName())) {
            return notifier.getTopicName();
        }
        if (StringUtils.isNoneEmpty(descriptor.getTopicName())) {
            return descriptor.getTopicName();
        }
        return "测试环境";
    }
    
    /**
     * 设置 jenkins 构建发起人通知
     */
    private static ConfigDTO configNotifyCauseUser(@Nonnull ConfigDTO config, @Nonnull NotifierDescriptor descriptor, Cause.UserIdCause cause ){
        if (!descriptor.isNotifyCauseUser() || cause == null) {
            return config;
        }
        // 获取构建者信息
        String userId = cause.getUserId();
        String userName = cause.getUserName();
        // 映射转换
        Map<String, String> userIdMap = descriptor.getUserIdMobileMap();
        Map<String, String> userNameMap = descriptor.getUserNameMobileMap();
        // cause user id 映射
        if (StringUtils.isNotEmpty(userId) && MapUtils.isNotEmpty(userIdMap)) {
            if (StringUtils.isNotEmpty(userIdMap.get(userId))) {
                config.getMemberMobiles().add(userIdMap.get(userId));
            }
        }
        // cause user mobile 映射
        if (StringUtils.isNotEmpty(userName) && MapUtils.isNotEmpty(userNameMap)) {
            if (StringUtils.isNotEmpty(userNameMap.get(userName))) {
                config.getMemberMobiles().add(userNameMap.get(userName));
            }
        }
        return config;
    }
    
    
}
