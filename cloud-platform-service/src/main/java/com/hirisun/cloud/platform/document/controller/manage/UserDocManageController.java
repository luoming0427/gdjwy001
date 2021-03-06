package com.hirisun.cloud.platform.document.controller.manage;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hirisun.cloud.api.file.FileApi;
import com.hirisun.cloud.common.annotation.LoginUser;
import com.hirisun.cloud.common.contains.ReviewStatus;
import com.hirisun.cloud.common.vo.QueryResponseResult;
import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.platform.document.bean.DevDoc;
import com.hirisun.cloud.platform.document.bean.UserDoc;
import com.hirisun.cloud.platform.document.service.UserDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>
 * 用户操作文档管理 前端控制器
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-07-20
 */
@RestController
@RequestMapping("/platform/userDocManage")
@Api(tags = "用户文档管理")
public class UserDocManageController {

    @Autowired
    private UserDocService userDocService;

    @Autowired
    private FileApi fileApi;

    /**
     * 分页获取文档列表
     */
    @ApiOperation("分页获取文档列表")
    @GetMapping("/list")
    public QueryResponseResult<UserDoc> list(@LoginUser UserVO user,
                                            @ApiParam("页码") @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                            @ApiParam("每页大小") @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                            @ApiParam("状态 1待上线 2已上线") @RequestParam(required = false,defaultValue = "1") Integer status,
                                            @ApiParam("关键词") @RequestParam(required = false) String keyword,
                                            @ApiParam("适用业务，根据数据字典查询") @RequestParam(required = false) String domain) {
        Page<UserDoc> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        Map param=new HashMap();
        param.put("status",status);
        param.put("name", keyword);
        param.put("domain", domain);
        page = userDocService.getPage(page,user,param);
        return QueryResponseResult.success(page);
    }

    @ApiOperation("用户文档详情")
    @GetMapping("/userDocDetail")
    public QueryResponseResult<UserDoc> detail(@ApiParam(value = "用户文档id",required = true) @RequestParam String id) {
        UserDoc document = userDocService.getById(id);
        JSONObject docFile=null;

        if (!StringUtils.isEmpty(document.getFileId())) {
            String fileJsonStr=fileApi.getFileSystemInfo(document.getFileId());
            if (!StringUtils.isEmpty(fileJsonStr)) {
                docFile = JSON.parseObject(fileJsonStr);
            }
        }
        Map map = new HashMap();
        map.put("devDoc", document);
        map.put("docFile", docFile);
        return QueryResponseResult.success(map);
    }

    @ApiOperation("删除文档")
    @PostMapping(value = "/delete")
    public QueryResponseResult delete(@LoginUser UserVO user, @ApiParam(value = "文档id",required = true) @RequestParam String id) {
        UserDoc document = userDocService.getById(id);
        document.setStatus(ReviewStatus.DELETE.getCode());
        userDocService.updateById(document);
        return QueryResponseResult.success(null);
    }

    @ApiOperation("新增文档")
    @PutMapping("/saveOrUpdate")
    public QueryResponseResult<UserDoc> saveOrUpdate(@LoginUser UserVO user, @RequestBody UserDoc devDoc) {
        userDocService.saveOrUpdateUserDoc(user, devDoc);
        return QueryResponseResult.success(null);
    }

    /**
     * 上/下线
     * @param type 1:上线,0:下线
     */
    @ApiOperation("上/下线操作")
    @PostMapping(value = "/publish")
    public QueryResponseResult publish(@LoginUser UserVO user,
                                       @ApiParam(value = "开发文档id",required = true) @RequestParam String id,
                                       @ApiParam(value = "类型 1上线 0下线",required = true) @RequestParam String type) {
        UserDoc userDoc = userDocService.getById(id);
        if(userDoc==null){
            return QueryResponseResult.fail("文档不存在");
        }
        if ("1".equals(type)) {
            userDoc.setStatus(ReviewStatus.ONLINE.getCode());
        }else{
            userDoc.setStatus(ReviewStatus.PRO_ONLINE.getCode());
        }
        userDocService.updateById(userDoc);
        return QueryResponseResult.success(null);
    }

}

