package org.pot.core.util;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.InetAddress;

@Slf4j
public class GeoIpUtil {
    private static volatile DatabaseReader database = null;

    static {
        loadDatabase();
    }

    public static void loadDatabase() {
        DatabaseReader newDatabase = null;
        DatabaseReader oldDatabase = database;
        String filePath = "GeoIP2-Country_20230808/GeoIP2-Country.mmdb";
        try {
            InputStream inputStream = ClassResourceUtil.getInputStream(filePath);
            newDatabase = new DatabaseReader.Builder(inputStream).withCache(new CHMCache()).build();
        } catch (Exception exception) {
            log.error("Load Database failed. file:{}", filePath, exception);
        } finally {
            log.info("Load Database ok");
            if (newDatabase != null) {
                database = newDatabase;
                if (oldDatabase != null) {
                    try {
                        oldDatabase.close();
                    } catch (Exception exception) {
                        log.error("Failed to close Database", exception);
                    }
                }
            }
        }
    }

    public static Country getCountry(String ip) {
        CountryResponse countryResponse = getCountryResponse(ip);
        return countryResponse == null ? null : countryResponse.getCountry();
    }

    public static CountryResponse getCountryResponse(String ip) {
        String usedIp = StringUtils.stripToEmpty(ip);
        if (database != null && StringUtils.isNotBlank(usedIp)) {
            try {
                return database.country(InetAddress.getByName(usedIp));
            } catch (Throwable throwable) {
                log.error("Database not recognized ip:{}", usedIp);
            }
        }
        return null;
    }
}
