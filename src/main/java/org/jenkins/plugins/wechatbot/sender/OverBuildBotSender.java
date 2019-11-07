package org.jenkins.plugins.wechatbot.sender;

import hudson.model.Result;
import org.jenkins.plugins.wechatbot.dto.ConfigDTO;

import javax.annotation.Nonnull;

/**
 * jenkins 结束构建消息发送器
 *
 * @author yulinying
 * @since 2019-11-06
 */
public class OverBuildBotSender extends BotSender{
    
    @Override
    protected String genBuildStatusMarkdownContent() {
        ConfigDTO config = getConfigDTO();
        StringBuilder content = new StringBuilder();
        content.append("Jenkins流水线构建通知").append("\n")
                .append(String.format(">任务：**%s**", config.getProjectName()))
                .append("\n")
                .append(getBuildResultContent(config))
                .append("\n")
                .append(String.format(">环境：<font color=\"comment\">%s</font>", config.getTopicName()))
                .append("\n")
                .append(String.format(">构建用时：<font color=\"comment\">%s</font>", getRunTime(config)))
                .append("\n")
                .append(String.format(">[查看日志](%s)", config.getConsoleUrl()));
        return content.toString();
    }
    
    /**
     * 获取构建结果文本
     */
    private String getBuildResultContent(ConfigDTO config){
        if (Result.SUCCESS.equals(config.getBuildResult())){
            return ">状态：<font color=\"info\">构建成功</font>";
        } else {
            return ">状态：<font color=\"warning\">构建失败</font>";
        }
    }
    
    /**
     * 获取运行时间文本
     */
    private String getRunTime(@Nonnull ConfigDTO config) {
        if (config.getDurationTime() == 0) {
            return "瞬间完成";
        }
        long time = config.getDurationTime() / (1000 * 60);
        if (time == 0) {
            return config.getDurationTime() / 1000 + "秒";
        } else {
            return time + "分钟";
        }
    }


}
