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
package me.zhengjie.domain;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 七牛云对象存储配置类
 * @author Zheng Jie
 * @date 2018-12-31
 */
@Data
@Entity
@Table(name = "tool_qiniu_config")
public class QiniuConfig implements Serializable {

    @Id
    @Column(name = "config_id")
    @Schema(title = "ID")
    private Long id;

    @NotBlank
    @Schema(title = "accessKey")
    private String accessKey;

    @NotBlank
    @Schema(title = "secretKey")
    private String secretKey;

    @NotBlank
    @Schema(title = "存储空间名称作为唯一的 Bucket 识别符")
    private String bucket;

    /**
     * Zone表示与机房的对应关系
     * 华东	Zone.zone0()
     * 华北	Zone.zone1()
     * 华南	Zone.zone2()
     * 北美	Zone.zoneNa0()
     * 东南亚	Zone.zoneAs0()
     */
    @NotBlank
    @Schema(title = "Zone表示与机房的对应关系")
    private String zone;

    @NotBlank
    @Schema(title = "外链域名，可自定义，需在七牛云绑定")
    private String host;

    @Schema(title = "空间类型：公开/私有")
    private String type = "公开";
}
