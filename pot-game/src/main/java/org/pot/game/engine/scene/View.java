package org.pot.game.engine.scene;

import com.google.protobuf.Message;
import lombok.Getter;
import org.pot.common.util.MathUtil;
import org.pot.core.net.protocol.MessageSender;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;

public class View implements MessageSender {
    @Getter
    protected final long playerId;
    protected final ViewManger viewManger;
    @Getter
    protected volatile ViewLevel viewLevel = null;
    protected volatile int centerX, centerY = 0;
    protected volatile int width, height = 0;

    public View(long playerId, ViewManger viewManger) {
        this.playerId = playerId;
        this.viewManger = viewManger;
    }

    public void update(ViewLevel viewLevel, int nx, int ny, int nWidth, int nHeight) {
        this.viewLevel = viewLevel;
        this.centerX = nx;
        this.centerY = ny;
        this.width = nWidth;
        this.height = nHeight;
    }

    @Override
    public void sendMessage(Message message) {
        Player player = PlayerManager.fetchPlayer(playerId);
        if (player != null) player.sendMessage(message);
    }

    public boolean isOpen() {
        return viewLevel != null;
    }

    public boolean contains(int nx, int ny) {
        return isOpen() && nx >= getMinX() && nx <= getMaxX() && ny >= getMinY() && ny <= getMaxY();
    }

    public int getMinX() {
        return MathUtil.limit(centerX - (width / 2), 0, viewManger.getScene().getPointRegulation().getMaxX());
    }

    public int getMaxX() {
        return MathUtil.limit(centerX + (width / 2), 0, viewManger.getScene().getPointRegulation().getMaxX());
    }

    public int getMinY() {
        return MathUtil.limit(centerY - (height / 2), 0, viewManger.getScene().getPointRegulation().getMaxY());
    }

    public int getMaxY() {
        return MathUtil.limit(centerY + (height / 2), 0, viewManger.getScene().getPointRegulation().getMaxY());
    }

}
