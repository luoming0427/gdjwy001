package com.hirisun.cloud.api.workflow;

import java.util.List;
import java.util.Map;

import com.hirisun.cloud.model.workflow.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hirisun.cloud.model.apply.FallBackVO;
import com.hirisun.cloud.model.param.ActivityParam;
import com.hirisun.cloud.model.param.WorkflowNodeParam;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author wuxiaoxing
 * @date 2020-08-07
 * @description
 */
@FeignClient("cloud-workflow-service")
public interface WorkflowApi {
    static String workflowBaseUrl="/workflow/workflowManage";
    static String workflowNodeBaseUrl="/workflow/workflowNodeManage";
    static String workflowActivityBaseUrl="/workflow/workflowActivityManage";
    static String workflowInstanceUrl="/workflow/workflowInstanceManage";
    static String workflowServiceBindingUrl="";


    /**
     * 执行保存应用申请实例
     *
     * @param createPersonId 创建人身份证
     * @param flowId         流程id
     * @param businessId     服务id
     */
    @ApiIgnore
    @ApiOperation("执行保存应用申请实例")
    @PostMapping(workflowInstanceUrl+"/feign/launchInstanceOfWorkflow")
    public void launchInstanceOfWorkflow(@RequestParam String createPersonId,
                                         @RequestParam String flowId,
                                         @RequestParam String businessId);

    /**
     * 执行保存应用申请实例
     *
     * @param creatorName 创建人身份证
     * @param flowId         流程id
     * @param businessId     服务id
     */
    @ApiIgnore
    @ApiOperation("执行保存应用申请实例")
    @PostMapping(workflowInstanceUrl+"/feign/launchInstanceByArea")
    public String launchInstanceByArea(@RequestParam String creatorName,
                                       @RequestParam String flowId,
                                       @RequestParam String businessId);

    /**
     * 根据服务id获取流程实例
     *
     * @param businessId 服务id
     */
    @ApiIgnore
    @ApiOperation("执行保存应用申请实例")
    @PostMapping(workflowInstanceUrl+"/feign/getWorkflowInstanceByBusinessId")
    public WorkflowInstanceVO getWorkflowInstanceByBusinessId(@RequestParam String businessId);

    /**
     * 根据参数获取流程活动
     *
     * @param status     流程活动状态
     * @param instanceId 流程实例id
     */
    @ApiIgnore
    @ApiOperation("根据参数获取一个流程流转信息")
    @PostMapping(workflowActivityBaseUrl+"/feign/getOneWorkflowActivityByParams")
    public WorkflowActivityVO getOneWorkflowActivityByParams(@RequestParam Integer status,
                                                 @RequestParam String instanceId);

    /**
     * 根据参数获取流程活动
     *
     * @param status     流程活动状态
     * @param instanceId 流程实例id
     */
    @ApiIgnore
    @ApiOperation("根据参数获取流程流转列表")
    @PostMapping(workflowActivityBaseUrl+"/feign/getWorkflowActivityListByParams")
    public List<WorkflowActivityVO> getWorkflowActivityListByParams(@RequestParam Integer status,
                                                  @RequestParam String instanceId);

    /**
     * 根据参数获取流程环节
     *
     * @param version    流程版本
     * @param workflowId 流程id
     * @param nodeSort   环节顺序
     */
    @ApiIgnore
    @ApiOperation("根据参数获取流程环节")
    @PostMapping(workflowNodeBaseUrl+"/feign/getWorkflowNodeByParams")
    public List<WorkflowNodeVO> getWorkflowNodeByParams(@RequestParam Integer version,
                                          @RequestParam String workflowId,
                                          @RequestParam(required = false) Integer nodeSort);

    /**
     * 根据参数获取流程环节
     *
     * @param version    流程版本
     * @param workflowId 流程id
     * @param instanceId 实例id
     */
    @ApiIgnore
    @ApiOperation("根据参数获取流程环节")
    @PostMapping(workflowNodeBaseUrl + "/feign/getWorkflowNodeAndActivitys")
    public List<WorkflowNodeVO> getWorkflowNodeAndActivitys(@RequestParam Integer version,
                                              @RequestParam String workflowId,
                                              @RequestParam String instanceId);


    /**
     * 环节流转
     *
     * @param advanceBeanVO   流转VO
     * @param map             短信消息map
     * @param workflowNodeStr 下个环节json串
     */
//    @ApiIgnore
//    @ApiOperation("环节流转")
//    @PutMapping(value = workflowActivityBaseUrl + "/feign/advanceCurrentActivity",consumes = "application/json")
//    public Map<String,String> advanceCurrentActivity(@RequestBody AdvanceBeanVO advanceBeanVO,
//                                                     @RequestParam Map<String, String> map,
//                                                     @RequestParam String workflowNodeStr);

    /**
     * 环节正常流转
     *
     * @param currentActivityId 环节id
     */
    @ApiIgnore
    @ApiOperation("环节正常流转")
    @PutMapping(workflowActivityBaseUrl + "/feign/advanceActivity")
    public Map<String, String> advanceActivity(@RequestParam String currentActivityId,@RequestParam Map map);

    /**
     * IPDS订单选择流程（s->saasService）
     *
     * @param resourceType
     * @param area
     * @param serviceId
     * @return
     */
    @ApiIgnore
    @ApiOperation("根据参数选择并返回申请流程")
    @PostMapping(workflowBaseUrl + "/feign/chooseWorkFlow")
    public WorkflowVO chooseWorkFlow(@RequestParam Integer resourceType,
                                     @RequestParam(required = false) String serviceId,
                                     @RequestParam(required = false) String area,
                                     @RequestParam(required = false) String policeCategory,
                                     @RequestParam(required = false) String nationalSpecialProject);

    /**
     * 通过实例id获取处理人id
     * @param instanceIdList 实例id list
     */
    @ApiIgnore
    @ApiOperation("通过实例id获取处理人id")
    @PostMapping(workflowActivityBaseUrl+"/feign/instanceToHandleIdCards")
    public Map<String, String> instanceToHandleIdCards(@RequestParam List<String> instanceIdList);

    /**
     * 通过实例id获取处理人id
     *
     * @param activityId 流程流转id
     */
    @ApiIgnore
    @ApiOperation("通过id获取流程流转信息")
    @PostMapping(workflowActivityBaseUrl + "/feign/getActivityById")
    public WorkflowActivityVO getActivityById(@RequestParam String activityId);

    /**
     * 根据流程实例id获取流程实例信息
     */
    @ApiIgnore
    @ApiOperation("根据流程实例id获取流程实例信息")
    @PostMapping(workflowInstanceUrl+"/feign/getInstanceById")
    public WorkflowInstanceVO getInstanceById(@RequestParam String instanceId);

    /**
     * 根据流程id获取流程信息
     */
    @ApiIgnore
    @ApiOperation("根据流程id获取流程信息")
    @PostMapping(workflowBaseUrl+"/feign/getWorkflowById")
    public WorkflowVO getWorkflowById(@RequestParam String workflowId);

    /**
     * 根据环节id获取环节信息
     */
    @ApiIgnore
    @ApiOperation("根据环节id获取环节信息")
    @PostMapping(workflowNodeBaseUrl+"/feign/getNodeById")
    public WorkflowNodeVO getNodeById(@RequestParam String nodeId);

    /**
     * 记录申请流程驳回信息
     *
     * @param fallBackVO 流程流转id
     */
    @ApiIgnore
    @ApiOperation("记录申请流程驳回信息")
    @PostMapping(workflowActivityBaseUrl+"/feign/fallbackOnApproveNotPass")
    public Map<String, String> fallbackOnApproveNotPass(@RequestBody FallBackVO fallBackVO,@RequestParam Map map);

    /**
     * 通知人流转信息
     *
     * @param activityId 流程流转id
     */
    @ApiIgnore
    @ApiOperation("通知人流转信息")
    @PostMapping("/feign/adviseActivity")
    public Map<String, String> adviseActivity(@RequestParam String activityId);
    
    /**
     * 根据参数获取流程环节
     *
     */
    @ApiIgnore
    @ApiOperation("通知人流转信息")
    @PostMapping(workflowActivityBaseUrl+"/feign/activity/get")
    public WorkflowActivityVO getActivityByParam(@RequestBody ActivityParam param);
    
    @ApiIgnore
    @ApiOperation("根据参数获取 WorkflowActivity 集合")
    @PostMapping(workflowActivityBaseUrl+"/feign/activity/find")
    public List<WorkflowActivityVO> findActivityByParam(@RequestBody ActivityParam param);
    
    /**
     * 根据环节param获取环节信息
     */
    @ApiIgnore
    @ApiOperation("根据环节id获取环节信息")
    @PostMapping(workflowNodeBaseUrl+"/feign/node/get")
    public WorkflowNodeVO getNodeByParam(@RequestBody WorkflowNodeParam param);
    
    /**
     * 环节流转
     *
     * @param advanceBeanVO   流转VO
     * @param map             短信消息map
     */
    @ApiIgnore
    @ApiOperation("环节流转")
    @PutMapping(value = workflowActivityBaseUrl + "/feign/activity/advance",consumes = "application/json")
    public void activityAdvance(@RequestBody AdvanceBeanVO advanceBeanVO, @RequestParam Map<String, String> map);


    /**
     * 加办
     */
    @ApiIgnore
    @ApiOperation("加办")
    @PostMapping(workflowActivityBaseUrl+"/feign/add")
    public Map<String, String> add(@RequestParam String handlerPersonIds,
                                   @RequestParam String currentActivityId,
                                   @RequestParam String creatorId);

    /**
     * 流程转发
     */
    @ApiIgnore
    @ApiOperation("流程转发")
    @PostMapping(workflowActivityBaseUrl + "/feign/activityForward")
    public Map<String, String> activityForward(@RequestParam String currentActivityId, @RequestParam String handlePersonIds);

    /**
     * 拒绝，回退到申请
     */
    @ApiIgnore
    @ApiOperation("回退到申请")
    @PostMapping(workflowActivityBaseUrl+"/feign/rejectApply")
    public Map<String, String> rejectApply(
            @RequestParam String currentActivityId,
            @RequestParam String fallBackModelId);

    /**
     * 终止流程
     *
     * @param applyInfoId
     * @return
     */
    @ApiIgnore
    @ApiOperation("终止流程")
    @PostMapping(workflowActivityBaseUrl + "/feign/terminationOrder")
    public Map<String, String> terminationOrder(
            @RequestParam String applyInfoId);

    /**
     * 根据流程类型获取流程信息
     */
    @ApiIgnore
    @ApiOperation("根据流程类型获取流程信息")
    @PostMapping(workflowBaseUrl+"/feign/getWorkflowByDefaultProcess")
    public WorkflowVO getWorkflowByDefaultProcess(@RequestParam String defaultProcess);

    /**
     * 根据对象内容获取流程流转信息
     */
    @ApiIgnore
    @ApiOperation("根据对象内容获取流程流转信息")
    @PutMapping(workflowActivityBaseUrl + "/feign/getActivityByObj")
    public List<WorkflowActivityVO> getActivityByObj(@RequestBody WorkflowActivityVO vo);
}
