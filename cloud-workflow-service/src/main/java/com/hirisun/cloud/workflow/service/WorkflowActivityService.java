package com.hirisun.cloud.workflow.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hirisun.cloud.model.apply.FallBackVO;
import com.hirisun.cloud.model.param.ActivityParam;
import com.hirisun.cloud.model.workflow.AdvanceBeanVO;
import com.hirisun.cloud.model.workflow.WorkflowActivityVO;
import com.hirisun.cloud.workflow.bean.WorkflowActivity;


/**
 * <p>
 * 流程流转表 服务类
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-08-05
 */
public interface WorkflowActivityService extends IService<WorkflowActivity> {

//    public Map<String, String> advanceCurrentActivity(AdvanceBeanVO advanceBeanVO, Map<String, String> map, WorkflowNode nextModel);

    public Map<String, String> advanceActivity(String currentActivityId, Map<String, String> map);

    public Map<String, String> instanceToHandleIdCards(List<String> instanceIdList);

    public Map<String, String> fallbackOnApproveNotPass(FallBackVO vo, Map<String, String> map);

    public Map<String, String> adviseActivity(String currentActivityId);
    
    public WorkflowActivityVO getActivityByParam(ActivityParam param);
    
    public List<WorkflowActivityVO> findActivityByParam(ActivityParam param);

    public void advanceCurrentActivity(AdvanceBeanVO advanceBeanVO, Map<String, String> map);
    public Map<String,String> add(String handlerPersonIds, String currentActivityId,String creatorId);

    public Map<String, String> rejectApply(String currentActivityId, String fallBackModelIds);

    public Map<String, String> terminationOrder(String applyInfoId);

}
