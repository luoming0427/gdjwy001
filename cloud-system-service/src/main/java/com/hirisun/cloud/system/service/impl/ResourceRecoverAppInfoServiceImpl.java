package com.hirisun.cloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hirisun.cloud.api.system.FilesApi;
import com.hirisun.cloud.api.system.SmsApi;
import com.hirisun.cloud.api.user.UserApi;
import com.hirisun.cloud.common.constant.BusinessName;
import com.hirisun.cloud.common.constant.RedisKey;
import com.hirisun.cloud.common.contains.ApplicationInfoStatus;
import com.hirisun.cloud.common.enumer.ResourceRecoverStatus;
import com.hirisun.cloud.common.exception.CustomException;
import com.hirisun.cloud.common.util.DateUtil;
import com.hirisun.cloud.common.util.UUIDUtil;
import com.hirisun.cloud.common.vo.CommonCode;
import com.hirisun.cloud.common.vo.ResponseResult;
import com.hirisun.cloud.model.app.param.SubpageParam;
import com.hirisun.cloud.model.impl.vo.ImplRequestVo;
import com.hirisun.cloud.model.system.ResourceRecoverSubmitVo;
import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.model.workflow.AdvanceBeanVO;
import com.hirisun.cloud.model.workflow.FallBackVO;
import com.hirisun.cloud.model.workflow.WorkflowNodeVO;
import com.hirisun.cloud.redis.lock.DistributeLock;
import com.hirisun.cloud.redis.service.RedisService;
import com.hirisun.cloud.system.bean.ResourceRecover;
import com.hirisun.cloud.system.bean.ResourceRecoverAppInfo;
import com.hirisun.cloud.system.mapper.ResourceRecoverAppInfoMapper;
import com.hirisun.cloud.system.service.IResourceRecoverAppInfoService;
import com.hirisun.cloud.system.service.IResourceRecoverImplService;
import com.hirisun.cloud.system.service.IResourceRecoverService;
import com.hirisun.cloud.system.timer.Timer;
import com.hirisun.cloud.system.vo.SystemCode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 资源回收申请信息 服务实现类
 * </p>
 *
 * @author yyc
 * @since 2020-05-14
 */
@Service
public class ResourceRecoverAppInfoServiceImpl extends ServiceImpl<ResourceRecoverAppInfoMapper, 
	ResourceRecoverAppInfo> implements IResourceRecoverAppInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceRecoverAppInfoServiceImpl.class);

    @Autowired
    private DistributeLock lock;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IResourceRecoverService resourceRecoverService;
//    TODO 申请流程审批
//    @Autowired
//    private IWorkflowService workflowService;
//
//    @Autowired
//    private IWorkflowmodelService workflowmodelService;
//
//    @Autowired
//    private IInstanceService instanceService;
//
//    @Autowired
//    private IActivityService activityService;

    @Autowired
    private ResourceRecoverAppInfoMapper resourceRecoverAppInfoMapper;

    @Autowired
    private SmsApi smsApi;
//  TODO 申请流程审批
//    @Autowired
//    private ISpeedUpService speedUpService;
//
//    @Autowired
//    private IAppReviewInfoService appReviewInfoService;
    @Autowired
    private FilesApi filesApi;

    @Autowired
    private IResourceRecoverImplService implService;

    @Autowired
    private RedisService redisService;
    
    @Autowired
    private UserApi userApi;

    @Autowired
    private Timer timer;


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult submit(UserVO user, ResourceRecoverSubmitVo submit) {
//      TODO 申请流程审批
//        logger.debug("submit -> {}",submit);
//        List<ResourceRecoverSubmitVo.RecoveredUserInfo> recoveredUserInfoList = submit.getRecoveredUserInfoList();
//        if(CollectionUtils.isEmpty(recoveredUserInfoList)){
//            throw new CustomException(SystemCode.RESOURCE_RECOVER_NULL);
//        }
//
//        List<String> recoveredUserName = recoveredUserInfoList.stream().map(ResourceRecoverSubmitVo.RecoveredUserInfo::getApplicant).distinct().collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(recoveredUserName)){
//            throw new CustomException(SystemCode.APPLY_PERSON_NULL);
//        }
//        
//		List<UserVO> allRecoverdUser = userApi.findUserByUserName(recoveredUserName);
//        
//        if(CollectionUtils.isEmpty(allRecoverdUser)){
//            throw new CustomException(SystemCode.GET_APPLY_PERSON_NULL);
//        }
//
//        Workflow workFlow = workflowService.getOne(new QueryWrapper<Workflow>().eq("DEFAULT_PROCESS","RECOVER"));
//        if(workFlow == null){
//            throw new BaseException("流程未初始化!");
//        }
//        //分单 按 人 电话 单位 导入时间分(分组条件)
//        for(ResourceRecoverSubmitVo.RecoveredUserInfo recoveredUserInfo:recoveredUserInfoList){
//
//            ResourceRecoverAppInfo info = new ResourceRecoverAppInfo();
//            info.setId(null);
//            info.setCreator(user.getName());
//            info.setCreatorPhone(user.getMobileWork());
//            info.setCreatorIdCard(user.getIdcard());
//            info.setStatus(ApplicationInfoStatus.INNER_REVIEW.getCode());
//            
//            redisService.genOrderNum(RedisKey.KEY_RESOURCE_RECOVER);
//            
//            info.setRecoveredPerson(recoveredUserInfo.getApplicant());
//            info.setRecoveredPersonPhone(recoveredUserInfo.getApplicantPhone());
//
//            Optional<UserVO> recoveredUserOp =allRecoverdUser.stream().filter(item->
//                StringUtils.equals(item.getName(),recoveredUserInfo.getApplicant())
//                        && StringUtils.equals(item.getOrgName(),recoveredUserInfo.getApplicantOrgName())).findFirst();
//
//            if(!recoveredUserOp.isPresent()){
//                logger.error("Can not found user:{} {}",recoveredUserInfo.getApplicant(),recoveredUserInfo.getApplicantOrgName());
//                //跳过 进入下一个循环
//                throw new BaseException("找不到用户"+recoveredUserInfo.getApplicant()+":"+recoveredUserInfo.getApplicantOrgName());
//            }
//
//            info.setRecoveredPersonIdCard(recoveredUserOp.get().getIdcard());
//            info.setWorkFlowId(workFlow.getId());
//            info.setWorkFlowVersion(workFlow.getVersion());
//
//            resourceRecoverAppInfoMapper.insert(info);
//
//            logger.debug("app id -> {}",info.getId());
//
//            //关联申请单和被回收资源
//            resourceRecoverService.update(new ResourceRecover(),new UpdateWrapper<ResourceRecover>().lambda()
//                                            .eq(ResourceRecover::getApplicant,recoveredUserInfo.getApplicant())
//                                            .eq(ResourceRecover::getApplicantPhone,recoveredUserInfo.getApplicantPhone())
//                                            .eq(ResourceRecover::getApplicantOrgName,recoveredUserInfo.getApplicantOrgName())
//                                            .eq(ResourceRecover::getImportTimeStr,recoveredUserInfo.getImportTime())
//                                            .eq(ResourceRecover::getStatus,ResourceRecoverStatus.UN_PROCESSED.getCode())
//                                            .set(ResourceRecover::getStatus, ResourceRecoverStatus.PROCESSING.getCode())
//                                            .set(ResourceRecover::getRefId,info.getId()));
//
//            //关联申请单和文件
//            SubpageParam param = new SubpageParam();
//            param.setFiles(submit.getFileList());
//            param.setRefId(info.getId());
//			filesApi.refFiles(param);
//
//           R r =  instanceService.launchInstanceOfWorkFlow(user.getIdcard(), info.getWorkFlowId(),info.getId());
//
//
//            Workflowmodel workflowmodel = workflowmodelService.getOne(new QueryWrapper<Workflowmodel>().eq("WORKFLOWID",workFlow.getId()).eq("modelname", ModelName.RECOVERED.getName()).eq("VERSION",workFlow.getVersion()));
//
//            Map<String, String> modelMapToPerson = new HashMap<>();
//            //todo：确认导入数据是否有身份证
//            modelMapToPerson.put(workflowmodel.getId(), recoveredUserOp.get().getIdcard());
//            AdvanceBeanVO advanceBeanVO = new AdvanceBeanVO();
//            advanceBeanVO.setCurrentActivityId(r.get("data").toString());
//            advanceBeanVO.setModelMapToPerson(modelMapToPerson);
//
//            Map<String,String> map = new HashMap<>();
//            map.put("name", BusinessName.RECOVER);
//            map.put("order", info.getOrderNumber());
//            getMsgTime(info,map);//处理map
//            activityService.advanceCurrentActivity(advanceBeanVO, map);
//            smsApi.buildSuccessMessage(user.getIdcard(), BusinessName.RECOVER, info.getOrderNumber());
//            //            sendMsg(info.getRecoveredPersonIdCard(),info);
//            //放入定时器，超过48小时未处理工单，重新发送短信
//            //1.开启定时器，48小时后执行
//            //2.检查工单状态，如果未处理，则进行短信发送
//            try{
//                String expendTime="48:00:00";
//                timer.startRecoverCheck(DateUtil.dateAdd(info.getCreateTime(),expendTime),info.getRecoveredPersonIdCard(),info);//发到定时器进行检查状态
//            }catch (Exception e){
//               e.printStackTrace();
//            }
//        }
//
        return ResponseResult.success();
    }

    @Override
    public ResponseResult initWorkFlow() {
//      TODO 申请流程审批
//        Workflow workFlow = workflowService.getOne(new QueryWrapper<Workflow>().eq("DEFAULT_PROCESS","RECOVER"));
//        if(workFlow != null){
//            throw new BaseException("流程已初始化");
//        }
//        Workflow workflow = new Workflow();
//        workflow.setFlowStatus(1);
//        workflow.setId(UUIDUtil.getUUID());
//        workflow.setDefaultProcess("RECOVER");
//        workflow.setWorkflowname("资源回收流程");
//        workflow.insert();
//
//        String workflowId = workflow.getId();
//
//        List<Workflowmodel> models = new ArrayList<>();
//
//        // 业务办理
//        Workflowmodel carry =  new Workflowmodel(workflowId, ModelName.CARRY.getName(),null);
//        carry.setDefaulthandleperson("410184198209096919");
//        carry.setVersion(0);
//        models.add(carry);
//        // 大数据办审批
//        Workflowmodel bigData =  new Workflowmodel(workflowId, ModelName.APPROVE.getName(), carry.getId());
//        bigData.setDefaulthandleperson("410184198209096919");
//        bigData.setVersion(0);
//        models.add(bigData);
//
//        //被回收资源负责人审批
//        Workflowmodel recovered =new Workflowmodel(workflowId,ModelName.RECOVERED.getName(),bigData.getId());
//        recovered.setVersion(0);
//        models.add(recovered);
//
//        //申请
//        Workflowmodel apply = new Workflowmodel(workflowId,ModelName.APPLY.getName(),recovered.getId());
//        apply.setVersion(0);
//        apply.setIsstart(0);
//        models.add(apply);
//
//        workflowmodelService.saveBatch(models);
        return ResponseResult.success();
    }

    /**
     * 详情
     *
     * @param id 申请单ID
     * @return map
     */
    @Override
    public Map<String, Object> detail(String id,UserVO user) {
		return null;
//      TODO 申请流程审批
//        ResourceRecoverAppInfo info = getBizData(id);
//        Instance instance = instanceService.getInstanceByBusinessId(id);
//        logger.debug("detail instanceId -> {}",instance.getId());
//
//        Activity activity = null;
//        if (null!=instance){
//            info.setWorkFlowId(instance.getWorkflowid());
//            info.setWorkFlowVersion(instance.getWorkflowversion());
//            List<Activity> activitys = activityService.list(new QueryWrapper<Activity>().lambda()
//                    .eq(Activity::getInstanceid,instance.getId()).eq(Activity::getHandlepersonids,user.getIdcard()).eq(Activity::getActivitystatus,"待办").isNull(Activity::getActivitytype));
//            if (null==activitys||activitys.size()==0){
//                activitys = activityService.list(new QueryWrapper<Activity>().lambda()
//                        .eq(Activity::getInstanceid,instance.getId()).eq(Activity::getHandlepersonids,user.getIdcard()).eq(Activity::getActivitystatus,"待办").eq(Activity::getActivitytype,"adviser"));
//            }
//            if (null!=activitys&&activitys.size()>0) activity = activitys.get(0);
//        }
//        String activityId = "";
//        if (activity!=null) {
//            activityId = activity.getId();
//            logger.debug("detail activityId -> {}",activity.getId());
//            Workflowmodel workflowmodel=workflowmodelService.getById(activity.getModelid());
//            if (null==workflowmodel){
//                return R.error("未找到流程环节");
//            }
//            String  modelName=workflowmodel.getModelname();
//            if (modelName.equals(ModelName.RECOVERED.getName())||modelName.equals(ModelName.APPROVE.getName())) {
//                info.setCanReview(true);
//            }
//            if (modelName.equals(ModelName.CARRY.getName())) {
//                info.setCanImpl(true);
//            }
//
//        }
//
//        List<AppReviewInfo> allReviewInfo = appReviewInfoService.getAllReviewInfoByAppInfoId(id);
//        Map bean = new HashMap<>();
//        if (allReviewInfo!=null){
//            Map<String, List<AppReviewInfo>> reviews = allReviewInfo.stream().collect(Collectors.groupingBy(a->a.getFlowStepId()+""));
//            bean.put("review",reviews);
//        }
//
//        WorkFlowModelVo modelVo=workflowmodelService.getWorkFlowDefinition(id);
//        Map<String,String> status = Maps.newHashMap();
//        status = modelStatusMap(modelVo,status);
//        String carryStatus = status.get(ModelName.CARRY.getName());
//        if(StringUtils.equals(carryStatus,"underway") || StringUtils.equals(carryStatus,"finished")){
//            if(!StringUtils.equals(status.get(ModelName.APPROVE.getName()),"finished")){
//                setApproveModelFinished(modelVo);
//            }
//        }
//        // 反馈记录
//        if (modelVo!=null&&info!=null) {
//            bean.put("model",modelVo);
//            bean.put("bizData",info);
//            bean.put("activityId",activityId);
//            return R.ok(bean);
//        }else {
//            return R.error("未找到流程");
//        }

    }

    /**
     * 获取订单订单业务信息
     * @param id 订单ID
     * @return
     */
    private ResourceRecoverAppInfo getBizData(String id){
//        ResourceRecoverAppInfo info = this.getById(id);
//        Instance instance = instanceService.getInstanceByBusinessId(id);
//        logger.debug("detail instanceId -> {}",instance.getId());
//        List<ResourceRecover> serverList = resourceRecoverService.list(new QueryWrapper<ResourceRecover>().lambda().eq(ResourceRecover::getRefId,id));
//        info.setServerList(serverList);
//
//        List<ResourceRecoverImpl> implServerList = implService.list(new QueryWrapper<ResourceRecoverImpl>().lambda().eq(ResourceRecoverImpl::getAppInfoId,id));
//        info.setImplServerList(implServerList);
//
//        List<Files> filesList = filesService.list(new QueryWrapper<Files>().lambda().eq(Files::getRefId, id));
//        info.setFileList(filesList);
//
//        // 审核信息
//        List<AppReviewInfo> allReviewInfo = appReviewInfoService.getAllReviewInfoByAppInfoId(id);
//        for (AppReviewInfo reviewInfo:allReviewInfo){
//            List<Files> reviewFileList = filesService.list(new QueryWrapper<Files>().lambda().eq(Files::getRefId, reviewInfo.getId()));
//            reviewInfo.setFileList(reviewFileList);
//        }
//        info.setReviewList(allReviewInfo);
//        // 实施审批信息
//        AppReviewInfo implInfo = null;
//        AppReviewInfo lastReviewInfo = appReviewInfoService.getLastPassReviewInfoByAppInfoId(id);
//        if (lastReviewInfo != null && "2".equals(lastReviewInfo.getrType())) {
//            // 最近一条审核记录为实施记录
//            implInfo = lastReviewInfo;
//            List<Files> implFileList = filesService.list(new QueryWrapper<Files>().lambda().eq(Files::getRefId, implInfo.getId()));
//            implInfo.setFileList(implFileList);
//            info.setImpl(implInfo);
//        }
//        return info;
    	return null;
    }

    /**
     * 判断大数据办是否完成
     * @param vo
     * @return
     */
    private Map<String,String> modelStatusMap(WorkflowNodeVO vo,Map<String,String> status){
//        List<WorkFlowModelVo> nextVoList = vo.getNextModels();
//        if(CollectionUtils.isNotEmpty(nextVoList)){
//            WorkFlowModelVo nextVo = nextVoList.get(0);
//            if(nextVo != null){
//                if(StringUtils.equals(nextVo.getModelName(),ModelName.APPROVE.getName()) ||
//                        StringUtils.equals(nextVo.getModelName(),ModelName.CARRY.getName())){
//                    status.put(nextVo.getModelName(),nextVo.getModelStatusCode());
//                }
//                modelStatusMap(nextVo,status);
//            }
//        }
        return status;
    }

    /**
     * 设置大数据办环节为已跳过
     * @param vo
     */
    private void setApproveModelFinished(WorkflowNodeVO vo){
//        List<WorkFlowModelVo> nextVoList = vo.getNextModels();
//        if(CollectionUtils.isNotEmpty(nextVoList)){
//            WorkFlowModelVo nextVo = nextVoList.get(0);
//            if(nextVo != null && !StringUtils.equals(nextVo.getModelName(),ModelName.APPROVE.getName())){
//                setApproveModelFinished(nextVo);
//            }
//            nextVo.setModelStatusCode("finished");
//            nextVo.setModelStatus("已跳过");
//        }
    }


    /**
     * 审批
     * @param user 当前用户
     * @param vo 流转VO
     * @return R
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResponseResult approve(UserVO user, FallBackVO vo){


//        AppReviewInfo approve = vo.getApprove();
//        approve.setCreator(user.getIdcard());
//
//
//        ResourceRecoverAppInfo info = this.getById(vo.getApprove().getAppInfoId());
//
//        Activity activity =activityService.getById(vo.getCurrentActivityId());
//        Workflowmodel currentModel = workflowmodelService.getById(activity.getModelid());
//
//
//
//        Map<String,String> map = new HashMap<>();
//        map.put("name",BusinessName.RECOVER);
//        map.put("order",info.getOrderNumber());
//        getMsgTime(info,map);//处理map
//
//        //被回收资源负责人
//        if(StringUtils.equals(ModelName.RECOVERED.getName(),currentModel.getModelname())){
//            //同意回收
//            if("1".equals(approve.getResult())){
//                //设置订单状态为被回收资源负责人同意回收，直接到业务办理
//                approve.setResourceRecoveredAgree(1);
//                info.setRecoveredAgree(1);
//            }else if("0".equals(approve.getResult())){
//                //设置订单状态为被回收资源负责人不同意回收，到下一环节（大数据办）
//                approve.setResourceRecoveredAgree(0);
//                info.setRecoveredAgree(0);
//            }
//            R r = activityService.advanceCurrentActivity(vo.getCurrentActivityId(), approve,map);
//            Object model = r.get("data");
//            if (ModelName.CARRY.getName().equals(model)){
//                info.setStatus(ApplicationInfoStatus.IMPL.getCode());
//            }else {
//                info.setStatus(ApplicationInfoStatus.RESENT.getCode());
//            }
//        }else if(StringUtils.equals(ModelName.APPROVE.getName(),currentModel.getModelname())){ //大数据办
//            //只有在被回收资源人不同意回收时，才发生流程流转
//            if(info.getRecoveredAgree()==0){
//                //大数据办处理结果为回收,只在被回收资源不同意回收时发生回退
//                if("1".equals(approve.getResult())){
////                    Workflowmodel fallbackModel = workflowmodelService.getById(vo.getFallBackModelIds());
////                    if(StringUtils.equals(ModelName.RECOVERED.getName(),fallbackModel.getModelname())){
////                        activityService.fallbackOnApproveNotPass(vo, null);
////                        info.setStatus(ApplicationInfoStatus.REVIEW_REJECT.getCode());
////
////                        messageProvider.sendMessageAsync(messageProvider.buildRejectMessage(info.getCreator(), BusinessName.RECOVER));
////                    }
//                    R r = activityService.advanceCurrentActivity(vo.getCurrentActivityId(), approve,map);
//                    Object model = r.get("data");
//                    if (ModelName.CARRY.getName().equals(model)){
//                        info.setStatus(ApplicationInfoStatus.IMPL.getCode());
//                    }else {
//                        info.setStatus(ApplicationInfoStatus.RESENT.getCode());
//                    }
//                }else if("0".equals(approve.getResult())){ //大数据办不回收,流转到业务办理
//                    //最新修改，大数据办不回收，进行回退
//                    activityService.fallbackOnApproveNotPass(vo, null);
//                    info.setStatus(ApplicationInfoStatus.REVIEW_REJECT.getCode());
//                    smsApi.buildRejectMessage(info.getCreator(), BusinessName.RECOVER);
//                }
//            }
//        }
//
//        resourceRecoverAppInfoMapper.updateById(info);
        return ResponseResult.success();
    }

    /**
     * 分页
     *
     * @param page     分页对象
     * @param name   身份证
     * @param orderNum 订单号 1 待审核 2 待实施 3使用中
     * @param status   状态
     * @return
     */
    @Override
    public IPage<ResourceRecoverAppInfo> getPage(IPage<ResourceRecoverAppInfo> page, UserVO user,String name, String orderNum, String status) {

//        Map<String,String> params = Maps.newHashMap();
//        params.put("name",name);
//        params.put("orderNum",orderNum);
//        params.put("status",status);
//        page =  resourceRecoverAppInfoMapper.getPage(page,params);
//        List<ResourceRecoverAppInfo> records = page.getRecords();
//        if(CollectionUtils.isNotEmpty(records)){
//            speedUpService.dealProcessingPersonResourceRecovered(records,user);
//        }
        return page;
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResponseResult impl(UserVO user, String id, String modelId, String activityId, ImplRequestVo implRequest) throws Exception {
//        ResourceRecoverAppInfo info = getBizData(id);
//        Activity activity =activityService.getById(activityId);
//        if (implRequest.getResult() == null) {
//            throw new BaseException("请选择实施结果");
//        }
//        if (!"1".equals(implRequest.getResult())) {
//            implRequest.setResult("0");
//        }
//        Map<String, Object> param = new HashMap<>();
//        param.put("info", info);
//        param.put("implRequest", implRequest);
//
//        String uuid = UUIDUtil.getUUID();
//        String lockKey = info.getId().intern();
//        try {
//            if (lock.lock(lockKey, uuid)) {
//                implService.saveImpl(user, param, activity.getModelid());
//            } else {
//                throw new BaseException("实施失败");
//            }
//        }catch (Exception e){
//            throw e;
//        }finally {
//            lock.unlock(lockKey, uuid);
//        }
//        if ("1".equals(implRequest.getResult())){
//            //ServiceReturnBean returnBean = getTestReturnBean(false);
//            activityService.advanceCurrentActivity(activity,info.getCreator());
////            sendMsg(user,info);
//            smsApi.buildCompleteMessage(info.getCreator(), BusinessName.RECOVER, info.getOrderNumber());
//        }else {
//            if (modelId==null||modelId.trim().equals("")) {
//                return R.error(201, "回退环节ID不能为空,回退失败");
//            }
//            Map<String,String> map = new HashMap<>();
//            map.put("name",BusinessName.RECOVER);
//            map.put("order",info.getOrderNumber());
//            FallBackVO vo = new FallBackVO();
//            vo.setCurrentActivityId(activityId);
//            vo.setFallBackModelIds(modelId);
//            activityService.fallbackOnApproveNotPass(vo, map);
//        }
        return ResponseResult.success();
    }

    /**
     * 删除
     *
     * @param id 订单ID
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResponseResult delete(String id) {
//       if(StringUtils.isBlank(id)){
//           throw new BaseException("请选择订单");
//       }
//       //标记为删除
//       this.update(new ResourceRecoverAppInfo(),new UpdateWrapper<ResourceRecoverAppInfo>().lambda()
//                            .eq(ResourceRecoverAppInfo::getId,id)
//                            .set(ResourceRecoverAppInfo::getStatus,ApplicationInfoStatus.DELETE.getCode()));
//       activityService.terminationInstanceOfWorkFlow(id);
       return ResponseResult.success();
    }

    /**
     * 获取未被负责人处理的回收资源工单,检查状态，未被处理时重新发送短信
     */
    public void queryUntreatedRecover(){
//        logger.debug("开始执行查询未被回收负责人处理的工单");
//        List<ResourceRecoverAppInfo> recoverList = resourceRecoverAppInfoMapper.queryUntreatedRecover();
//        if(recoverList==null||recoverList.size()==0){return ;}
//        //存放被负责人集合，减少数据库重复查询
//        ConcurrentMap<String,User> userMap=new ConcurrentHashMap<String,User>();
//        //循环未被负责责任处理的工单，再次发送短信，更新工单状态为已发送短信状态
//        recoverList.forEach(entry->{
//            //检查执行时间
//            try {
//                Date indate=DateUtil.parseDate(DateUtil.dateAdd(entry.getCreateTime(),48*60),"yyyy-MM-dd HH:mm:ss");
//                long timeLong=DateUtil.dateDiff("millsecond",indate,new Date());
//                if(timeLong<=999){//已经超出设定时间，马上再次发送短信
//                    //再次发提醒短信
//                    sendMsg(entry.getRecoveredPersonIdCard(),entry);
//                    //更新状态
//                    entry.setStatus(ApplicationInfoStatus.RESENT.getCode());
//                    this.updateById(entry);
//                    logger.debug("更新数据成功,id:"+entry.getId());
//                }else{//没超出时间，继续加入定时服务
//                    Date date=DateUtil.parseDate(DateUtil.dateAdd(entry.getCreateTime(),48*60),"yyyy-MM-dd HH:mm:ss");
//                    long l = date.getTime()-new Date().getTime();
//                    int hour=48-(((int)l)/(1000*60*60));//计算回收资源创建时间离重发消息的时间
//                    String expendTime="01:00:00";//00:00:00
//                    if(hour>0&&hour<10){
//                        expendTime="0"+hour+":00:00";
//                    }else if(hour>=10){
//                        expendTime=hour+":00:00";
//                    }
////                    User user=null;
////                    if(userMap.get(entry.getRecoveredPersonIdCard())!=null){
////                        user=userMap.get(entry.getRecoveredPersonIdCard());
////                    }else{
////                        user=userService.getById(entry.getRecoveredPersonIdCard());
////                        if(user!=null){
////                            userMap.put(user.getIdcard(),user);
////                        }
////                    }
//                    logger.debug("短信任务加入计时器，"+expendTime+"后重发提示短信。");
//                    //如果旧数据没有负责人信息，则不发短信
////                    if(user!=null) {
//                        timer.startRecoverCheck(DateUtil.dateAdd(entry.getCreateTime(), expendTime), entry.getRecoveredPersonIdCard(), entry);//发到定时器进行检查状态
////                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        });
    }

    /**
     * 通用发送回收资源短信
     * @param info
     */
    public void sendMsg(String userId,ResourceRecoverAppInfo info){
        //查出其中一个缩配资源的缩配时间
//        ResourceRecover resourceRecover=resourceRecoverService.getOne(new QueryWrapper<ResourceRecover>().eq("ref_Id",info.getId()));
//        if(resourceRecover==null){
//            return ;
//        }
//        //时间分割
//        String[] shrinkTime=null;
//        String month = "";
//        String day = "";
//        if(resourceRecover.getShrinkTime()!=null){
//            shrinkTime=resourceRecover.getShrinkTime().split(" ")[0].split("-");
//        }else{
//            shrinkTime=DateUtil.formateDate(info.getCreateTime(),"yyyy-MM-dd").split("-");
//        }
//        month = shrinkTime[1];
//        day = shrinkTime[2];
        //发送短信
//        messageProvider.sendMessageAsync(messageProvider.buildRecoverMessage(userId, BusinessName.RECOVER, info.getOrderNumber(),month,day));
        UserVO user = userApi.getUserById(userId);
        if(user!=null){
            smsApi.buildSuccessMessage(user.getIdcard(), BusinessName.RECOVER, info.getOrderNumber());
        }
    }
    /**
     * 通过回收资源查询短信提示时间,并传入map
     * @param info
     */
    public void getMsgTime(ResourceRecoverAppInfo info,Map<String,String> map){
        //查出其中一个缩配资源的缩配时间
        ResourceRecover resourceRecover=resourceRecoverService.getOne(new QueryWrapper<ResourceRecover>().eq("ref_Id",info.getId()));
        //时间分割
        String[] shrinkTime=null;
        String month = "";
        String day = "";
        if(resourceRecover!=null&&resourceRecover.getShrinkTime()!=null){
            shrinkTime=resourceRecover.getShrinkTime().split(" ")[0].split("-");
        }else{
            shrinkTime=DateUtil.formateDate(info.getCreateTime(),"yyyy-MM-dd").split("-");
        }
        month = shrinkTime[1];
        day = shrinkTime[2];
        map.put("month",month);
        map.put("day",day);
    }
}
