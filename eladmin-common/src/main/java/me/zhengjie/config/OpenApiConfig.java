/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhengjie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @author arming
 *
 */
@Configuration
public class OpenApiConfig {
  
  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${swagger.enabled}")
  private Boolean enabled;
  
  @Bean
  OpenAPI springDocOpenAPI() {
    // 配置Authorizations
    return new OpenAPI()
        .info(new Info()
            .title("ELADMIN 接口文档")
            .description("一个简单且易上手的 Spring boot 后台管理框架")
            .version("v2.7"))
        .externalDocs(new ExternalDocumentation()
            .description("github地址")
            .url("https://github.com/ar-ming/eladmin.git"))
        // 配置Authorizations
        .components(new Components()
            .addSecuritySchemes(tokenHeader, new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name(tokenHeader)
                .in(SecurityScheme.In.HEADER)))
         .addSecurityItem(new SecurityRequirement().addList(tokenHeader))
         ;
  }
}
