package com.towelove.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSendMessage {

    /**
     * 邮件日志编号
     */
    @NotNull(message = "邮件日志编号不能为空")
    private Long logId;
    /**
     * 接收邮件地址
     */
    @NotNull(message = "接收邮件地址不能为空")
    private String mail;
    /**
     * 邮件账号编号
     */
    @NotNull(message = "邮件账号编号不能为空")
    private Long accountId;

    /**
     * 邮件发件人
     */
    private String nickname;
    /**
     * 邮件标题
     */
    @NotEmpty(message = "邮件标题不能为空")
    private String title;
    /**
     * 邮件内容
     */
    @NotEmpty(message = "邮件内容不能为空")
    private String content;

    private Boolean isHtml;

    private File[] files;

}
