package com.hirisun.cloud.model.ncov.vo.realtime;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("疫情实时数据")
public class NcovRealtimeVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 162317556187196564L;

	@ApiModelProperty(value="id")
	private String id;

	@ApiModelProperty(value="区域名称")
	@Excel(name = "区域名称",orderNum = "0")
    private String regionName; 

	@ApiModelProperty(value="确诊")
	@Excel(name = "确诊",orderNum = "1")
    private int diagnosis; 

	@ApiModelProperty(value="疑似")
	@Excel(name = "疑似",orderNum = "3")
    private int suspected; 

	@ApiModelProperty(value="死亡")
	@Excel(name = "死亡",orderNum = "5")
    private int death; 

	@ApiModelProperty(value="治愈")
	@Excel(name = "治愈",orderNum = "7")
    private int cure; 

	@ApiModelProperty(value="省份号码 广东=44")
	@Excel(name = "省份编号",orderNum = "9")
    private Integer provinceCode; 

	@ApiModelProperty(value="1=省，2=市")
    private int regionType; 
	
	@ApiModelProperty(value="较昨日确诊新增")
	@Excel(name = "较昨日确诊新增",orderNum = "2")
    private int yesterdayDiagnosis; //较昨日确诊新增

	@ApiModelProperty(value="较昨日疑似新增")
	@Excel(name = "较昨日疑似新增",orderNum = "4")
    private int yesterdaySuspected; //

	@ApiModelProperty(value="较昨日死亡新增")
	@Excel(name = "较昨日死亡新增",orderNum = "6")
    private int yesterdayDeath; 

	@ApiModelProperty(value="较昨日治愈新增")
	@Excel(name = "较昨日治愈新增",orderNum = "8")
    private int yesterdayCure;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(int diagnosis) {
        this.diagnosis = diagnosis;
    }

    public int getSuspected() {
        return suspected;
    }

    public void setSuspected(int suspected) {
        this.suspected = suspected;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getCure() {
        return cure;
    }

    public void setCure(int cure) {
        this.cure = cure;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getRegionType() {
        return regionType;
    }

    public void setRegionType(Integer regionType) {
        this.regionType = regionType;
    }

	public int getYesterdayDiagnosis() {
		return yesterdayDiagnosis;
	}

	public void setYesterdayDiagnosis(int yesterdayDiagnosis) {
		this.yesterdayDiagnosis = yesterdayDiagnosis;
	}

	public int getYesterdaySuspected() {
		return yesterdaySuspected;
	}

	public void setYesterdaySuspected(int yesterdaySuspected) {
		this.yesterdaySuspected = yesterdaySuspected;
	}

	public int getYesterdayDeath() {
		return yesterdayDeath;
	}

	public void setYesterdayDeath(int yesterdayDeath) {
		this.yesterdayDeath = yesterdayDeath;
	}

	public int getYesterdayCure() {
		return yesterdayCure;
	}

	public void setYesterdayCure(int yesterdayCure) {
		this.yesterdayCure = yesterdayCure;
	}

}
