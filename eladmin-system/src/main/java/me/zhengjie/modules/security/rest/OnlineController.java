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
package me.zhengjie.modules.security.rest;

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
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.utils.EncryptUtils;

/**
 * @author Zheng Jie
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/online")
@Tag(name = "系统：在线用户管理")
public class OnlineController {

    private final OnlineUserService onlineUserService;

    @Operation(summary = "查询在线用户")
    @GetMapping
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> queryOnlineUser(String filter, Pageable pageable){
        return new ResponseEntity<>(onlineUserService.getAll(filter, pageable),HttpStatus.OK);
    }

    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check()")
    public void exportOnlineUser(HttpServletResponse response, String filter) throws IOException {
        onlineUserService.download(onlineUserService.getAll(filter), response);
    }

    @Operation(summary = "踢出用户")
    @DeleteMapping
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> deleteOnlineUser(@RequestBody Set<String> keys) throws Exception {
        for (String key : keys) {
            // 解密Key
            key = EncryptUtils.desDecrypt(key);
            onlineUserService.kickOut(key);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
