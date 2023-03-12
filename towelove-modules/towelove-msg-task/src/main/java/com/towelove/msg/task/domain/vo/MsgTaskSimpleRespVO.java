package com.towelove.msg.task.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @author: 张锦标
 * @date: 2023/3/11 17:32
 */
@Schema(description = "消息的精简 Response VO")
@Data
public class MsgTaskSimpleRespVO {

    @Schema(description = "消息编号", required = true, example = "1024")
    private Long id;

    @Schema(description = "用户编号", required = true, example = "1024")
    private Long userId;

    @Schema(description = "邮件账号编号", required = true, example = "1024")
    private Long accountId;

    @Schema(description = "模板编号", required = true, example = "1024")
    private Long templateId;


}