package com.weird.service.impl;

import com.alibaba.fastjson.JSON;
import com.weird.config.AutoConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.facade.RecordFacade;
import com.weird.mapper.main.PackageCardMapper;
import com.weird.mapper.main.RouletteMapper;
import com.weird.mapper.main.UserCardListMapper;
import com.weird.mapper.main.UserDataMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.RouletteConfigModel;
import com.weird.model.UserCardListModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.RouletteConfigDTO;
import com.weird.model.dto.RouletteHistoryDTO;
import com.weird.model.dto.RouletteResultDTO;
import com.weird.model.param.PageParam;
import com.weird.service.RouletteService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.weird.utils.CacheUtil.clearCardOwnListCache;

/**
 * 转盘服务实现
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Service
@Slf4j
public class RouletteServiceImpl implements RouletteService {
    @Autowired
    RouletteMapper rouletteMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    UserCardListMapper userCardListMapper;

    @Autowired
    RecordFacade recordFacade;

    @Autowired
    BroadcastFacade broadcastFacade;

    static final Pattern colorPattern = Pattern.compile("^#[0-9a-fA-F]{6}$");

    static final Pattern DpPattern = Pattern.compile("^(\\d+)[\\s]*[Dd][Pp]");

    @Override
    public List<RouletteConfigDTO> listConfig() {
        return rouletteMapper.selectConfigList();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String updateConfig(List<RouletteConfigDTO> list, String operator) throws OperationException {
        if (CollectionUtils.isEmpty(list)) {
            throw new OperationException("配置项不能为空！");
        }
        for (RouletteConfigDTO config : list) {
            if (config == null) {
                throw new OperationException("存在空配置！");
            }
            if (config.getRate() <= 0) {
                throw new OperationException("比例需要大于0！");
            }
            if (!colorPattern.matcher(config.getColor()).matches()) {
                throw new OperationException("[%s]不符合颜色要求！", config.getColor());
            }
        }
        recordFacade.setRecord(operator,
                "转盘配置修改为%s",
                JSON.toJSONString(list));
        List<RouletteConfigModel> modelList = BeanConverter.convertList(list, RouletteConfigModel.class);
        rouletteMapper.clearConfig();
        int count = rouletteMapper.batchInsertConfig(modelList);
        if (count > 0) {
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public RouletteResultDTO roulette(String userName) throws OperationException {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(userName);
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", userName);
        }

        Byte isAdmin = user.getIsAdmin();
        int rouletteCount = (isAdmin > 0) ? 1 : user.getRoulette();
        if (rouletteCount <= 0) {
            throw new OperationException("你的转盘次数已用完！");
        }

        List<RouletteConfigDTO> configList = rouletteMapper.selectConfigList();
        if (CollectionUtils.isEmpty(configList)) {
            throw new OperationException("当前没有转盘配置，请联系管理员！");
        }
        final int rateSum = configList.stream().mapToInt(RouletteConfigDTO::getRate).sum();

        Random rd = new Random();
        int randRate = rd.nextInt(rateSum);
        int currentRate = randRate;

        // 根据随机值判断转盘结果
        for (int index = 0; index < configList.size(); ++index) {
            RouletteConfigDTO config = configList.get(index);
            currentRate -= config.getRate();
            if (currentRate >= 0) {
                continue;
            }

            // 生成结果
            RouletteResultDTO result = new RouletteResultDTO();
            result.setIndex(index);
            result.setResult(String.format("抽奖结果：%s", config.getDetail()));
            try {
                rouletteMapper.addHistory(userName, config.getDetail());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            // 更新用户转盘数据
            if (user.getIsAdmin() <= 0) {
                user.setRoulette(rouletteCount - 1);
                Matcher matcher = DpPattern.matcher(config.getDetail());
                if (matcher.matches()) {
                    if (AutoConfig.fetchDp()) {
                        String dpStr = matcher.group(1);
                        int incDp = Integer.parseInt(dpStr);
                        user.setDuelPoint(user.getDuelPoint() + incDp);
                    }
                } else {
                    PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(config.getDetail());
                    if (cardModel != null) {
                        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(user.getUserId(), cardModel.getCardPk());
                        if (cardListModel == null) {
                            cardListModel = new UserCardListModel();
                            cardListModel.setUserId(user.getUserId());
                            cardListModel.setCardPk(cardModel.getCardPk());
                            cardListModel.setCount(1);
                            if (userCardListMapper.insert(cardListModel) <= 0) {
                                log.error("插入转盘卡片：{}时失败", config.getDetail());
                            }
                        } else {
                            cardListModel.setCount(cardListModel.getCount() + 1);
                            if (userCardListMapper.update(cardListModel) <= 0) {
                                log.error("更新转盘卡片：{}时失败", config.getDetail());
                            }
                        }
                        clearCardOwnListCache();
                    }
                }

                int updateCount = userDataMapper.updateByPrimaryKey(user);
                if (updateCount <= 0) {
                    throw new OperationException("用户数据更新失败！");
                }
            }

            // 发送广播
            recordFacade.setRecord(userName, "转盘抽奖结果:%s(%d),当前次数:%d->%d",
                    config.getDetail(), randRate, rouletteCount, rouletteCount - 1);
            try {
                String concatStr = "";
                if (rateSum / config.getRate() >= 20) {
                    concatStr = "，真是羡煞旁人啊";
                }
                String broadcastMsg = String.format(
                        "【广播】恭喜 %s 通过转盘抽奖获得 %s%s！",
                        userName,
                        config.getDetail(),
                        concatStr
                );
                broadcastFacade.sendMsgAsync(broadcastMsg, 5000);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            return result;
        }
        throw new OperationException("转盘失败，请重试！");
    }

    @Override
    public PageResult<RouletteHistoryDTO> history(PageParam param) throws Exception {
        PageResult<RouletteHistoryDTO> result = new PageResult<>();
        int count = rouletteMapper.countHistory();
        List<RouletteHistoryDTO> historyList = Collections.emptyList();
        if (count > 0) {
            historyList = rouletteMapper.searchHistory();
        }
        result.addPageInfo(historyList, param.getPage(), param.getPageSize());
        return result;
    }
}
