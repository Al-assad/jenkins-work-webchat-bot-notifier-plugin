package org.jenkins.plugins.wechatbot.sender;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hudson.model.BuildListener;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jenkins.plugins.wechatbot.dto.ConfigDTO;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 企业微信群机器人消息推送
 *
 * @author yulinying
 * @since 2019-11-07
 */
public abstract class BotSender {
    
    private ConfigDTO configDTO;
    private BuildListener listener;
    
    /**
     * 获取构建开始阶段的消息发送对象
     *
     * @param configDTO
     * @param listener
     * @return
     */
    public static BotSender ofBuildStart(ConfigDTO configDTO, BuildListener listener) {
        BotSender sender = new StartBuildBotSender();
        sender.setConfigDTO(configDTO);
        sender.setListener(listener);
        return sender;
    }
    
    /**
     * 获取构建结束阶段的消息发送对象
     *
     * @param configDTO
     * @param listener
     * @return
     */
    public static BotSender ofBuildOver(ConfigDTO configDTO, BuildListener listener) {
        BotSender sender = new OverBuildBotSender();
        sender.setConfigDTO(configDTO);
        sender.setListener(listener);
        return sender;
    }
    
    /**
     * 发送消息
     */
    public void send() {
        if (configDTO == null || CollectionUtils.isEmpty(configDTO.getWebhookUrls())) {
            return;
        }
        // 构建发送内容
        String jenkinsStatusContent = genJenkinsBuildStatusJsonContent();
        if (StringUtils.isEmpty(jenkinsStatusContent)) {
            return;
        }
        String atContent = genAtJsonContent();
        boolean sendAtMsg = StringUtils.isNotEmpty(atContent);
        // 串行发送消息
        for (String webhookUrl : configDTO.getWebhookUrls()) {
            try {
                sendHttp(webhookUrl, jenkinsStatusContent);
                if (sendAtMsg){
                    sendHttp(webhookUrl, atContent);
                }
            } catch (IOException e) {
                getListener().getLogger().println("企业微信群机器人消息发送失败" + e.getMessage());
            }
        }
    }
    
    /**
     * 构建状态 json markdown 正文
     */
    protected abstract String genBuildStatusMarkdownContent();
    
    /**
     * 获取 jenkins 构建状态 json 正文
     */
    private String genJenkinsBuildStatusJsonContent(){
        JSONObject json = new JSONObject();
        json.put("msgtype", "markdown");
        JSONObject markdown = new JSONObject();
        markdown.put("content", genBuildStatusMarkdownContent());
        json.put("markdown", markdown);
        return json.toJSONString();
    }
    
    /**
     * 获取 @ 人信息 json 正文
     */
    private String genAtJsonContent(){
        if (CollectionUtils.isEmpty(configDTO.getMemberIds()) && CollectionUtils.isEmpty(configDTO.getMemberMobiles())) {
            return null;
        }
        JSONObject text = new JSONObject();
        text.put("mentioned_list",new JSONArray(new ArrayList<>(configDTO.getMemberIds())));
        text.put("mentioned_mobile_list", new JSONArray(new ArrayList<>(configDTO.getMemberMobiles())));
        JSONObject json = new JSONObject();
        json.put("msgtype", "text");
        json.put("text", text);
        return json.toJSONString();
    }
    
    /**
     * 发送 http 请求
     */
    protected void sendHttp(@Nonnull String url, @Nonnull String content) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody postBody = RequestBody.create(content, MediaType.parse("application/json;charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(postBody)
                .build();
        ResponseBody responseBody = httpClient.newCall(request).execute().body();
        if (responseBody != null) {
            responseBody.close();
        }
    }
    

    
    public ConfigDTO getConfigDTO() {
        return configDTO;
    }
    
    public void setConfigDTO(ConfigDTO configDTO) {
        this.configDTO = configDTO;
    }
    
    public BuildListener getListener() {
        return listener;
    }
    
    public void setListener(BuildListener listener) {
        this.listener = listener;
    }
}
