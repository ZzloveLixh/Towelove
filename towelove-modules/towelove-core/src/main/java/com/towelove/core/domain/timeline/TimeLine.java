package com.towelove.core.domain.timeline;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Time;

/**
 * @author: 张锦标
 * @date: 2023/5/11 20:37
 * TimeLine类
 */
@Data
@TableName("time_line")
public class TimeLine {
    private Long id;
    private Long loveAlbumId;
    private String title;
    private Time createTime;
    @TableLogic(value = "0", delval = "1")
    private Boolean deleted;
}
