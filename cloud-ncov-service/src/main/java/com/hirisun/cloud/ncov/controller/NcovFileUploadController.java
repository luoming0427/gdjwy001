package com.hirisun.cloud.ncov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hirisun.cloud.common.vo.QueryResponseResult;
import com.hirisun.cloud.model.ncov.vo.file.FileUploadVo;
import com.hirisun.cloud.ncov.service.NcovFileUploadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("疫情excel文件数据上传接口")
@RestController
@RequestMapping("/ncov/file")
public class NcovFileUploadController {

	@Autowired
	private NcovFileUploadService ncovFileUploadService;
	
	@ApiOperation(value = "文件上传")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "serviceType", value = "服务类型(疫情直接大写写死 NCOV)", required = true, paramType = "query"),
        @ApiImplicitParam(name = "dataType", value = "服务共享:DATASHARING," + 
        		"数据服务:DATASERVICE," + 
        		"数据接入:DATAACCESS," + 
        		"数据建模:DATAMODELING," + 
        		"数据治理:DATAGOVERNANCE," + 
        		"大数据集群配置:PAASDATA," + 
        		"疫情桌面云数据配置:IAASDESKTOP," + 
        		"虚拟机数据配置:IAASVM,"+
        		"疫情实时各省数据:REALTIMEPROVICE,"+
        		"疫情实时各市数据:REALTIMECITY", required = true, paramType = "query"),
        @ApiImplicitParam(name = "file", value = "文件", required = true, paramType = "query")
	})
	@PostMapping (value="/upload")
	public QueryResponseResult upload(@RequestParam(name = "serviceType",required = true) String serviceType,
			@RequestParam(name = "dataType",required = true) String dataType,
			@RequestParam(name = "file",required = true) MultipartFile file) throws Exception {
		
		FileUploadVo fileUploadVo = ncovFileUploadService.fileUpload(serviceType, dataType,file);
		if(fileUploadVo == null) return QueryResponseResult.fail(fileUploadVo);
		return QueryResponseResult.success(fileUploadVo);
	}
	
	@ApiOperation(value = "获取疫情excel文件下载地址")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "serviceType", value = "服务类型", required = true, paramType = "query"),
        @ApiImplicitParam(name = "dataType", value = "数据类型", required = false,paramType = "query")
	})
	@ApiResponses(//响应参数说明
            @ApiResponse(code=200,message="success",response= FileUploadVo.class)
    )
	@PostMapping (value="/url")
	public QueryResponseResult getUrl(String serviceType,String dataType) throws Exception {
		return QueryResponseResult.success(ncovFileUploadService.getFileUrlByServiceType(serviceType,dataType));
	}
	
	
	
}
