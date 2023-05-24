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
package me.zhengjie.modules.mnt.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@Entity
@Getter
@Setter
@Table(name = "mnt_deploy_history")
public class DeployHistory implements Serializable {

  @Id
  @Column(name = "history_id")
  @Schema(title = "ID", hidden = true)
  private String id;

  @Schema(title = "应用名称")
  private String appName;

  @Schema(title = "IP")
  private String ip;

  @CreationTimestamp
  @Schema(title = "部署时间")
  private Timestamp deployDate;

  @Schema(title = "部署者")
  private String deployUser;

  @Schema(title = "部署ID")
  private Long deployId;

  public void copy(DeployHistory source) {
    BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
  }
}
