package org.pot.strategy.beans;

import com.google.common.collect.ImmutableList;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.*;

import java.util.*;

public class GatewayAddress {
    private final List<String> gatewayIpv4List;
    private final Map<String, List<String>> gatewayIpv4CountryMap;
    private final Map<String, List<String>> gatewayIpv4ContinentMap;

    public GatewayAddress() {
        this.gatewayIpv4List = Collections.emptyList();
        this.gatewayIpv4CountryMap = Collections.emptyMap();
        this.gatewayIpv4ContinentMap = Collections.emptyMap();
    }

    public GatewayAddress(String gatewayHost) {
        this.gatewayIpv4List = ImmutableList.copyOf(HostUtil.getHostAddressList(gatewayHost, true));
        Map<String, List<String>> countryMap = new HashMap<>();
        Map<String, List<String>> continentMap = new HashMap<>();
        for (String gatewayIpv4 : gatewayIpv4List) {
            CountryResponse countryResponse = GeoIpUtil.getCountryResponse(gatewayIpv4);
            if (countryResponse == null) continue;
            Country country = countryResponse.getCountry();
            if (country != null) {
                countryMap.computeIfAbsent(country.getIsoCode(), k -> new ArrayList<>()).add(gatewayIpv4);
            }
            Continent continent = countryResponse.getContinent();
            if (continent != null) {
                continentMap.computeIfAbsent(country.getIsoCode(), k -> new ArrayList<>()).add(gatewayIpv4);
            }
        }
        this.gatewayIpv4CountryMap = MapUtil.immutableMapList(countryMap);
        this.gatewayIpv4ContinentMap = MapUtil.immutableMapList(continentMap);
    }

    public String getNearestGatewayIpv4(String requestIp) {
        if (CollectionUtil.isEmpty(gatewayIpv4List)) {
            return StringUtils.EMPTY;
        }
        CountryResponse requestCountryResponse = GeoIpUtil.getCountryResponse(requestIp);
        if (requestCountryResponse == null) {
            return RandomUtil.naturalRandomOne(gatewayIpv4List);
        }
        Country requestCountry = requestCountryResponse.getCountry();
        if (requestCountry != null) {
            List<String> countryGatewayIpv4List = gatewayIpv4CountryMap.get(requestCountry.getIsoCode());
            if (CollectionUtil.isNotEmpty(countryGatewayIpv4List)) {
                return RandomUtil.naturalRandomOne(countryGatewayIpv4List);
            }
        }
        Continent requestContinent = requestCountryResponse.getContinent();
        if (requestContinent != null) {
            List<String> continentGatewayIpv4List = gatewayIpv4ContinentMap.get(requestContinent.getCode());
            if (CollectionUtil.isNotEmpty(continentGatewayIpv4List)) {
                return RandomUtil.naturalRandomOne(continentGatewayIpv4List);
            }
        }
        return RandomUtil.naturalRandomOne(gatewayIpv4List);
    }
}
