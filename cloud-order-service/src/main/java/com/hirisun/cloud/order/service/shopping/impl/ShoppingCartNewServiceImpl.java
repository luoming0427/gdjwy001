package com.hirisun.cloud.order.service.shopping.impl;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.hirisun.cloud.common.contains.RequestCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hirisun.cloud.api.daas.DaasApplicationApi;
import com.hirisun.cloud.api.saas.SaasApplicationApi;
import com.hirisun.cloud.api.system.FilesApi;
import com.hirisun.cloud.api.system.SystemApi;
import com.hirisun.cloud.api.workflow.WorkflowApi;
import com.hirisun.cloud.common.constant.RedisKey;
import com.hirisun.cloud.common.contains.ApplicationInfoStatus;
import com.hirisun.cloud.common.contains.ResourceType;
import com.hirisun.cloud.common.contains.WorkflowActivityStatus;
import com.hirisun.cloud.common.exception.CustomException;
import com.hirisun.cloud.common.util.AreaPoliceCategoryUtils;
import com.hirisun.cloud.common.util.IpUtil;
import com.hirisun.cloud.common.util.UUIDUtil;
import com.hirisun.cloud.common.vo.CommonCode;
import com.hirisun.cloud.model.app.param.SubpageParam;
import com.hirisun.cloud.model.file.FilesVo;
import com.hirisun.cloud.model.param.ActivityParam;
import com.hirisun.cloud.model.param.FilesParam;
import com.hirisun.cloud.model.shopping.vo.ShoppingCartVo;
import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.model.workflow.WorkflowActivityVO;
import com.hirisun.cloud.model.workflow.WorkflowInstanceVO;
import com.hirisun.cloud.model.workflow.WorkflowVO;
import com.hirisun.cloud.order.bean.apply.ApplyInfo;
import com.hirisun.cloud.order.bean.shopping.ShoppingCart;
import com.hirisun.cloud.order.continer.FormNum;
import com.hirisun.cloud.order.continer.HandlerWrapper;
import com.hirisun.cloud.order.continer.ShoppingCartStatus;
import com.hirisun.cloud.order.mapper.shopping.ShoppingCartMapper;
import com.hirisun.cloud.order.service.apply.ApplyInfoService;
import com.hirisun.cloud.order.service.shopping.ShoppingCartItemService;
import com.hirisun.cloud.order.service.shopping.ShoppingCartService;
import com.hirisun.cloud.order.vo.OrderCode;
import com.hirisun.cloud.order.vo.SubmitRequest;

@Service
public class ShoppingCartNewServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> 
	implements ShoppingCartService{

	private static final Logger logger = LoggerFactory.getLogger(ShoppingCartNewServiceImpl.class);
	
	@Autowired
	private FilesApi filesApi;
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
//	@Autowired
//	private IApplicationInfoService applicationInfoService;
	@Autowired
	private ApplyInfoService applyInfoService;
	@Autowired
	private SaasApplicationApi saasApplicationApi;
	@Autowired
	private DaasApplicationApi daasApplicationApi;
	@Autowired
	private WorkflowApi workflowApi;
	@Autowired
    private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SystemApi sysLogApi;
	@Autowired
	private ShoppingCartItemService shoppingCartItemService;
	
	@Transactional(rollbackFor = Throwable.class)
	public void create(UserVO user, ShoppingCartVo shoppingCartVo) {
		
		String formNum = shoppingCartVo.getFormNum();
		
		HandlerWrapper hw = FormNum.getHandlerWrapperByName(formNum);
		shoppingCartVo.setResourceType(hw.getFormNum().getResourceType().getCode());
		shoppingCartVo.setFormNum(hw.getFormNum().name());
		shoppingCartVo.setStatus(ShoppingCartStatus.WAIT_SUBMIT.getCode());
		shoppingCartVo.setCreatorIdCard(user.getIdcard());
		shoppingCartVo.setCreatorName(user.getName());
		
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartVo, shoppingCart);
		//保存购物车
		shoppingCartMapper.insert(shoppingCart);
		
		shoppingCartVo.setId(shoppingCart.getId());
		
        logger.debug("parseShoppingCart -> {}",shoppingCart);

        //保存购物车购物项
        shoppingCartItemService.saveShoppingCartItem(shoppingCartVo);
        
        //保存文件
        if(CollectionUtils.isNotEmpty(shoppingCart.getFileList())){
            logger.debug("ref file");
            refFiles(shoppingCart.getFileList(),shoppingCart.getId());
        }
	}

	
	
	private void refFiles(List<FilesVo> files, String refId) {
        if (StringUtils.isEmpty(refId)) {
            return;
        }
        
        SubpageParam param = new SubpageParam();
        param.setRefId(refId);
		filesApi.remove(param);
        
        if (files != null && !files.isEmpty()) {
            for (FilesVo f : files) {
                f.setId(null);
                f.setRefId(refId);
            }
            FilesParam filesParam = new FilesParam();
            param.setFiles(files);
            filesApi.saveBatch(filesParam);
        }
    }
	
	@Override
	public List<ShoppingCart> getShoppingCartList(String idCard, Long resourceType, String name) {

        List<ShoppingCart> shoppingCartList = Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            if(ResourceType.SAAS_SERVICE.getCode().equals(resourceType) ||  ResourceType.DAAS.getCode().equals(resourceType)){
                shoppingCartList = this.list(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getCreatorIdCard,idCard)
                        .eq(ShoppingCart::getResourceType,resourceType)
                        .like(ShoppingCart::getDsName,name).orderByDesc(ShoppingCart::getModifiedTime));
            }else {
                shoppingCartList = this.list(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getCreatorIdCard,idCard)
                        .eq(ShoppingCart::getResourceType,resourceType)
                        .like(ShoppingCart::getServiceTypeName,name).orderByDesc(ShoppingCart::getModifiedTime));
            }
        }else {
            shoppingCartList = this.list(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getCreatorIdCard,idCard)
                    .eq(ShoppingCart::getResourceType,resourceType).orderByDesc(ShoppingCart::getModifiedTime));
        }
        shoppingCartList.forEach(shoppingCart -> {
        	
        	ShoppingCartVo vo = new ShoppingCartVo(shoppingCart.getId(),shoppingCart.getFormNum());
        	List list = shoppingCartItemService.getByShoppingCartId(vo);
        	Integer totalNum = shoppingCartItemService.getTotalNumInShoppingCart(vo);
            shoppingCart.setServerList(list);
            shoppingCart.setTotalNum(totalNum);
        });
        return shoppingCartList;
    
	}

	@Override
	public ShoppingCart detail(String id) {
        ShoppingCart shoppingCart = this.getById(id);
        if(shoppingCart != null){
        	
        	ShoppingCartVo vo = new ShoppingCartVo(shoppingCart.getId(),shoppingCart.getFormNum());
        	List list = shoppingCartItemService.getByShoppingCartId(vo);
        	shoppingCart.setServerList(list);
        	
            SubpageParam param = new SubpageParam();
            param.setRefId(id);
			List<FilesVo> filesList = filesApi.find(param);
            
            shoppingCart.setFileList(filesList);
        }
        return shoppingCart;
    }

	@Transactional(rollbackFor = Throwable.class)
	public void update(ShoppingCart shoppingCart) {
        logger.debug("shoppingCart -> {}",shoppingCart);
        shoppingCart.setStatus(ShoppingCartStatus.WAIT_SUBMIT.getCode());
        shoppingCartMapper.updateById(shoppingCart);

        if(CollectionUtils.isNotEmpty(shoppingCart.getFileList())){
            refFiles(shoppingCart.getFileList(),shoppingCart.getId());
        }
        //更新购物车购物项
        ShoppingCartVo shoppingCartVo = new ShoppingCartVo();
        BeanUtils.copyProperties(shoppingCartVo, shoppingCartVo);
        shoppingCartItemService.updateShoppingCartItem(shoppingCartVo);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void delete(String ids) {

        if(StringUtils.isNotBlank(ids)){
            List<String> idList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(ids);
            if(CollectionUtils.isNotEmpty(idList)){
                idList.forEach(id ->{
                    ShoppingCart cart = this.getById(id);
                    if(cart != null){
                    	ShoppingCartVo shoppingCartVo = new ShoppingCartVo();
                    	BeanUtils.copyProperties(cart, shoppingCartVo);
                    	shoppingCartItemService.deleteItemByShoppingCartId(shoppingCartVo );
                    }
                });
                this.removeByIds(idList);
            }
        }
	}

	@Transactional(rollbackFor = Throwable.class)
	public void submit(UserVO user, SubmitRequest submitRequest) throws Exception {
		
        logger.debug("submitRequest -> {}",submitRequest);
        //订单公共基本信息
        ApplyInfo baseInfo = submitRequest.convertToApplicationInfo();
        logger.debug("baseInfo -> {}",baseInfo);
        List<ShoppingCart> shoppingCartItems;
        //获取提交购物车集合
        if(StringUtils.equals("all",submitRequest.getShoppingCartIds())){
            shoppingCartItems = this.list(new QueryWrapper<ShoppingCart>().lambda()
                                            .eq(ShoppingCart::getCreatorIdCard,user.getIdcard())
                                            .eq(ShoppingCart::getStatus,ShoppingCartStatus.WAIT_SUBMIT.getCode()));
        }else {
            shoppingCartItems = getShoppingCartItems(user.getIdcard(),submitRequest.getShoppingCartIds());
        }
        if(StringUtils.isBlank(submitRequest.getShoppingCartIds())){
            throw new CustomException(CommonCode.ITEM_NULL);
        }
        List<String> idList;
        if(StringUtils.equals("all",submitRequest.getShoppingCartIds())){
            idList = shoppingCartItems.stream().map(ShoppingCart::getId).distinct().collect(Collectors.toList());
        }else {
            idList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(submitRequest.getShoppingCartIds());
        }

        List<ApplyInfo> infoList = Lists.newArrayList();

        //处理需要合并的DaaS订单
        merge(user,shoppingCartItems,baseInfo,infoList);

        logger.debug("after merge DS -> {}",infoList.size());
        logger.debug("after merge baseInfo -> {}",baseInfo);
        int dsNum = infoList.size();

        List<ShoppingCart> shoppingCartListWithoutDS = shoppingCartItems.stream().filter(item->
               /*! StringUtils.equals(FormNum.SAAS_SERVICE.toString(),item.getFormNum()) && */!StringUtils.equals(FormNum.DAAS.toString(),item.getFormNum())).distinct().collect(Collectors.toList());

        List<String> withoutDsIdList = shoppingCartListWithoutDS.stream().map(ShoppingCart::getId).distinct().collect(Collectors.toList());


        logger.debug("shoppingCartListWithoutDS -> {}",shoppingCartListWithoutDS.size());
        int withoutDsNum = shoppingCartListWithoutDS.size();

        //对非DS先将文件关联到每个购物车
        List<FilesVo> fileList  = submitRequest.getFileList();
        if(CollectionUtils.isNotEmpty(fileList)){
            List<FilesVo> splitFileList = Lists.newArrayList();
            for(String id:withoutDsIdList){
                for(FilesVo f:fileList){
                	FilesVo nf =  new FilesVo();
                    BeanUtils.copyProperties(f,nf);
                    nf.setId(null);
                    nf.setRefId(id);
                    logger.debug("File nf -> {}",nf);
                    splitFileList.add(nf);
                }
            }
            FilesParam param = new FilesParam();
            param.setFiles(splitFileList);
			filesApi.saveBatch(param);
            logger.debug("id size -> {} fileList size ->{} splitFileList -> {}",withoutDsIdList.size(),fileList.size(),splitFileList.size());
            //删除原记录
            List<String> originFileId = fileList.stream().map(FilesVo::getId).distinct().collect(Collectors.toList());
            param.setFilesIdList(originFileId);
            filesApi.deleteBatch(param);
        }
        //IaaS 和 PaaS、SaaS购物车 分单（遍历IaaS PaaS SaaS购物车）
        for (ShoppingCart shoppingCart:shoppingCartListWithoutDS){
        	
        	ApplyInfo info = configAndSaveBaseInfo(user,shoppingCart,baseInfo);
        	infoList.add(info);
        	updateFiles(info.getId(),shoppingCart.getId());
            ShoppingCartVo shoppingCartVo = new ShoppingCartVo();
        	BeanUtils.copyProperties(shoppingCart, shoppingCartVo);
        	shoppingCartVo.setAppInfoId(info.getId());
        	shoppingCartItemService.refAppInfoFromShoppingCart(shoppingCartVo);
            
            //新生成的订单发起流程
           workflowApi.launchInstanceOfWorkflow(user.getIdcard(),info.getWorkFlowId(),info.getId());
           
        }

        logger.debug("after deal without DS -> {}",infoList.size());
        //提交审核

        if(infoList.size() != dsNum + withoutDsNum){
            throw new CustomException(CommonCode.ORDER_SPLIT_ERROR);
        }
        Date now = new Date();
        //订单提交
        for(ApplyInfo info:infoList){
            if ("1".equals(info.getDraft())) {
            	throw new CustomException(CommonCode.DRAFT_ERROR);
            }
            sysLogApi.saveLog(user.getIdcard(),"服务名称："+info.getServiceTypeName()+";申请单号："+info.getOrderNumber(),"提交申请", IpUtil.getIp());

            WorkflowInstanceVO instance = workflowApi.getWorkflowInstanceByBusinessId(info.getId());
            if (null==instance){
                throw new CustomException(CommonCode.FLOW_INSTANCE_NULL_ERROR);
            }
            
            ActivityParam param = new ActivityParam();
            param.setActivitystatus(0);
            param.setIsstart(0);
            param.setInstanceId(instance.getId());
            
            info.setCreateTime(now);
            info.setModifiedTime(now);
            if ("kx".equals(submitRequest.getType())) {
                //科信待审核
                info.setStatus(ApplicationInfoStatus.REVIEW.getCode());
            }else {
                //部门内待审核
                info.setStatus(ApplicationInfoStatus.INNER_REVIEW.getCode());
            }
            Map<String,String> map = new HashMap<>();
            map.put("name",info.getServiceTypeName());
            map.put("order",info.getOrderNumber());
            map.put("depApproveUserIds",submitRequest.getUserIds());//申请后一个流程处理人
            WorkflowActivityVO firstActivity = workflowApi.getOneWorkflowActivityByParams(WorkflowActivityStatus.WAITING.getCode(),instance.getId());
            if (firstActivity==null) {
                logger.info("未找到对应的流程活动信息！");
                throw new CustomException(OrderCode.WORKFLOW_ACTIVITY_MISSING);
            }
            Map resultMap = workflowApi.advanceActivity(firstActivity.getId(),map);
            if (!RequestCode.SUCCESS.getCode().equals(resultMap.get("code"))) {
                throw new CustomException(OrderCode.FEIGN_METHOD_ERROR);
            }
        }
        applyInfoService.updateBatchById(infoList);
        //删除购物车
        this.removeByIds(idList);
	}
	
	/**
     * 更新文件关联关系
     * @param appInfoId
     * @param shoppingCartId
     */
    private  void updateFiles(String appInfoId,String shoppingCartId){
        FilesParam param = new FilesParam();
        param.setRefId(shoppingCartId);
        param.setNewRefId(appInfoId);
		filesApi.updateFileRef(param );
    
    }
	
	/**
     * 处理需要合并的DaaS购物车资源:数据服务(DaaS)和数据资源(DaaS)分开合并处理
     * @param allItems 全部购物车
	 * @throws Exception 
     */
    private void merge(UserVO user,List<ShoppingCart> allItems,
    		ApplyInfo baseInfo,List<ApplyInfo> infoList) throws Exception{

        //获取数据资源(DaaS)购物车
        List<ShoppingCart> daaSResource = getDaaSResource(allItems);
        if(CollectionUtils.isNotEmpty(daaSResource)){
        	ApplyInfo info = dealMerge(user,daaSResource,baseInfo);
            infoList.add(info);
            workflowApi.launchInstanceOfWorkflow(user.getIdcard(),info.getWorkFlowId(),info.getId());
        }
        //获取数据服务(DaaS)购物车
        List<ShoppingCart> daaSService = getDaaSService(allItems);
        if(CollectionUtils.isNotEmpty(daaSService)){
            //数据服务(DaaS)没有大数据集群账号和使用资源的虚拟机IP
            baseInfo.setClusterAccount(null);
            baseInfo.setVmIp(null);
            ApplyInfo info = dealMerge(user,daaSService,baseInfo);
            infoList.add(info);
            workflowApi.launchInstanceOfWorkflow(user.getIdcard(),info.getWorkFlowId(),info.getId());
        }
    }
	
    /**
     * 获取DaaS服务购物车
     * @param allItems 全部购物车
     * @return DaaS服务购物车
     */
    private List<ShoppingCart> getDaaSService(List<ShoppingCart> allItems){

        List<ShoppingCart> daaSService = allItems.stream().filter(item -> StringUtils.equals(FormNum.DAAS.toString(),item.getFormNum()) && StringUtils.equals("数据服务(DaaS)",item.getServiceTypeName())).collect(Collectors.toList());
        return daaSService;
    }
    
    /**
     * 处理合并
     * @param shoppingCartList DAAS或SAAS购物车集合
     * @param baseInfo 订单基本信息
     * @throws Exception 
     */
    private ApplyInfo dealMerge(UserVO user,List<ShoppingCart> shoppingCartList,
    		ApplyInfo baseInfo) throws Exception{
        if(CollectionUtils.isNotEmpty(shoppingCartList)){
            //取第一个DaaS/SaaS购物车
            ShoppingCart shoppingCart = shoppingCartList.get(0);
            //以第一个DaaS/SaaS购物车创建订单
            ApplyInfo info = configAndSaveBaseInfo(user,shoppingCart,baseInfo);
            logger.debug("DS info -> {}",info);
            //applicationInfoService.save(info);
            //获取DaaS/SaaS购物车的所有ID
            List<String> idList = shoppingCartList.stream().map(ShoppingCart::getId).distinct().collect(Collectors.toList());
            //处理服务关联关系
            dealRef(info,idList);
            return info;
        }
        return null;
    }
    
    /**
     * 订单关联信息处理
     * @param info
     * @param shoppingCartIdList
     */
    private void dealRef(ApplyInfo info,List<String> shoppingCartIdList){
        //文件
        refFiles(info.getFileList(), info.getId());
        if(StringUtils.equals(FormNum.DAAS.toString(),info.getFormNum())){
        	daasApplicationApi.submitMerge(info.getId(),shoppingCartIdList);
        }else if(StringUtils.equals(FormNum.SAAS_SERVICE.toString(),info.getFormNum())){
        	saasApplicationApi.submitMerge(info.getId(),shoppingCartIdList);
        }
    }
    
    /**
     * 配置和保存订单
     * @param shoppingCart
     * @param baseInfo
     * @throws Exception 
     */
    private ApplyInfo configAndSaveBaseInfo(UserVO user,
    		ShoppingCart shoppingCart,ApplyInfo baseInfo) throws Exception{
    	
        HandlerWrapper hw = FormNum.getHandlerWrapperByName(shoppingCart.getFormNum());
        ApplyInfo info = new ApplyInfo();
        BeanUtils.copyProperties(baseInfo,info);
        info.setId(UUIDUtil.getUUID());
        logger.debug("ID -> {}",info.getId());
        info.setCreator(user.getIdcard());
        info.setCreatorName(user.getName());
        info.setStatus(ApplicationInfoStatus.SHOPPING_CART.getCode());

        //iaas、paas和saas申请从购物车中取申请说明，daas从申请单中取申请说明
        //1为iaas，2为daas，3为paas，5为saas
        if (shoppingCart.getResourceType() == 2) {
            info.setExplanation(baseInfo.getExplanation());
        }else {
            info.setExplanation(shoppingCart.getExplanation());
        }

//        info.setFlowStepId(null);
//        info.setFlowStepIdBak(null);
        info.setServiceTypeId(shoppingCart.getServiceTypeId());
        info.setServiceTypeName(shoppingCart.getServiceTypeName());
        ResourceType resourceType = hw.getFormNum().getResourceType();
        //流程选择（重要选择流程逻辑）
        WorkflowVO workflow = workflowApi.chooseWorkFlow(resourceType.getCode(), info.getServiceTypeId(),
        		info.getAreaName(), info.getPoliceCategory(), info.getNationalSpecialProject());
        if(workflow == null){
            logger.error("购物车ID:{} 资源类型:{} 地市:{} 警种: {} 服务ID:{} 国家专项:{}",shoppingCart.getId(),resourceType.toString(),info.getAreaName(),info.getPoliceCategory(),info.getServiceTypeId(),info.getNationalSpecialProject());
            throw  new Exception("资源类型: "+resourceType.toString()+ "地市: "+ info.getAreaName()+"警种: "
            		+info.getPoliceCategory()+ "服务ID: "+info.getServiceTypeId()+"国家专项: "
            		+info.getNationalSpecialProject()+"无匹配流程");
        }
        info.setWorkFlowId(workflow.getId());
        info.setResourceType(resourceType.getCode());
        info.setFormNum(shoppingCart.getFormNum());
        info.setOrderNumber(genOrderNum());
        info.setHwPoliceCategory(AreaPoliceCategoryUtils.getPoliceCategory(baseInfo.getAppName()));
        applyInfoService.save(info);
        return info;
    }
    
    private String genOrderNum() {
        // 生成单号
        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String redisKey = RedisKey.KEY_ORDER_NUM_PREFIX + yyyyMMdd;
        Long increment = stringRedisTemplate.opsForValue().increment(redisKey, 1L);
        if (increment == null) {
            throw new CustomException(CommonCode.ORDER_NUMBER_ERROR);
        }
        // 过期时间1天
        stringRedisTemplate.expire(redisKey, 1L, TimeUnit.DAYS);
        return String.format("%s%04d", yyyyMMdd, increment);
    }
    
    /**
     * 获取DaaS服务购物车
     * @param allItems 全部购物车
     * @return DaaS服务购物车
     */
    private List<ShoppingCart> getDaaSResource(List<ShoppingCart> allItems){

        List<ShoppingCart> daaSResource = allItems.stream().filter(item -> StringUtils.equals(FormNum.DAAS.toString(),item.getFormNum()) && StringUtils.equals("数据资源(DaaS)",item.getServiceTypeName())).collect(Collectors.toList());
        return daaSResource;
    }

	/**
     * 获取当前用户选择Item中，待提交状态的Item。
     * @param idCard 当前用户身份证号
     * @param ids 购物车Items的Id字符串
     * @return 购物车Item实体List
     */
    private List<ShoppingCart> getShoppingCartItems(String idCard,String ids){
        if(StringUtils.isBlank(ids)){
            logger.debug("shoppingCartIds -> {}",ids);
            throw new CustomException(CommonCode.SHOPPINGCAR_ID_NULL);
        }
        List<String> idList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(ids);

       return this.list(new QueryWrapper<ShoppingCart>().lambda()
                                                    .eq(ShoppingCart::getCreatorIdCard,idCard)
                                                    .eq(ShoppingCart::getStatus,ShoppingCartStatus.WAIT_SUBMIT.getCode())
                                                    .in(ShoppingCart::getId,idList));


    }
	
	/**
     * 各类资源购物车数目
     *
     * @return map
     */
	public Map<String, Integer> getNumGroupByType(String idCard) {
        int iaas = this.count(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getResourceType,ResourceType.IAAS.getCode()).eq(ShoppingCart::getCreatorIdCard,idCard));

        int paas = this.count(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getResourceType,ResourceType.PAAS.getCode()).eq(ShoppingCart::getCreatorIdCard,idCard));
        int saas = this.count(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getResourceType,ResourceType.SAAS_SERVICE.getCode()).eq(ShoppingCart::getCreatorIdCard,idCard));

        int daasService = this.count(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getResourceType,ResourceType.DAAS.getCode()).eq(ShoppingCart::getCreatorIdCard,idCard).eq(ShoppingCart::getServiceTypeName,"数据服务(DaaS)"));
        int daasResource = this.count(new QueryWrapper<ShoppingCart>().lambda().eq(ShoppingCart::getResourceType,ResourceType.DAAS.getCode()).eq(ShoppingCart::getCreatorIdCard,idCard).eq(ShoppingCart::getServiceTypeName,"数据资源(DaaS)"));

        Map<String,Integer> result = Maps.newHashMapWithExpectedSize(7);
        result.put("iaas",iaas);
        result.put("paas",paas);
        result.put("daasService",daasService);
        result.put("daasResource",daasResource);
        result.put("saas",saas);

        return result;
    }

	@Override
	public Integer count(String idCard) {
		return this.count(new QueryWrapper<ShoppingCart>()
        		.lambda().eq(ShoppingCart::getCreatorIdCard,idCard));
	}

}
