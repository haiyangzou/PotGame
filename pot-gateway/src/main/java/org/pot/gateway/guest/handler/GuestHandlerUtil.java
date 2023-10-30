package org.pot.gateway.guest.handler;

import org.apache.commons.lang3.StringUtils;
import org.pot.gateway.guest.Guest;
import org.pot.message.protocol.login.AppVersionInfoS2C;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.login.LoginNoticeS2C;
import org.pot.message.protocol.login.NoticeDto;

public class GuestHandlerUtil {
    static void versionFail(Guest guest, LoginDataS2S loginDataS2S) {
        AppVersionInfoS2C.Builder builder = AppVersionInfoS2C.newBuilder();
        builder.setAppUpdatePolicy(loginDataS2S.getAppUpdatePolicy());
        builder.setAppUpdateVersion(loginDataS2S.getAppUpdateVersion());
        builder.setAppUpdateUrl(StringUtils.stripToEmpty(loginDataS2S.getAppUpdateUrl()));
        guest.disconnect(builder.build());
    }

    static void sendMaintainNotice(Guest guest, LoginDataS2S loginDataS2S) {
        LoginNoticeS2C.Builder loginNoticeBuilder = LoginNoticeS2C.newBuilder();
        NoticeDto.Builder noticeDto = NoticeDto.newBuilder();
        noticeDto.setMaintainNoticeTitle(loginDataS2S.getMaintainNoticeTitle());
        noticeDto.setMaintainNoticeDetail(loginDataS2S.getMaintainNoticeDetail());
        noticeDto.setMaintainRemainingTime(loginDataS2S.getMaintainRemainingTime());
        loginNoticeBuilder.addNotices(noticeDto);
        guest.sendMessage(loginNoticeBuilder.build());
    }
}
