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
package me.zhengjie.modules.security.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.modules.security.config.bean.SecurityProperties;
import me.zhengjie.modules.security.security.JwtAccessDeniedHandler;
import me.zhengjie.modules.security.security.JwtAuthenticationEntryPoint;
import me.zhengjie.modules.security.security.TokenConfigurer;
import me.zhengjie.modules.security.security.TokenProvider;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.UserCacheManager;
import me.zhengjie.utils.enums.RequestMethodEnum;

/**
 * @author arming
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfiguration {

  private final TokenProvider tokenProvider;
  private final CorsFilter corsFilter;
  private final JwtAuthenticationEntryPoint authenticationErrorHandler;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final SecurityProperties properties;
  private final OnlineUserService onlineUserService;
  private final UserCacheManager userCacheManager;
  private final ApplicationContext applicationContext;

  @Bean
  GrantedAuthorityDefaults grantedAuthorityDefaults() {
    // 去除 ROLE_ 前缀
    return new GrantedAuthorityDefaults("");
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    // 密码加密方式
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext
        .getBean("requestMappingHandlerMapping");
    Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
    // 获取匿名标记
    Map<String, Set<String>> anonymousUrls = getAnonymousUrl(handlerMethodMap);
    http.csrf(csrf -> csrf.disable()).addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
        // 授权异常
        .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationErrorHandler)
            .accessDeniedHandler(jwtAccessDeniedHandler))
        .headers(headers -> headers.frameOptions().disable())
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests -> requests
            // 静态资源等等
            .requestMatchers(HttpMethod.GET, "/*.html", "/*/*.html", "/*/*.css", "/*/*.js", "/webSocket/*")
            .permitAll()
            // swagger 文档
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/swagger-ui/*").permitAll()
            .requestMatchers("/swagger-resources/*").permitAll()
            .requestMatchers("/webjars/*").permitAll()
            .requestMatchers("/*/api-docs").permitAll()
            .requestMatchers("/*/api-docs/*").permitAll()
            // 文件
            .requestMatchers("/avatar/*").permitAll()
            .requestMatchers("/file/*").permitAll()
            // 阿里巴巴 druid
            .requestMatchers("/druid/*").permitAll()
            // 放行OPTIONS请求
            .requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()
            // 自定义匿名访问所有url放行：允许匿名和带Token访问，细腻化到每个 Request 类型
            // GET
            .requestMatchers(HttpMethod.GET, anonymousUrls.get(RequestMethodEnum.GET.getType()).toArray(new String[0]))
            .permitAll()
            // POST
            .requestMatchers(HttpMethod.POST,
                anonymousUrls.get(RequestMethodEnum.POST.getType()).toArray(new String[0]))
            .permitAll()
            // PUT
            .requestMatchers(HttpMethod.PUT, anonymousUrls.get(RequestMethodEnum.PUT.getType()).toArray(new String[0]))
            .permitAll()
            // PATCH
            .requestMatchers(HttpMethod.PATCH,
                anonymousUrls.get(RequestMethodEnum.PATCH.getType()).toArray(new String[0]))
            .permitAll()
            // DELETE
            .requestMatchers(HttpMethod.DELETE,
                anonymousUrls.get(RequestMethodEnum.DELETE.getType()).toArray(new String[0]))
            .permitAll()
            // 所有类型的接口都放行
            .requestMatchers(anonymousUrls.get(RequestMethodEnum.ALL.getType()).toArray(new String[0])).permitAll()
            // 所有请求都需要认证
            .anyRequest().authenticated())
        .apply(securityConfigurerAdapter());

    return http.build();
  }

  private TokenConfigurer securityConfigurerAdapter() {
    return new TokenConfigurer(tokenProvider, properties, onlineUserService, userCacheManager);
  }

  private Map<String, Set<String>> getAnonymousUrl(Map<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
    Map<String, Set<String>> anonymousUrls = new HashMap<>(8);
    Set<String> get = new HashSet<>();
    Set<String> post = new HashSet<>();
    Set<String> put = new HashSet<>();
    Set<String> patch = new HashSet<>();
    Set<String> delete = new HashSet<>();
    Set<String> all = new HashSet<>();
    for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
      HandlerMethod handlerMethod = infoEntry.getValue();
      AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
      if (null != anonymousAccess) {
        List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
        RequestMethodEnum request = RequestMethodEnum
            .find(requestMethods.size() == 0 ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
        switch (Objects.requireNonNull(request)) {
        case GET:
          get.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        case POST:
          post.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        case PUT:
          put.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        case PATCH:
          patch.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        case DELETE:
          delete.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        default:
          all.addAll(infoEntry.getKey().getPathPatternsCondition().getPatternValues());
          break;
        }
      }
    }
    anonymousUrls.put(RequestMethodEnum.GET.getType(), get);
    anonymousUrls.put(RequestMethodEnum.POST.getType(), post);
    anonymousUrls.put(RequestMethodEnum.PUT.getType(), put);
    anonymousUrls.put(RequestMethodEnum.PATCH.getType(), patch);
    anonymousUrls.put(RequestMethodEnum.DELETE.getType(), delete);
    anonymousUrls.put(RequestMethodEnum.ALL.getType(), all);
    return anonymousUrls;
  }
}
