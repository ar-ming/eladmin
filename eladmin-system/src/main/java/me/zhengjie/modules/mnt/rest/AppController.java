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
import me.zhengjie.modules.mnt.domain.App;
import me.zhengjie.modules.mnt.service.AppService;
import me.zhengjie.modules.mnt.service.dto.AppQueryCriteria;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "运维：应用管理")
@RequestMapping("/api/app")
public class AppController {

  private final AppService appService;

  @Operation(summary = "导出应用数据")
  @GetMapping(value = "/download")
  @PreAuthorize("@el.check('app:list')")
  public void exportApp(HttpServletResponse response, AppQueryCriteria criteria) throws IOException {
    appService.download(appService.queryAll(criteria), response);
  }

  @Operation(summary = "查询应用")
  @GetMapping
  @PreAuthorize("@el.check('app:list')")
  public ResponseEntity<Object> queryApp(AppQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity<>(appService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @Log("新增应用")
  @Operation(summary = "新增应用")
  @PostMapping
  @PreAuthorize("@el.check('app:add')")
  public ResponseEntity<Object> createApp(@Validated @RequestBody App resources) {
    appService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log("修改应用")
  @Operation(summary = "修改应用")
  @PutMapping
  @PreAuthorize("@el.check('app:edit')")
  public ResponseEntity<Object> updateApp(@Validated @RequestBody App resources) {
    appService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log("删除应用")
  @Operation(summary = "删除应用")
  @DeleteMapping
  @PreAuthorize("@el.check('app:del')")
  public ResponseEntity<Object> deleteApp(@RequestBody Set<Long> ids) {
    appService.delete(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
