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
import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 上传成功后，存储结果
 * @author Zheng Jie
 * @date 2018-12-31
 */
@Data
@Entity
@Table(name = "tool_qiniu_content")
public class QiniuContent implements Serializable {

    @Id
    @Column(name = "content_id")
    @Schema(title = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Schema(title = "文件名")
    private String key;

    @Schema(title = "空间名")
    private String bucket;

    @Schema(title = "大小")
    private String size;

    @Schema(title = "文件地址")
    private String url;

    @Schema(title = "文件类型")
    private String suffix;

    @Schema(title = "空间类型：公开/私有")
    private String type = "公开";

    @UpdateTimestamp
    @Schema(title = "创建或更新时间")
    @Column(name = "update_time")
    private Timestamp updateTime;
}
