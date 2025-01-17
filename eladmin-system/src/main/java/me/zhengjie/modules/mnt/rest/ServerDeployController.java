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
package me.zhengjie.modules.mnt.rest;

import java.io.IOException;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.mnt.domain.ServerDeploy;
import me.zhengjie.modules.mnt.service.ServerDeployService;
import me.zhengjie.modules.mnt.service.dto.ServerDeployQueryCriteria;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@RestController
@Tag(name = "运维：服务器管理")
@RequiredArgsConstructor
@RequestMapping("/api/serverDeploy")
public class ServerDeployController {

    private final ServerDeployService serverDeployService;

    @Operation(summary="导出服务器数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('serverDeploy:list')")
    public void exportServerDeploy(HttpServletResponse response, ServerDeployQueryCriteria criteria) throws IOException {
        serverDeployService.download(serverDeployService.queryAll(criteria), response);
    }

    @Operation(summary= "查询服务器")
    @GetMapping
	@PreAuthorize("@el.check('serverDeploy:list')")
    public ResponseEntity<Object> queryServerDeploy(ServerDeployQueryCriteria criteria, Pageable pageable){
    	return new ResponseEntity<>(serverDeployService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增服务器")
    @Operation(summary= "新增服务器")
    @PostMapping
	@PreAuthorize("@el.check('serverDeploy:add')")
    public ResponseEntity<Object> createServerDeploy(@Validated @RequestBody ServerDeploy resources){
        serverDeployService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改服务器")
    @Operation(summary= "修改服务器")
    @PutMapping
	@PreAuthorize("@el.check('serverDeploy:edit')")
    public ResponseEntity<Object> updateServerDeploy(@Validated @RequestBody ServerDeploy resources){
        serverDeployService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除服务器")
    @Operation(summary= "删除Server")
	@DeleteMapping
	@PreAuthorize("@el.check('serverDeploy:del')")
    public ResponseEntity<Object> deleteServerDeploy(@RequestBody Set<Long> ids){
        serverDeployService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@Log("测试连接服务器")
	@Operation(summary= "测试连接服务器")
	@PostMapping("/testConnect")
	@PreAuthorize("@el.check('serverDeploy:add')")
	public ResponseEntity<Object> testConnectServerDeploy(@Validated @RequestBody ServerDeploy resources){
		return new ResponseEntity<>(serverDeployService.testConnect(resources),HttpStatus.CREATED);
	}
}
