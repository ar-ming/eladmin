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
package me.zhengjie.modules.system.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.Dict;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.dto.RoleDto;
import me.zhengjie.modules.system.service.dto.RoleQueryCriteria;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.utils.SecurityUtils;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "系统：角色管理")
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    private static final String ENTITY_NAME = "role";

    @Operation(summary="获取单个role")
    @GetMapping(value = "/{id}")
    @PreAuthorize("@el.check('roles:list')")
    public ResponseEntity<Object> findRoleById(@PathVariable Long id){
        return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
    }

    @Operation(summary="导出角色数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('role:list')")
    public void exportRole(HttpServletResponse response, RoleQueryCriteria criteria) throws IOException {
        roleService.download(roleService.queryAll(criteria), response);
    }

    @Operation(summary="返回全部的角色")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('roles:list','user:add','user:edit')")
    public ResponseEntity<Object> queryAllRole(){
        return new ResponseEntity<>(roleService.queryAll(),HttpStatus.OK);
    }

    @Operation(summary="查询角色")
    @GetMapping
    @PreAuthorize("@el.check('roles:list')")
    public ResponseEntity<Object> queryRole(RoleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(roleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Operation(summary="获取用户级别")
    @GetMapping(value = "/level")
    public ResponseEntity<Object> getRoleLevel(){
        return new ResponseEntity<>(Dict.create().set("level", getLevels(null)),HttpStatus.OK);
    }

    @Log("新增角色")
    @Operation(summary="新增角色")
    @PostMapping
    @PreAuthorize("@el.check('roles:add')")
    public ResponseEntity<Object> createRole(@Validated @RequestBody Role resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        getLevels(resources.getLevel());
        roleService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改角色")
    @Operation(summary="修改角色")
    @PutMapping
    @PreAuthorize("@el.check('roles:edit')")
    public ResponseEntity<Object> updateRole(@Validated(Role.Update.class) @RequestBody Role resources){
        getLevels(resources.getLevel());
        roleService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("修改角色菜单")
    @Operation(summary="修改角色菜单")
    @PutMapping(value = "/menu")
    @PreAuthorize("@el.check('roles:edit')")
    public ResponseEntity<Object> updateRoleMenu(@RequestBody Role resources){
        RoleDto role = roleService.findById(resources.getId());
        getLevels(role.getLevel());
        roleService.updateMenu(resources,role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除角色")
    @Operation(summary="删除角色")
    @DeleteMapping
    @PreAuthorize("@el.check('roles:del')")
    public ResponseEntity<Object> deleteRole(@RequestBody Set<Long> ids){
        for (Long id : ids) {
            RoleDto role = roleService.findById(id);
            getLevels(role.getLevel());
        }
        // 验证是否被用户关联
        roleService.verification(ids);
        roleService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 获取用户的角色级别
     * @return /
     */
    private int getLevels(Integer level){
        List<Integer> levels = roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if(level != null){
            if(level < min){
                throw new BadRequestException("权限不足，你的角色级别：" + min + "，低于操作的角色级别：" + level);
            }
        }
        return min;
    }
}
