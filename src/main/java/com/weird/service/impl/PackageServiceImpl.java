package com.weird.service.impl;

import com.alibaba.fastjson.JSON;
import com.weird.mapper.main.CardHistoryMapper;
import com.weird.mapper.main.PackageCardMapper;
import com.weird.mapper.main.PackageInfoMapper;
import com.weird.model.CardHistoryModel;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.param.BatchAddCardParam;
import com.weird.service.PackageService;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.weird.utils.CacheUtil.clearCardOwnListCache;
import static com.weird.utils.CacheUtil.clearRollListWithDetailCache;

/**
 * 卡包Service实现
 *
 * @author Nidhogg
 */
@Service
@Slf4j
public class PackageServiceImpl implements PackageService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    CardHistoryMapper cardHistoryMapper;

    /**
     * 根据名称查找卡包列表
     *
     * @param packageName 卡包名
     * @return 卡包列表
     */
    @Override
    public List<PackageInfoModel> selectByName(String packageName) throws Exception {
        return packageInfoMapper.selectByName(packageName);
    }

    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addPackage(String name) throws Exception {
        // 查找是否重名
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(name);
        if (oldPackage != null) {
            throw new OperationException("卡包[%s]已存在！", name);
        }
        PackageInfoModel newPackage = new PackageInfoModel();
        newPackage.setPackageName(name);
        log.warn("添加卡包：[{}]", name);
        return packageInfoMapper.insert(newPackage) > 0;
    }

    /**
     * 更新卡包名称
     *
     * @param oldName 旧卡包名称
     * @param newName 新卡包名称
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updatePackageName(String oldName, String newName) throws Exception {
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(oldName);
        if (oldPackage == null) {
            throw new OperationException("找不到该卡包：[%s]！", oldName);
        }
        PackageInfoModel newPackage = packageInfoMapper.selectByNameDistinct(newName);
        if (newPackage != null) {
            throw new OperationException("[%s]已存在！", newName);
        }

        oldPackage.setPackageName(newName);
        log.warn("卡包更改名字：[{}]->[{}]", oldName, newName);
        clearCardOwnListCache();
        clearRollListWithDetailCache();
        return packageInfoMapper.updateByPrimaryKey(oldPackage) > 0;
    }

    /**
     * 在卡包中添加卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addCard(String packageName, String cardName, String rare) throws Exception {
        // 查找卡包是否存在
        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到该卡包：[%s]！", packageName);
        }
        int packageId = packageModel.getPackageId();

        // 查找卡片是否存在
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel != null) {
            throw new OperationException("卡片[%s]已存在！", cardName);
        }

        // 添加
        PackageCardModel newCardModel = new PackageCardModel();
        newCardModel.setCardName(cardName);
        newCardModel.setPackageId(packageId);
        newCardModel.setRare(rare);
        log.warn("在卡包[{}]中添加卡片[{}]({})", packageName, cardName, rare);
        return packageCardMapper.insert(newCardModel) > 0;
    }

    /**
     * 在卡包中批量添加卡片
     *
     * @param param 批量添加参数
     * @return 返回结果
     */
    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public String addCardList(BatchAddCardParam param, List<String> allCardList) {
        PackageInfoModel packageInfoModel = packageInfoMapper.selectByNameDistinct(param.getPackageName());
        if (packageInfoModel == null) {
            return String.format("找不到卡包：%s！", param.getPackageName());
        }

        List<String> existsList = packageCardMapper.selectByNameListDistinct(allCardList);
        if (existsList.size() > 0) {
            return String.format("以下卡片已在卡池中：%s！", JSON.toJSONString(existsList));
        }

        StringBuilder sb = new StringBuilder();
        if (param.getNList().size() > 0) {
            int nCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "N", param.getNList());
            if (nCount < param.getNList().size()) {
                sb.append("N卡没有全部更新，请重试！\n");
            }
        }
        if (param.getRList().size() > 0) {
            int rCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "R", param.getRList());
            if (rCount < param.getRList().size()) {
                sb.append("R卡没有全部更新，请重试！\n");
            }
        }
        if (param.getSrList().size() > 0) {
            int srCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "SR", param.getSrList());
            if (srCount < param.getSrList().size()) {
                sb.append("SR卡没有全部更新，请重试！\n");
            }
        }
        if (param.getUrList().size() > 0) {
            int urCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "UR", param.getUrList());
            if (urCount < param.getUrList().size()) {
                sb.append("UR卡没有全部更新，请重试！\n");
            }
        }
        if (param.getHrList().size() > 0) {
            int hrCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "HR", param.getHrList());
            if (hrCount < param.getHrList().size()) {
                sb.append("HR卡没有全部更新，请重试！\n");
            }
        }
        if (param.getGrList().size() > 0) {
            int grCount = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), "GR", param.getGrList());
            if (grCount < param.getGrList().size()) {
                sb.append("GR卡没有全部更新，请重试！\n");
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        log.warn("批量添加卡片：{}", param);
        return "";
    }

    /**
     * 修改卡片名字
     *
     * @param oldName 旧卡名
     * @param newName 新卡名
     * @param isShow  是否在历史记录中显示该卡片
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardName(String oldName,
                                  String newName,
                                  int isShow) throws Exception {
        // 查找是否能否更新
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(oldName);
        if (cardModel == null) {
            throw new OperationException("卡片[%s]不存在！", oldName);
        }
        PackageCardModel goalModel = packageCardMapper.selectByNameDistinct(newName);
        if (goalModel != null) {
            throw new OperationException("卡片[%s]已存在！", newName);
        }

        // 修改
        log.warn("卡片[{}]重命名为[{}]", oldName, newName);
        cardModel.setCardName(newName);
        int result = packageCardMapper.updateByPrimaryKey(cardModel);
        if (result > 0) {
            if (isShow != 0) {
                CardHistoryModel cardHistory = new CardHistoryModel();
                cardHistory.setPackageId(cardModel.getPackageId());
                cardHistory.setCardPk(cardModel.getCardPk());
                cardHistory.setOldName(oldName);
                cardHistory.setNewName(newName);
                cardHistory.setRare(cardModel.getRare());
                cardHistoryMapper.insert(cardHistory);
            }

            clearCardOwnListCache();
            clearRollListWithDetailCache();
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean exchangeCardName(String name1, String name2, int isShow) throws Exception {
        // 查找是否能否更新
        PackageCardModel cardModel1 = packageCardMapper.selectByNameDistinct(name1);
        if (cardModel1 == null) {
            throw new OperationException("卡片[%s]不存在！", name1);
        }
        PackageCardModel cardModel2 = packageCardMapper.selectByNameDistinct(name2);
        if (cardModel2 == null) {
            throw new OperationException("卡片[%s]不存在！", name1);
        }
        if (cardModel1.getRare().equals(cardModel2.getRare())) {
            throw new OperationException("卡片[%s]和[%s]的稀有度相同！", name1, name2);
        }
        if (cardModel1.getPackageId() != cardModel2.getPackageId()) {
            throw new OperationException("卡片[%s]和[%s]不在同一卡包！", name1, name2);
        }

        // 修改
        log.warn("卡片[{}]({})、[{}]({})稀有度互换",
                cardModel1.getCardName(), cardModel1.getRare(),
                cardModel2.getCardName(), cardModel2.getRare());
        String tempName = cardModel1.getCardName();
        cardModel1.setCardName(cardModel2.getCardName());
        cardModel2.setCardName(tempName);
        int result = packageCardMapper.updateByPrimaryKey(cardModel1) + packageCardMapper.updateByPrimaryKey(cardModel2);
        if (result == 2) {
            if (isShow != 0) {
                CardHistoryModel cardHistory1 = new CardHistoryModel();
                cardHistory1.setPackageId(cardModel1.getPackageId());
                cardHistory1.setCardPk(cardModel1.getCardPk());
                cardHistory1.setOldName(name1);
                cardHistory1.setNewName(name2);
                cardHistory1.setRare(cardModel1.getRare());
                cardHistoryMapper.insert(cardHistory1);

                CardHistoryModel cardHistory2 = new CardHistoryModel();
                cardHistory2.setPackageId(cardModel2.getPackageId());
                cardHistory2.setCardPk(cardModel2.getCardPk());
                cardHistory2.setOldName(name2);
                cardHistory2.setNewName(name1);
                cardHistory2.setRare(cardModel2.getRare());
                cardHistoryMapper.insert(cardHistory2);
            }

            clearCardOwnListCache();
            clearRollListWithDetailCache();
            return true;
        }
        return false;
    }

    /**
     * 对卡包进行排序
     *
     * @param packageList 卡包ID列表
     * @return 排序结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String sort(List<Integer> packageList) throws OperationException {
        int orderNum = 1;
        for (Integer packageId : packageList) {
            if (packageId == null) {
                continue;
            }
            int count = packageInfoMapper.updateOrder(packageId, orderNum);
            if (count <= 0) {
                throw new OperationException("找不到卡包ID：[%d]！", packageId);
            }
            orderNum ++;
        }
        return "修改排序成功！";
    }
}
