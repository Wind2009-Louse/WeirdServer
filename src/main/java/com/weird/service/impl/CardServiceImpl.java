package com.weird.service.impl;

import com.weird.mapper.main.*;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.UserCardListModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.BatchUpdateUserCardParam;
import com.weird.model.dto.CardHistoryDTO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.service.CardService;
import com.weird.utils.CacheUtil;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.weird.utils.CacheUtil.clearCardListCache;

@Service
@Slf4j
public class CardServiceImpl implements CardService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    UserCardListMapper userCardListMapper;

    @Autowired
    CardHistoryMapper cardHistoryMapper;

    /**
     * 修改用户持有的卡片数量
     *
     * @param userName 用户名
     * @param cardName 卡片名
     * @param count    新的卡片数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardCount(String userName, String cardName, int count) throws Exception {
        if (count > 3 || count < 0) {
            throw new OperationException("修改卡片数量错误，应在0~3内！");
        }

        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null) {
            throw new OperationException("找不到该用户:[%s]！", userName);
        }

        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到该卡片：[%s]！", cardName);
        }

        int userId = userModel.getUserId();
        int cardPk = cardModel.getCardPk();

        UserCardListModel model = userCardListMapper.selectByUserCard(userId, cardPk);
        if (model != null) {
            if (count == model.getCount()) {
                throw new OperationException("[%s]的卡片[%s]的数量没有变化！", userName, cardName);
            }
            log.warn("修改[{}]的[{}]数量（{}->{}）", userName, cardName, model.getCount(), count);
            model.setCount(count);
            clearCardListCache();
            return userCardListMapper.update(model) > 0;
        } else {
            if (count == 0) {
                throw new OperationException("[%s]的卡片[%s]的数量没有变化！", userName, cardName);
            }
            log.warn("添加[{}]的[{}]数量（{}->{}）", userName, cardName, 0, count);
            model = new UserCardListModel();
            model.setUserId(userId);
            model.setCardPk(cardPk);
            model.setCount(count);
            clearCardListCache();
            return userCardListMapper.insert(model) > 0;
        }
    }

    /**
     * 批量修改用户持有的卡片数量
     *
     * @param param 参数
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String updateCardCountBatch(BatchUpdateUserCardParam param) throws Exception {
        UserDataModel userModel = userDataMapper.selectByNameDistinct(param.getTarget());
        if (userModel == null) {
            throw new OperationException("找不到该用户:[%s]！", param.getTarget());
        }
        int userId = userModel.getUserId();
        String userName = userModel.getUserName();
        StringBuilder sb = new StringBuilder();

        List<UserCardListModel> insertList = new LinkedList<>();
        List<UserCardListModel> updateList = new LinkedList<>();
        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, Integer> entry : param.getCounts().entrySet()) {
            if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) {
                sb.append("数据为空！\n");
                continue;
            }
            if (entry.getValue() <= 0 || entry.getValue() >= 4) {
                sb.append(String.format("[%s]的持有量应在0-3！\n", entry.getKey()));
                continue;
            }
            PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(entry.getKey());
            if (cardModel == null) {
                sb.append(String.format("找不到该卡片：[%s]！\n", entry.getKey()));
                failCount++;
                continue;
            }
            int cardPk = cardModel.getCardPk();
            String cardName = cardModel.getCardName();

            UserCardListModel model = userCardListMapper.selectByUserCard(userId, cardPk);
            if (model != null) {
                if (entry.getValue().equals(model.getCount())) {
                    sb.append(String.format("[%s]的卡片[%s]的数量没有变化！\n", userName, cardName));
                    failCount++;
                    continue;
                }
                log.warn("修改[{}]的[{}]数量（{}->{}）", userName, cardName, model.getCount(), entry.getValue());
                model.setCount(entry.getValue());
                updateList.add(model);
            } else {
                if (entry.getValue() == 0) {
                    sb.append(String.format("[%s]的卡片[%s]的数量没有变化！\n", userName, cardName));
                    failCount++;
                    continue;
                }
                log.warn("添加[{}]的[{}]数量（{}->{}）", userName, cardName, 0, entry.getValue());
                model = new UserCardListModel();
                model.setUserId(userId);
                model.setCardPk(cardPk);
                model.setCount(entry.getValue());
                insertList.add(model);
            }
        }

        if (insertList.size() + updateList.size() > 0) {
            clearCardListCache();
            if (insertList.size() > 0) {
                int success = userCardListMapper.insertBatch(insertList);
                successCount += success;
                failCount += (insertList.size() - success);
            }
            for (UserCardListModel updateModel : updateList) {
                if (userCardListMapper.update(updateModel) > 0) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
        }
        sb.append(String.format("成功%d条数据，失败%d条数据。", successCount, failCount));

        clearCardListCache();
        return sb.toString();
    }

    /**
     * 管理端根据条件筛选所有卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    @Override
    public List<CardListDTO> selectListAdmin(String packageName, String cardName, String rare) {
        return userCardListMapper.selectCardListAdmin(packageName, cardName, rare);
    }

    /**
     * 玩家端根据条件筛选所有卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    @Override
    public List<CardListDTO> selectListUser(String packageName, String cardName, String rare) {
        return userCardListMapper.selectCardListUser(packageName, cardName, rare, 0);
    }


    /**
     * 根据条件筛选拥有的卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param userName    用户名
     * @return 查询结果
     */
    @Override
    public List<CardOwnListDTO> selectList(String packageName, String cardName, String rare, String userName) {
        String key = String.format("{%s,%s,%s,%s}", packageName, cardName, rare, userName);
        log.debug("查询卡片列表：{}", key);
        List<CardOwnListDTO> cache = CacheUtil.cardListCache.get(key);
        if (cache == null) {
            cache = userCardListMapper.selectCardOwnList(packageName, cardName, rare, userName);
            CacheUtil.cardListCache.put(key, cache);
        }
        return cache;
    }

    /**
     * 根据条件筛选卡片的历史记录
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    @Override
    public List<CardHistoryDTO> selectHistory(String packageName, String cardName, String rare) {
        List<Integer> packageIndexList;
        if (packageName.length() > 0) {
            List<PackageInfoModel> packageList = packageInfoMapper.selectByName(packageName);
            packageIndexList = packageList.stream().map(PackageInfoModel::getPackageId).collect(Collectors.toList());
        } else {
            packageIndexList = null;
        }
        List<Integer> cardIndexList = cardHistoryMapper.selectCardPk(packageIndexList, cardName, rare);
        if (cardIndexList.size() == 0) {
            return Collections.emptyList();
        } else {
            return cardHistoryMapper.selectByCardPk(cardIndexList);
        }
    }
}
