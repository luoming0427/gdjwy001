package com.hirisun.cloud.iaas.bean;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 弹性云服务器变更申请表
 */
@TableName("TB_IAAS_TXYFWBG")
public class IaasTxyfwbg extends Model<IaasTxyfwbg> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

        /**
     * 应用组件
     */
         @TableField("COMPONENT")
    private String component;

        /**
     * 网络
     */
         @TableField("NET")
    private String net;

        /**
     * 部署应用
     */
         @TableField("DEPLOY_APP")
    private String deployApp;

        /**
     * 虚拟IP
     */
         @TableField("VIRTUAL_IP")
    private String virtualIp;

        /**
     * 账号
     */
         @TableField("ACCOUNT")
    private String account;

        /**
     * 密码
     */
         @TableField("PASSWORD")
    private String password;

        /**
     * 变更原因
     */
         @TableField("REASON")
    private String reason;

        /**
     * 变更后状态
     */
         @TableField("CHANGE_STATUS")
    private String changeStatus;

        /**
     * 变更前内存
     */
         @TableField("BEFOR_RAM")
    private String beforRam;

        /**
     * 变更前数据盘存储
     */
         @TableField("BEFOR_STORAGE")
    private String beforStorage;

        /**
     * 变更前操作系统
     */
         @TableField("BEFOR_OS")
    private String beforOs;

        /**
     * 变更后内存
     */
         @TableField("RAM")
    private String ram;

        /**
     * 变更后数据盘存储
     */
         @TableField("STORAGE")
    private String storage;

        /**
     * 变更后操作系统
     */
         @TableField("OS")
    private String os;

        /**
     * 申请服务信息id
     */
         @TableField("APP_INFO_ID")
    private String appInfoId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "MODIFIED_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date modifiedTime;


    public String getId() {
        return id;
    }

    public IaasTxyfwbg setId(String id) {
        this.id = id;
        return this;
    }

    public String getComponent() {
        return component;
    }

    public IaasTxyfwbg setComponent(String component) {
        this.component = component;
        return this;
    }

    public String getNet() {
        return net;
    }

    public IaasTxyfwbg setNet(String net) {
        this.net = net;
        return this;
    }

    public String getDeployApp() {
        return deployApp;
    }

    public IaasTxyfwbg setDeployApp(String deployApp) {
        this.deployApp = deployApp;
        return this;
    }

    public String getVirtualIp() {
        return virtualIp;
    }

    public IaasTxyfwbg setVirtualIp(String virtualIp) {
        this.virtualIp = virtualIp;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public IaasTxyfwbg setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public IaasTxyfwbg setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public IaasTxyfwbg setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getChangeStatus() {
        return changeStatus;
    }

    public IaasTxyfwbg setChangeStatus(String changeStatus) {
        this.changeStatus = changeStatus;
        return this;
    }

    public String getBeforRam() {
        return beforRam;
    }

    public IaasTxyfwbg setBeforRam(String beforRam) {
        this.beforRam = beforRam;
        return this;
    }

    public String getBeforStorage() {
        return beforStorage;
    }

    public IaasTxyfwbg setBeforStorage(String beforStorage) {
        this.beforStorage = beforStorage;
        return this;
    }

    public String getBeforOs() {
        return beforOs;
    }

    public IaasTxyfwbg setBeforOs(String beforOs) {
        this.beforOs = beforOs;
        return this;
    }

    public String getRam() {
        return ram;
    }

    public IaasTxyfwbg setRam(String ram) {
        this.ram = ram;
        return this;
    }

    public String getStorage() {
        return storage;
    }

    public IaasTxyfwbg setStorage(String storage) {
        this.storage = storage;
        return this;
    }

    public String getOs() {
        return os;
    }

    public IaasTxyfwbg setOs(String os) {
        this.os = os;
        return this;
    }

    public String getAppInfoId() {
        return appInfoId;
    }

    public IaasTxyfwbg setAppInfoId(String appInfoId) {
        this.appInfoId = appInfoId;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public IaasTxyfwbg setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public IaasTxyfwbg setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "IaasTxyfwbg{" +
        "id=" + id +
        ", component=" + component +
        ", net=" + net +
        ", deployApp=" + deployApp +
        ", virtualIp=" + virtualIp +
        ", account=" + account +
        ", password=" + password +
        ", reason=" + reason +
        ", changeStatus=" + changeStatus +
        ", beforRam=" + beforRam +
        ", beforStorage=" + beforStorage +
        ", beforOs=" + beforOs +
        ", ram=" + ram +
        ", storage=" + storage +
        ", os=" + os +
        ", appInfoId=" + appInfoId +
        ", createTime=" + createTime +
        ", modifiedTime=" + modifiedTime +
        "}";
    }
}
