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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.mnt.service.DeployHistoryService;
import me.zhengjie.modules.mnt.service.dto.DeployHistoryQueryCriteria;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@RestController
@RequiredArgsConstructor
@Tag(name = "运维：部署历史管理")
@RequestMapping("/api/deployHistory")
public class DeployHistoryController {

    private final DeployHistoryService deployhistoryService;

    @Operation(summary="导出部署历史数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deployHistory:list')")
    public void exportDeployHistory(HttpServletResponse response, DeployHistoryQueryCriteria criteria) throws IOException {
        deployhistoryService.download(deployhistoryService.queryAll(criteria), response);
    }

    @Operation(summary= "查询部署历史")
    @GetMapping
	@PreAuthorize("@el.check('deployHistory:list')")
    public ResponseEntity<Object> queryDeployHistory(DeployHistoryQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deployhistoryService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("删除DeployHistory")
    @Operation(summary= "删除部署历史")
	@DeleteMapping
    @PreAuthorize("@el.check('deployHistory:del')")
    public ResponseEntity<Object> deleteDeployHistory(@RequestBody Set<String> ids){
        deployhistoryService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
