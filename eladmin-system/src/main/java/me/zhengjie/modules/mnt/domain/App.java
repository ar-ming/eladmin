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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@Entity
@Getter
@Setter
@Table(name="mnt_app")
public class App extends BaseEntity implements Serializable {

    @Id
	@Column(name = "app_id")
	@Schema(title = "ID", hidden = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Schema(title = "名称")
    private String name;

	@Schema(title = "端口")
	private int port;

	@Schema(title = "上传路径")
	private String uploadPath;

	@Schema(title = "部署路径")
	private String deployPath;

	@Schema(title = "备份路径")
	private String backupPath;

	@Schema(title = "启动脚本")
	private String startScript;

	@Schema(title = "部署脚本")
	private String deployScript;

    public void copy(App source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
