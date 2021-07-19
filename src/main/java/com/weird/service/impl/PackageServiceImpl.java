package com.weird.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.weird.mapper.main.CardHistoryMapper;
import com.weird.mapper.main.PackageCardMapper;
import com.weird.mapper.main.PackageInfoMapper;
import com.weird.model.CardHistoryModel;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.param.BatchAddCardParam;
import com.weird.service.PackageService;
import com.weird.service.RecordService;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    RecordService recordService;

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
    public boolean addPackage(String name, String operator) throws Exception {
        // 查找是否重名
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(name);
        if (oldPackage != null) {
            throw new OperationException("卡包[%s]已存在！", name);
        }
        PackageInfoModel newPackage = new PackageInfoModel();
        newPackage.setPackageName(name);
        recordService.setRecord(operator, "添加卡包：[%s]", name);
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
    public boolean updatePackageName(String oldName, String newName, String operator) throws Exception {
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(oldName);
        if (oldPackage == null) {
            throw new OperationException("找不到该卡包：[%s]！", oldName);
        }
        PackageInfoModel newPackage = packageInfoMapper.selectByNameDistinct(newName);
        if (newPackage != null) {
            throw new OperationException("[%s]已存在！", newName);
        }

        oldPackage.setPackageName(newName);
        recordService.setRecord(operator, "卡包更改名字：[%s]->[%s]", oldName, newName);
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
    public boolean addCard(String packageName, String cardName, String rare, String operator) throws Exception {
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
        recordService.setRecord(operator, "在卡包[%s]中添加卡片[%s](%s)", packageName, cardName, rare);
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
        Map<String, List<String>> cardMap = new LinkedHashMap<>();
        cardMap.put("N", param.getNList());
        cardMap.put("R", param.getRList());
        cardMap.put("SR", param.getSrList());
        cardMap.put("UR", param.getUrList());
        cardMap.put("HR", param.getHrList());
        cardMap.put("GR", param.getGrList());
        cardMap.put("SER", param.getSerList());
        for (Map.Entry<String, List<String>> entry : cardMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                int count = packageCardMapper.insertByRareBatch(packageInfoModel.getPackageId(), entry.getKey(), entry.getValue());
                if (count < entry.getValue().size()) {
                    sb.append(entry.getKey());
                    sb.append("卡没有全部更新，请重试！\n");
                }
                recordService.setRecord(
                        param.getName(),
                        "[%s]添加%s卡：%s", param.getPackageName(), entry.getKey(), JSON.toJSONString(entry.getValue()));
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        return "";
    }

    /**
     * 修改卡片名字
     *
     * @param oldName 旧卡名
     * @param newName 新卡名
     * @param newRare 新稀有度
     * @param isShow  是否在历史记录中显示该卡片
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardName(String oldName,
                                  String newName,
                                  String newRare,
                                  int isShow,
                                  String operator) throws Exception {
        boolean isUpdated = false;
        // 查找是否能否更新
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(oldName);
        if (cardModel == null) {
            throw new OperationException("卡片[%s]不存在！", oldName);
        }
        String oldRare = cardModel.getRare();

        if (StringUtils.isEmpty(newName)) {
            newName = oldName;
        }
        if (!StringUtils.equals(oldName, newName)) {
            PackageCardModel goalModel = packageCardMapper.selectByNameDistinct(newName);
            if (goalModel != null) {
                throw new OperationException("卡片[%s]已存在！", newName);
            }
            isUpdated = true;
        }
        if (StringUtils.isEmpty(newRare)) {
            newRare = oldRare;
        }
        if (!StringUtils.equals(oldRare, newRare)) {
            if (!PackageUtil.RARE_LIST.contains(newRare)) {
                throw new OperationException("稀有度[%s]不合法！", newRare);
            }
            isUpdated = true;
        }
        if (!isUpdated) {
            throw new OperationException("卡片未修改！");
        }

        // 修改
        cardModel.setCardName(newName);
        cardModel.setRare(newRare);
        int result = packageCardMapper.updateByPrimaryKey(cardModel);
        if (result > 0) {
            if (isShow != 0) {
                if (StringUtils.equals(oldRare, newRare)) {
                    CardHistoryModel cardHistory = new CardHistoryModel();
                    cardHistory.setPackageId(cardModel.getPackageId());
                    cardHistory.setCardPk(cardModel.getCardPk());
                    cardHistory.setOldName(oldName);
                    cardHistory.setNewName(newName);
                    cardHistory.setRare(cardModel.getRare());
                    cardHistoryMapper.insert(cardHistory);
                } else {
                    CardHistoryModel oldCardHistory = new CardHistoryModel();
                    oldCardHistory.setPackageId(cardModel.getPackageId());
                    oldCardHistory.setCardPk(cardModel.getCardPk());
                    oldCardHistory.setOldName(oldName);
                    oldCardHistory.setNewName("");
                    oldCardHistory.setRare(oldRare);
                    cardHistoryMapper.insert(oldCardHistory);

                    CardHistoryModel newCardHistory = new CardHistoryModel();
                    newCardHistory.setPackageId(cardModel.getPackageId());
                    newCardHistory.setCardPk(cardModel.getCardPk());
                    newCardHistory.setOldName("");
                    newCardHistory.setNewName(newName);
                    newCardHistory.setRare(newRare);
                    cardHistoryMapper.insert(newCardHistory);
                }
            }

            clearCardOwnListCache();
            clearRollListWithDetailCache();
            recordService.setRecord(operator, "卡片[%s](%s)重命名为[%s](%s)", oldName, oldRare, newName, newRare);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean exchangeCardName(String name1, String name2, int isShow, String operator) throws Exception {
        // 查找是否能否更新
        PackageCardModel cardModel1 = packageCardMapper.selectByNameDistinct(name1);
        if (cardModel1 == null) {
            throw new OperationException("卡片[%s]不存在！", name1);
        }
        PackageCardModel cardModel2 = packageCardMapper.selectByNameDistinct(name2);
        if (cardModel2 == null) {
            throw new OperationException("卡片[%s]不存在！", name1);
        }
        if (cardModel1.getRare().equals(cardModel2.getRare()) && cardModel1.getPackageId() == cardModel2.getPackageId()) {
            throw new OperationException("卡片[%s]和[%s]完全相同！", name1, name2);
        }

        // 修改
        String tempName = cardModel1.getCardName();
        cardModel1.setCardName(cardModel2.getCardName());
        cardModel2.setCardName(tempName);
        int result = packageCardMapper.updateByPrimaryKey(cardModel1) + packageCardMapper.updateByPrimaryKey(cardModel2);
        if (result > 0) {
            recordService.setRecord(
                    operator,
                    "卡片[%d-%s](%s)、[%d-%s](%s)稀有度互换",
                            cardModel1.getPackageId(), cardModel1.getCardName(), cardModel1.getRare(),
                            cardModel2.getPackageId(), cardModel2.getCardName(), cardModel2.getRare()
            );
        }
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
            orderNum++;
        }
        return "修改排序成功！";
    }
}
