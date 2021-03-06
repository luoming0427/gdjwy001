package com.hirisun.cloud.user.controller.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hirisun.cloud.common.annotation.LoginUser;
import com.hirisun.cloud.common.annotation.support.LoginUserHandlerMethodArgumentResolver;
import com.hirisun.cloud.common.util.AreaPoliceCategoryUtils;
import com.hirisun.cloud.common.vo.QueryResponseResult;
import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.model.workflow.WorkflowNodeVO;
import com.hirisun.cloud.user.bean.SysRole;
import com.hirisun.cloud.user.bean.User;
import com.hirisun.cloud.user.bean.UserRole;
import com.hirisun.cloud.user.service.SysRoleService;
import com.hirisun.cloud.user.service.UserRoleService;
import com.hirisun.cloud.user.service.UserService;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuxiaoxing
 * @since 2020-07-24
 */
@Api(tags = "用户后台管理")
@RestController
@RequestMapping("/user/userManage")
public class UserManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询列表
     *
     * @return TOTO 优化sql查询
     */
    @ApiOperation("用户列表分页查询")
    @GetMapping("/page")
    public QueryResponseResult<User> page(
            @ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @ApiParam("关键字") @RequestParam(required = false) String name,
            @ApiParam("用户类型  0：普通用户 1: 服务厂商 20:租户管理员  100：管理用户") @RequestParam(required = false) String type) {
        IPage<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        Map map = new HashMap<>();
        map.put("name", name);
        map.put("type", type);
        page = userService.getPage(page, map);
        return QueryResponseResult.success(page);
    }

    /**
     * 获取用户拥有的角色、系统所有角色
     *
     * @param userId
     * @return
     */
    @ApiOperation("根据用户身份证获取用户角色")
    @GetMapping("/getRoleByUserId")
    public QueryResponseResult<SysRole> getRoleByUserId(@ApiParam(value = "用户身份证", required = true) @RequestParam String userId) {
        List<SysRole> sysRoleList = sysRoleService.list();
        List<SysRole> roleList = new ArrayList<>();
        List<UserRole> userRoleList = userRoleService.list(new QueryWrapper<UserRole>().lambda().select(UserRole::getRoleId).eq(UserRole::getUserId, userId));
        userRoleList.forEach(userRole -> {
            sysRoleList.forEach(sysRole -> {
                if (userRole.getRoleId() != null && userRole.getRoleId().equals(sysRole.getId())) {
                    roleList.add(sysRole);
                    return;
                }
            });
        });
        return QueryResponseResult.success(roleList);
    }

    @ApiOperation("修改用户角色")
    @PostMapping("/editRoleByUserId")
    public QueryResponseResult<SysRole> editRoleByUserId(
            @ApiParam(value = "用户身份证", required = true) @RequestParam String userId,
            @ApiParam(value = "角色id,多个使用逗号(,)分隔", required = true) @RequestParam String roleId) {
        /*
         * 1.移除用户角色
         * 2.批量保存用户角色
         */
        userRoleService.remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, userId));
        List<UserRole> userRoleList = new ArrayList<>();
        String[] userRoles = roleId.split(",");
        for (String role : userRoles) {
            UserRole userRole = new UserRole();
            userRole.setRoleId(role);
            userRole.setUserId(userId);
            userRoleList.add(userRole);
        }
        userRoleService.saveBatch(userRoleList);
        return QueryResponseResult.success("成功");
    }

    /**
     * 修改用户类型
     */
    @ApiOperation("修改用户类型")
    @PostMapping("/editUserType")
    public QueryResponseResult editUserType(
            @ApiParam(value = "用户身份证", required = true) @RequestParam String userId,
            @ApiParam(value = "类型,0：普通用户 1: 服务厂商 20:租户管理员 100：管理用户", required = true) @RequestParam Long type,
            @ApiParam("地区") @RequestParam(required = false) String tenantArea,
            @ApiParam("警种") @RequestParam(required = false) String tenantPoliceCategory,
            @ApiParam("国家专项") @RequestParam(required = false) String nationalProject,
            @ApiParam("是否第一租户管理员 1-是") @RequestParam(required = false) String defaultTenant) {
        if (UserVO.USER_TYPE_TENANT_MANAGER.equals(type)
                && org.apache.commons.lang.StringUtils.isEmpty(tenantArea)
                && org.apache.commons.lang.StringUtils.isEmpty(tenantPoliceCategory)
                && StringUtils.isEmpty(nationalProject)) {
            return QueryResponseResult.fail("请选择地区、警种或国家专项!");
        }
        userService.editUserType(userId, type, tenantArea, tenantPoliceCategory, nationalProject, defaultTenant);
        return QueryResponseResult.success("成功");
    }

    @ApiOperation("消息通知设置")
    @PostMapping("/editUserNotifyType")
    public QueryResponseResult changeNotifyType(HttpServletRequest request,
                                                @LoginUser UserVO user,
                                                @ApiParam(value = "通知类型  0：短信 1:邮箱 2:微信,多个以逗号分隔", required = true) @RequestParam String notifyType) {
        userService.update(new User(), new UpdateWrapper<User>().lambda()
                .eq(User::getIdcard, user.getIdcard())
                .set(User::getNotifyType, notifyType));

        // 修改成功后修改 session 中的值
        user.setNotifyType(notifyType);
        request.getSession().setAttribute(LoginUserHandlerMethodArgumentResolver.USER, user);
        return QueryResponseResult.success("成功");
    }

    @ApiOperation("根据用户名查询用户")
    @GetMapping("/getReviewer")
    public QueryResponseResult<User> getReviewer(@ApiParam(value = "机构id") @RequestParam(required = false) String orgId,
                                                 @ApiParam(value = "用户名", required = true) @RequestParam String name) {
        if (StringUtils.isEmpty(name)) {
            return QueryResponseResult.fail("请输入用户名");
        }
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().like(User::getName, name);
        if (!StringUtils.isEmpty(orgId)) {
            wrapper.and(
                    i -> i.and(x -> x.ne(User::getUserType, UserVO.POLICE_TYPE_EXTERNAL).eq(User::getOrgId, orgId))
            ).or(
                    i -> i.and(x -> x.eq(User::getUserType, UserVO.POLICE_TYPE_EXTERNAL).eq(User::getCompany, orgId))
            );
        }
        wrapper.eq(User::getDeleted, "0");
        List<User> userList = userService.list(wrapper);
        userList.stream().filter(user -> UserVO.POLICE_TYPE_EXTERNAL.equals(user.getUserType()))
                .forEach(user -> user.setOrgName(user.getCompanyName()));
        return QueryResponseResult.success(userList);
    }

    /**
     * feign调用，提供查询用户方法
     *
     * @param idCard
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据用户身份证查询用户")
    @GetMapping("/feign/getUserByIdCard")
    public UserVO getUserByIdCard(@ApiParam(value = "用户身份证", required = true) @RequestParam String idCard) {

        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getIdcard, idCard));
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
    
    /**
     * feign调用，提供查询用户方法
     *
     * @param id
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据用户id查询用户")
    @GetMapping("/feign/getUserById")
    public UserVO getUserById(@ApiParam(value = "用户id", required = true) @RequestParam String id) {

        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getId, id));
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * feign调用，提供查询用户方法，根据id list查询多个用户
     *
     * @param idCardList
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据用户身份证列表获取用户列表")
    @GetMapping("/feign/getUserByIdCardList")
    public List<UserVO> getUserByIdCardList(@ApiParam(value = "用户身份证列表", required = true) @RequestParam List<String> idCardList) {
        List<String> distinctIdCardList = idCardList.stream().distinct().collect(Collectors.toList());

        List<User> userList = userService.list(new QueryWrapper<User>().lambda().in(User::getIdcard, distinctIdCardList).isNotNull(User::getName));
        List<UserVO> voList = new ArrayList<>();

        userList.forEach(item->{
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(item, userVO);
            voList.add(userVO);
        });
        return voList;
    }

    /**
     * feign调用，提供查询用户方法
     *
     * @param user
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据参数查询用户")
    @PutMapping("/feign/getUserByParams")
    public List<UserVO> getUserByParams(@RequestBody UserVO user) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda();
        if (user.getType() != null) {
            wrapper.eq(User::getType, user.getType());
        }
        if (user.getDefaultTenant() != null) {
            wrapper.eq(User::getDefaultTenant, user.getDefaultTenant());
        }
        if (user.getTenantPoliceCategory() != null) {
            wrapper.eq(User::getTenantPoliceCategory, user.getTenantPoliceCategory());
        }
        if (user.getTenantArea() != null) {
            wrapper.eq(User::getTenantArea, user.getTenantArea());
        }
        List<User> userList = userService.list(wrapper);
        if(CollectionUtils.isNotEmpty(userList)) {
            List<UserVO> list = JSON.parseObject(JSON.toJSON(userList).toString(),
                    new TypeReference<List<UserVO>>(){});
            return list;
        }
        return null;
    }
    
    /**
     * feign调用，根据名称集合查询用户
     *
     * @param user
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据名称集合查询用户")
    @GetMapping("/feign/findUserByUserName")
    public List<UserVO> findUserByParams(@ApiParam(value = "用户名称集合", required = true) 
    	@RequestParam List<String> nameList) {
    	
    	List<User> userList = userService.list(new QueryWrapper<User>().lambda()
    			.in(User::getName,nameList));
    	
        if(CollectionUtils.isNotEmpty(userList)) {
            List<UserVO> list = JSON.parseObject(JSON.toJSON(userList).toString(),
                    new TypeReference<List<UserVO>>(){});
            return list;
        }
        return null;
    }

}
