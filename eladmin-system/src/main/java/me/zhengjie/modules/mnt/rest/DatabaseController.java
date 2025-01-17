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

import java.io.File;
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
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.mnt.domain.Database;
import me.zhengjie.modules.mnt.service.DatabaseService;
import me.zhengjie.modules.mnt.service.dto.DatabaseDto;
import me.zhengjie.modules.mnt.service.dto.DatabaseQueryCriteria;
import me.zhengjie.modules.mnt.util.SqlUtils;
import me.zhengjie.utils.FileUtil;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@Tag(name = "运维：数据库管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/database")
public class DatabaseController {

	private final String fileSavePath = FileUtil.getTmpDirPath()+"/";
    private final DatabaseService databaseService;

	@Operation(summary="导出数据库数据")
	@GetMapping(value = "/download")
	@PreAuthorize("@el.check('database:list')")
	public void exportDatabase(HttpServletResponse response, DatabaseQueryCriteria criteria) throws IOException {
		databaseService.download(databaseService.queryAll(criteria), response);
	}

    @Operation(summary= "查询数据库")
    @GetMapping
	@PreAuthorize("@el.check('database:list')")
    public ResponseEntity<Object> queryDatabase(DatabaseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(databaseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增数据库")
    @Operation(summary= "新增数据库")
    @PostMapping
	@PreAuthorize("@el.check('database:add')")
    public ResponseEntity<Object> createDatabase(@Validated @RequestBody Database resources){
		databaseService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改数据库")
    @Operation(summary= "修改数据库")
    @PutMapping
	@PreAuthorize("@el.check('database:edit')")
    public ResponseEntity<Object> updateDatabase(@Validated @RequestBody Database resources){
        databaseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除数据库")
    @Operation(summary= "删除数据库")
    @DeleteMapping
	@PreAuthorize("@el.check('database:del')")
    public ResponseEntity<Object> deleteDatabase(@RequestBody Set<String> ids){
        databaseService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@Log("测试数据库链接")
	@Operation(summary= "测试数据库链接")
	@PostMapping("/testConnect")
	@PreAuthorize("@el.check('database:testConnect')")
	public ResponseEntity<Object> testConnect(@Validated @RequestBody Database resources){
		return new ResponseEntity<>(databaseService.testConnection(resources),HttpStatus.CREATED);
	}

	@Log("执行SQL脚本")
	@Operation(summary= "执行SQL脚本")
	@PostMapping(value = "/upload")
	@PreAuthorize("@el.check('database:add')")
	public ResponseEntity<Object> uploadDatabase(@RequestBody MultipartFile file, HttpServletRequest request)throws Exception{
		String id = request.getParameter("id");
		DatabaseDto database = databaseService.findById(id);
		String fileName;
		if(database != null){
			fileName = file.getOriginalFilename();
			File executeFile = new File(fileSavePath+fileName);
			FileUtil.del(executeFile);
			file.transferTo(executeFile);
			String result = SqlUtils.executeFile(database.getJdbcUrl(), database.getUserName(), database.getPwd(), executeFile);
			return new ResponseEntity<>(result,HttpStatus.OK);
		}else{
			throw new BadRequestException("Database not exist");
		}
	}
}
