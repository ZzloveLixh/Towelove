package com.towelove.system.domain.mail.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 邮箱账号的精简 Response VO")
@Data
public class MailAccountSimpleRespVO {

    @Schema(description = "邮箱编号", required = true, example = "1024")
    private Long id;

    @Schema(description = "邮箱", required = true, example = "460219753@qq.com")
    private String mail;

}