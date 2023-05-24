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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Getter
@Setter
@Entity
@Table(name = "sys_quartz_job")
public class QuartzJob extends BaseEntity implements Serializable {

    public static final String JOB_KEY = "JOB_KEY";

    @Id
    @Column(name = "job_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    @Schema(title = "用于子任务唯一标识", hidden = true)
    private String uuid;

    @Schema(title = "定时器名称")
    private String jobName;

    @NotBlank
    @Schema(title = "Bean名称")
    private String beanName;

    @NotBlank
    @Schema(title = "方法名称")
    private String methodName;

    @Schema(title = "参数")
    private String params;

    @NotBlank
    @Schema(title = "cron表达式")
    private String cronExpression;

    @Schema(title = "状态，暂时或启动")
    private Boolean isPause = false;

    @Schema(title = "负责人")
    private String personInCharge;

    @Schema(title = "报警邮箱")
    private String email;

    @Schema(title = "子任务")
    private String subTask;

    @Schema(title = "失败后暂停")
    private Boolean pauseAfterFailure;

    @NotBlank
    @Schema(title = "备注")
    private String description;
}