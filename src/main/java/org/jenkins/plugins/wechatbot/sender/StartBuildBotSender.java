package org.jenkins.plugins.wechatbot.sender;


import org.jenkins.plugins.wechatbot.dto.ConfigDTO;

import javax.annotation.Nonnull;

/**
 * jenkins 开始构建阶段消息发送器
 *
 * @author yulinying
 * @since 2019-11-06
 */
public class StartBuildBotSender extends BotSender {
    
    @Override
    protected String genBuildStatusMarkdownContent() {
        ConfigDTO config = getConfigDTO();
        StringBuilder content = new StringBuilder();
        content.append("Jenkins流水线构建通知").append("\n")
                .append(String.format(">任务：**%s**", config.getProjectName()))
                .append("\n")
                .append(">状态：<font color=\"info\">开始构建</font>")
                .append("\n")
                .append(String.format(">环境：<font color=\"comment\">%s</font>", config.getTopicName()))
                .append("\n")
                .append(String.format(">预计用时：<font color=\"comment\">%s</font>", getEstimatedTime(config)))
                .append("\n")
                .append(String.format(">[查看日志](%s)", config.getConsoleUrl()));
        return content.toString();
    }
    
    /**
     * 获取预估时间文本
     */
    private String getEstimatedTime(@Nonnull ConfigDTO config) {
        if (config.getDurationTime() == 0) {
            return "快了";
        }
        long time = config.getDurationTime() / (1000 * 60);
        if (time == 0) {
            return config.getDurationTime() / 1000 + "秒";
        } else {
            return time + "分钟";
        }
    }
}
