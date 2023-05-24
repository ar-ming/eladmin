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
package me.zhengjie.modules.system.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

/**
 * @author Zheng Jie
 * @date 2018-12-17
 */
@Entity
@Getter
@Setter
@Table(name = "sys_menu")
public class Menu extends BaseEntity implements Serializable {

    @Id
    @Column(name = "menu_id")
    @NotNull(groups = {Update.class})
    @Schema(title = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "menus")
    @Schema(title = "菜单角色")
    private Set<Role> roles;

    @Schema(title = "菜单标题")
    private String title;

    @Column(name = "name")
    @Schema(title = "菜单组件名称")
    private String componentName;

    @Schema(title = "排序")
    private Integer menuSort = 999;

    @Schema(title = "组件路径")
    private String component;

    @Schema(title = "路由地址")
    private String path;

    @Schema(title = "菜单类型，目录、菜单、按钮")
    private Integer type;

    @Schema(title = "权限标识")
    private String permission;

    @Schema(title = "菜单图标")
    private String icon;

    @Column(columnDefinition = "bit(1) default 0")
    @Schema(title = "缓存")
    private Boolean cache;

    @Column(columnDefinition = "bit(1) default 0")
    @Schema(title = "是否隐藏")
    private Boolean hidden;

    @Schema(title = "上级菜单")
    private Long pid;

    @Schema(title = "子节点数目", hidden = true)
    private Integer subCount = 0;

    @Schema(title = "外链菜单")
    private Boolean iFrame;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
