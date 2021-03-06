package com.hirisun.cloud.platform.document.bean;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hirisun.cloud.model.common.Tree;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 文档分类表
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-07-20
 */
@TableName("T_DEV_DOC_CLASS")
@ApiModel(value="DevDocClass对象", description="文档分类表")
public class DevDocClass extends Tree<DevDocClass> implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "分类名称")
    @TableField("NAME")
    private String name;

    @ApiModelProperty(value = "排序")
    @TableField("SORT")
    private Integer sort;

    @TableField("UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATOR")
    private String creator;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "类别 1一级分类 2二级分类")
    @TableField("TYPE")
    private Long type;

    @ApiModelProperty(value = "分类图片")
    @TableField("IMAGE")
    private String image;

    @ApiModelProperty(value = "上级id")
    @TableField("PID")
    private String pid;

    @TableField(exist = false)
    private List<DevDocClass> children;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    @Override
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<DevDocClass> getChildren() {
        return children;
    }

    public void setChildren(List<DevDocClass> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "DevDocClass{" +
        "id=" + id +
        ", name=" + name +
        ", sort=" + sort +
        ", updateTime=" + updateTime +
        ", creator=" + creator +
        ", remark=" + remark +
        ", type=" + type +
        ", image=" + image +
        ", pid=" + pid +
        "}";
    }
}
