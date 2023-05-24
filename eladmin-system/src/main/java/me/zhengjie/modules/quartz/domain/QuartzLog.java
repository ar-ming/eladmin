/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.quartz.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Entity 
@Data
@Table(name = "sys_quartz_log")
public class QuartzLog implements Serializable {

    @Id
    @Column(name = "log_id")
    @Schema(title = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(title = "任务名称", hidden = true)
    private String jobName;

    @Schema(title = "bean名称", hidden = true)
    private String beanName;

    @Schema(title = "方法名称", hidden = true)
    private String methodName;

    @Schema(title = "参数", hidden = true)
    private String params;

    @Schema(title = "cron表达式", hidden = true)
    private String cronExpression;

    @Schema(title = "状态", hidden = true)
    private Boolean isSuccess;

    @Schema(title = "异常详情", hidden = true)
    private String exceptionDetail;

    @Schema(title = "执行耗时", hidden = true)
    private Long time;

    @CreationTimestamp
    @Schema(title = "创建时间", hidden = true)
    private Timestamp createTime;
}
