package com.hirisun.cloud.workflow.service;

import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.workflow.bean.Workflow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 流程定义表 服务类
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-08-05
 */
public interface WorkflowService extends IService<Workflow> {
    boolean saveOrUpdateWorkflow(UserVO user,Workflow workflow,String list);
}
