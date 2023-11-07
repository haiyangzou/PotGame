package org.pot.login.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.communication.server.GameServer;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.RandomUtil;
import org.pot.login.beans.UserLoginInfo;
import org.pot.login.cache.*;
import org.pot.login.entity.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRegisterService {
    @Resource
    private RegisteredCountCache registeredCountCache;
    @Resource
    private GameServerDaoCache gameServerDaoCache;
    @Resource
    private RegisterPolicyDaoCache registerPolicyDaoCache;
    @Resource
    private RegisterServerPolicyDaoCache registerServerPolicyDaoCache;
    @Resource
    private RegisterPolicyGroupDaoCache registerPolicyGroupDaoCache;
    @Resource
    private RegisterGroupDaoCache registerGroupDaoCache;
    @Resource
    private RegisterGroupLocaleDaoCache registerGroupLocaleDaoCache;
    @Resource
    private RegisterLocaleDaoCache registerLocaleDaoCache;

    /**
     * 导量选服服务，那句用户的国家和语言，来进行合适的服务器。
     * 当前生效中的导量策略n个，生效中的策略可以指定优先级，相同优先级的策略使用顺序是随机的
     * 每个导量策略应用到n个服务器上，一个策略应用到服务器上时可以指定优先级，相同优先级的服务器的使用书序是随机的
     * 假设
     * 有A(0),B(0),C(1)三个策略，括号内为优先级，越小越优先，相同优先级则随机
     * A策略应用到了1(1),2(0),3(0)服，括号内为优先级，越小越优先，相同优先级则随机。
     * B策略应用到了3(1),4(0)服，括号内为优先级，越小越优先，相同优先级则随机。
     * C策略应用到了1(0),2(0),3(0),4(0)服括号内为优先级，越小越优先，相同优先级则随机。
     * 则
     * 一个用户进入之后，首先查到了A,B,C三个生效中的策略
     * 按照优先级进行分组，同优先级的策略随机顺序，[B,A]和[C],则策略应用顺序为B->A->C
     *
     * @param userLoginInfo
     */
    public void selectMatchedServer(UserLoginInfo userLoginInfo) {
        List<GameServer> allServerList = getServerList();
        List<GameServer> availableServerList = allServerList.stream().filter(registeredCountCache::isNotFullServer).collect(Collectors.toList());
        Map<Integer, GameServer> availableServerMap = availableServerList.stream().collect(Collectors.toMap(GameServer::getServerId, server -> server));
        List<RegisterPolicy> policyList = getPolicyList(userLoginInfo);
        for (RegisterPolicy policy : policyList) {
            List<GameServer> policyServerList = getPolicyServerList(policy, availableServerMap);
            List<RegisterPolicyGroup> policyGroups = registerPolicyGroupDaoCache.selectAll().stream()
                    .filter(group -> Objects.equals(group.getPolicyId(), policy.getId()))
                    .collect(Collectors.toList());
            for (GameServer policyServer : policyServerList) {
                for (RegisterPolicyGroup policyGroup : policyGroups) {
                    final int groupMaxCount = (int) (policyGroup.getRatio() / 100D * policy.getTotalMaxCount());
                    final int groupNowCount = registeredCountCache.getServerPolicyGroupCount(policyServer, policyGroup);
                    if (groupNowCount >= groupMaxCount) {
                        continue;
                    }
                    RegisterGroup registerGroup = registerGroupDaoCache.selectAll().stream()
                            .filter(group -> Objects.equals(group.getId(), policyGroup.getGroupId()))
                            .filter(group -> !isExcludeLocale(userLoginInfo, group))
                            .findFirst().orElse(null);
                    if (registerGroup == null) {
                        continue;
                    }
                    List<RegisterGroupLocale> groupLocales = registerGroupLocaleDaoCache.selectAll().stream()
                            .filter(locale -> Objects.equals(locale.getId(), registerGroup.getId()))
                            .collect(Collectors.toList());
                    for (RegisterGroupLocale groupLocale : groupLocales) {
                        final int localeMaxCount = (int) (groupLocale.getRatio() / 100D * groupMaxCount);
                        final int localeNowCount = registeredCountCache.getServerPolicyGroupLocaleCount(policyServer, policyGroup, groupLocale);
                        if (localeNowCount >= localeMaxCount) {
                            continue;
                        }
                        RegisterLocale registerLocale = registerLocaleDaoCache.selectAll().stream()
                                .filter(locale -> Objects.equals(locale.getId(), groupLocale.getLocaleId()))
                                .filter(locale -> !isExcludeLocale(userLoginInfo, locale))
                                .findFirst().orElse(null);
                        if (registerLocale == null) {
                            continue;
                        }
                        userLoginInfo.setServerId(policyServer.getServerId());
                        registeredCountCache.addPolicyCount(policy);
                        registeredCountCache.addServerCount(policyServer);
                        registeredCountCache.addServerPolicyGroupCount(policyServer, policyGroup);
                        registeredCountCache.addServerPolicyGroupLocaleCount(policyServer, policyGroup, groupLocale);
                        return;
                    }
                }
            }
        }
        if (firstServer(userLoginInfo, availableServerList)) return;
        if (randomServer(userLoginInfo, allServerList)) return;
        if (firstServer(userLoginInfo, availableServerList)) return;
        if (randomServer(userLoginInfo, allServerList)) return;
        throw new ServiceException("none matched server", CommonErrorCode.INVALID_SERVER_INFO);
    }

    private boolean randomServer(UserLoginInfo userLoginInfo, List<GameServer> list) {
        if (CollectionUtil.isEmpty(list)) return false;
        List<GameServer> subList = list.subList(0, Math.min(10, list.size()));
        GameServer gameServer = subList.get(RandomUtil.randomInt(subList.size()));
        if (gameServer == null) return false;
        Integer serverId = gameServer.getServerId();
        userLoginInfo.setServerId(serverId);
        registeredCountCache.addServerCount(gameServer);
        return true;
    }

    private boolean firstServer(UserLoginInfo userLoginInfo, List<GameServer> list) {
        if (CollectionUtil.isEmpty(list)) return false;
        GameServer gameServer = list.get(0);
        if (gameServer == null) return false;
        Integer serverId = gameServer.getServerId();
        userLoginInfo.setServerId(serverId);
        registeredCountCache.addServerCount(gameServer);
        return true;
    }

    private List<GameServer> getServerList() {
        return gameServerDaoCache.selectAll().stream().filter(server -> server.getServerStatus() > 0).collect(Collectors.toList());
    }

    private List<GameServer> getPolicyServerList(RegisterPolicy policy, Map<Integer, GameServer> availableServerMap) {
        List<GameServer> policyServerList = new ArrayList<>();
        registerServerPolicyDaoCache.selectAll().stream()
                .filter(serverPolicy -> Objects.equals(policy.getId(), serverPolicy.getPolicyId()))
                .collect(Collectors.groupingBy(RegisterServerPolicy::getPriority, TreeMap::new, Collectors.toList()))
                .forEach((priority, serverPolicies) -> {
                    CollectionUtil.shuffle(serverPolicies);
                    serverPolicies.forEach(serverPolicy -> policyServerList.add(availableServerMap.get(serverPolicy.getServerId())));
                });
        policyServerList.removeIf(Objects::isNull);
        return policyServerList;
    }

    private List<RegisterPolicy> getPolicyList(UserLoginInfo userLoginInfo) {
        List<RegisterPolicy> policyList = new ArrayList<>();
        registerPolicyDaoCache.selectAll().stream()
                .filter(policy -> policy.getDisable() == 0)
                .filter(registeredCountCache::isNotFullPolicy)
                .collect(Collectors.groupingBy(RegisterPolicy::getPriority, TreeMap::new, Collectors.toList()))
                .forEach((priority, policies) -> policyList.addAll(CollectionUtil.shuffle(policies)));
        policyList.removeIf(policy -> isExcludeLocale(userLoginInfo, policy));
        return policyList;
    }

    private boolean isExcludeLocale(UserLoginInfo userLoginInfo, RegisterPolicy policy) {
        return isExcluded(userLoginInfo.getCountry(), policy.getInclusiveCountryIsoCodes(), policy.getExclusiveCountryIsoCodes())
                || isExcluded(userLoginInfo.getDeviceLanguage(), policy.getInclusiveLanguages(), policy.getExclusiveLanguages());
    }

    private boolean isExcludeLocale(UserLoginInfo userLoginInfo, RegisterLocale policy) {
        return isExcluded(userLoginInfo.getCountry(), policy.getInclusiveCountryIsoCodes(), policy.getExclusiveCountryIsoCodes())
                || isExcluded(userLoginInfo.getDeviceLanguage(), policy.getInclusiveLanguages(), policy.getExclusiveLanguages());
    }

    private boolean isExcludeLocale(UserLoginInfo userLoginInfo, RegisterGroup policy) {
        return isExcluded(userLoginInfo.getCountry(), policy.getInclusiveCountryIsoCodes(), policy.getExclusiveCountryIsoCodes())
                || isExcluded(userLoginInfo.getDeviceLanguage(), policy.getInclusiveLanguages(), policy.getExclusiveLanguages());
    }

    private boolean isExcluded(String seq, String inclusive, String exclusive) {
        if (StringUtils.contains(exclusive, seq)) {
            return true;
        }
        if (StringUtils.isNotBlank(inclusive) && !StringUtils.contains(inclusive, seq)) {
            return true;
        }
        return false;
    }
}
