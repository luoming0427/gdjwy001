package com.hirisun.cloud.workflow.service;

import com.hirisun.cloud.common.vo.QueryResponseResult;
import com.hirisun.cloud.workflow.bean.WorkflowInstance;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 流程实例表 服务类
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-08-05
 */
public interface WorkflowInstanceService extends IService<WorkflowInstance> {


    /**
     * 保存工作流审批信息
     * @param createPersonId
     * @param flowId
     * @param businessId
     * @return
     */
    public String launchInstanceOfWorkflow(String createPersonId, String flowId, String businessId);


    public void launchInstanceByArea(String creatorName, String businessId, String resourceType);
}

