package com.weird.controller;

import com.alibaba.druid.util.StringUtils;
import com.weird.aspect.TrimArgs;
import com.weird.model.CardPreviewModel;
import com.weird.model.dto.CardSwapDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.BatchUpdateUserCardParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.DeckService;
import com.weird.service.UserService;
import com.weird.utils.CacheUtil;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 用户信息相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    @Autowired
    CardPreviewService cardPreviewService;

    @Autowired
    DeckService deckService;

    /**
     * 【ALL】查询用户信息
     *
     * @param page     页号
     * @param userName 用户名
     * @return 查询结果
     */
    @RequestMapping("/weird_project/user/list")
    public PageResult<UserDataDTO> getDustList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "user", required = false, defaultValue = "") String userName) throws Exception {
        List<UserDataDTO> dtoList = userService.getListByName(userName);
        PageResult<UserDataDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page, pageSize);
        return result;
    }

    /**
     * 【ALL】检查用户类型
     *
     * @param name     用户名
     * @param password 用户密码
     * @return UNLOGIN(未登录)、ADMIN(管理员)、(NORMAL)普通用户
     */
    @RequestMapping("/weird_project/user/check")
    public LoginTypeEnum getLoginType(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "password") String password) {
        return userService.checkLogin(name, password);
    }


    /**
     * 【管理端】修改用户持有的卡片数量
     *
     * @param targetUser 用户名
     * @param cardName   卡片名
     * @param newCount   新持有量
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @GetMapping("/weird_project/user/card/update")
    public String updateUserCardCount(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "card") String cardName,
            @RequestParam(value = "count") int newCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        String result = cardService.updateCardCount(targetUser, cardName, newCount, name);
        CompletableFuture.runAsync(() -> {
            try {
                CardPreviewModel card = cardPreviewService.selectPreviewByName(cardName);
                if (card != null) {
                    long cardCode = card.getId();
                    deckService.updateDeckCardCountWhenUpdateCount(targetUser, cardCode, newCount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    /**
     * 【管理端】批量修改用户持有的卡片数量
     *
     * @param param 参数
     * @return 是否修改成功
     */
    @PostMapping("/weird_project/user/card/update")
    public String updateUserCardCountList(@RequestBody BatchUpdateUserCardParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        return cardService.updateCardCountBatch(param);
    }

    /**
     * 【管理端】交换两个用户持有的卡片
     *
     * @param dto 参数
     * @return 是否交换成功
     */
    @PostMapping("/weird_project/user/card/swap")
    public String swapUserCard(@RequestBody CardSwapDTO dto) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(dto.getName(), dto.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        if (dto.getUserA().equals(dto.getUserB())) {
            throw new OperationException("不能更换同一个用户的卡！");
        }

        if (dto.getCardA().equals(dto.getCardB())) {
            throw new OperationException("不能更换相同的卡！");
        }
        return userService.swapCard(dto, dto.getName());
    }

    /**
     * 【管理端】修改用户的尘数
     *
     * @param targetUser 用户名
     * @param dustCount  新尘数
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/dust")
    public String updateDust(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "count") int dustCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (StringUtils.isEmpty(targetUser)) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateDust(targetUser, dustCount, name);
    }

    /**
     * 【玩家端】直接将尘转换为卡片
     *
     * @param cardName 卡片名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否转换成功
     */
    @RequestMapping("/weird_project/user/card/change")
    public String dustToCard(@RequestParam(value = "card") String cardName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password) throws Exception {
        if (userService.dustToCard(cardName, name, password)) {
            return "转换成功！";
        } else {
            throw new OperationException("转换失败！");
        }
    }

    /**
     * 【玩家端】将尘转换成指定卡包的随机闪
     *
     * @param packageName 卡包名
     * @param name        用户名
     * @param password    密码
     * @param dustFirst   优先使用尘进行合成
     * @return 转换结果
     */
    @RequestMapping("/weird_project/user/card/random")
    public String dustToRare(@RequestParam(value = "package") String packageName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password,
                             @RequestParam(value = "dustFirst", required = false, defaultValue = "0") int dustFirst) throws Exception {
        if (PackageUtil.canNotRoll(packageName)) {
            throw new OperationException("SP卡包无法由玩家合成卡片！");
        }
        return userService.dustToRare(packageName, name, password, dustFirst);
    }

    /**
     * 【玩家端】硬币换卡
     *
     * @param cardName 卡片名
     * @param name     用户名
     * @param password 密码
     * @return 转换结果
     */
    @RequestMapping("/weird_project/user/card/coin")
    public String coinToCard(@RequestParam(value = "cardName") String cardName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password) throws Exception {
        return userService.coinToCard(cardName, name, password);
    }

    /**
     * 【玩家端】将溢出的闪卡合成尘
     *
     * @param userName 用户名
     * @param password 密码
     * @param cardName 卡名
     * @param count    要转换的数量
     * @return 转换结果
     */
    @RequestMapping("/weird_project/user/card/todust")
    public String rareToDust(@RequestParam(value = "name") String userName,
                             @RequestParam(value = "password") String password,
                             @RequestParam(value = "card") String cardName,
                             @RequestParam(value = "count") int count) throws Exception {
        if (count <= 0) {
            throw new OperationException("转换数量应大于0！");
        }
        int dustCount = userService.rareToDust(userName, password, cardName, count);
        return String.format("共获得%d尘！", dustCount);
    }

    /**
     * 【管理端】修改用户不出货数量
     *
     * @param targetUser 用户名
     * @param awardCount 不出货数量
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/award")
    public String updateCount(@RequestParam(value = "target") String targetUser,
                              @RequestParam(value = "award") int awardCount,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateAward(targetUser, awardCount, name);
    }

    /**
     * 【管理端】修改用户DP
     *
     * @param targetUser 用户名
     * @param dpCount    新DP
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/dp")
    public String updateDuelPoint(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "dp") int dpCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateDuelPoint(targetUser, dpCount, name);
    }

    /**
     * 【管理端】修改用户硬币
     *
     * @param targetUser 用户名
     * @param coin       新硬币数量
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/coin")
    public String updateCoin(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "coin") int coin,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateCoin(targetUser, coin, name);
    }

    /**
     * 【管理端】修改用户转盘次数
     *
     * @param targetUser 用户名
     * @param roulette   新转盘次数
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/roulette")
    public String updateRoulette(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "roulette") int roulette,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateRoulette(targetUser, roulette, name);
    }

    /**
     * 【管理端】修改用户抽卡计数
     *
     * @param targetUser 用户名
     * @param rollCount  新抽卡计数
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/rollCount")
    public String updateRollCount(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "rollCount") int rollCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        return userService.updateRollCount(targetUser, rollCount, name);
    }

    /**
     * 【管理端】添加新用户
     * 新用户密码默认为123456
     *
     * @param target   用户名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否添加成功
     */
    @RequestMapping("/weird_project/user/add")
    public String addUser(@RequestParam(value = "target") String target,
                          @RequestParam(value = "name") String name,
                          @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (target == null || target.length() == 0) {
            throw new OperationException("用户名为空！");
        }
        if (target.contains(" ")) {
            throw new OperationException("用户名不能包含空格！");
        }
        if (userService.addUser(target, name)) {
            return "添加成功！";
        } else {
            throw new OperationException("添加失败！");
        }
    }

    /**
     * 【ALL】修改用户密码
     *
     * @param name        用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/pw")
    public String updatePassword(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "old") String oldPassword,
                                 @RequestParam(value = "new") String newPassword) throws Exception {
        if (userService.updatePassword(name, oldPassword, newPassword)) {
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }

    /**
     * 【管理端】重置用户密码
     * 用户密码默认为123456
     *
     * @param target   用户名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否重置成功
     */
    @RequestMapping("/weird_project/user/reset")
    public String resetPassword(@RequestParam(value = "target") String target,
                                @RequestParam(value = "name") String name,
                                @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        final LoginTypeEnum loginTypeEnum = userService.checkLogin(name, password);
        if (loginTypeEnum == LoginTypeEnum.ADMIN) {
            if (StringUtils.isEmpty(target)) {
                throw new OperationException("用户名为空！");
            }
        } else if (loginTypeEnum == LoginTypeEnum.NORMAL) {
            if (!StringUtils.equals(name, target)) {
                throw new OperationException("权限不足！");
            }
        }

        if (userService.resetPassword(target, name)) {
            return "重置成功！";
        } else {
            throw new OperationException("重置失败！");
        }
    }

    /**
     * 【管理端】设置用户禁用状态
     *
     * @param target   用户名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @param disabled 禁用（0=恢复，1=禁用）
     * @return 是否设置成功
     */
    @RequestMapping("/weird_project/user/disabled")
    public String updateDisables(@RequestParam(value = "target") String target,
                                 @RequestParam(value = "name") String name,
                                 @RequestParam(value = "password") String password,
                                 @RequestParam(value = "disabled") int disabled) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        if (userService.updateDisabled(target, name, disabled)) {
            CompletableFuture.runAsync(() -> {
                CacheUtil.clearCardOwnListCache();
                CacheUtil.clearRollListWithDetailCache();
            });
            return "设置禁用状态成功！";
        } else {
            throw new OperationException("设置禁用状态失败！");
        }
    }

    /**
     * 【管理端】查看禁用中账户
     *
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否设置成功
     */
    @RequestMapping("/weird_project/user/disabing")
    public List<String> showDisable(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        return userService.showDisabledUserName();
    }
}
