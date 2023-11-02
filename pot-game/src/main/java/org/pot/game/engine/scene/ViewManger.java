package org.pot.game.engine.scene;

import com.google.protobuf.Message;
import lombok.Getter;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.march.March;
import org.pot.message.protocol.world.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ViewManger {
    @Getter
    private final AbstractScene scene;
    private final Map<Long, View> views = new ConcurrentHashMap<>();
    private final Map<Long, RemoteView> remoteViews = new ConcurrentHashMap<>();

    public ViewManger(AbstractScene scene) {
        this.scene = scene;
    }

    public IErrorCode remoteBrowseView(long playerId, int fromServerId, WorldMapViewC2S request) {
        ViewLevel viewLevel = ViewLevel.findById(request.getViewLevel());
        if (viewLevel == null) {
            return GameErrorCode.UNLOCK;
        }
        RemoteView view = remoteViews.computeIfAbsent(playerId, k -> new RemoteView(playerId, this, fromServerId));
        browseView(request, view);
        return null;
    }

    public IErrorCode browseView(long playerId, WorldMapViewC2S request) {
        ViewLevel viewLevel = ViewLevel.findById(request.getViewLevel());
        if (viewLevel == null) {
            return GameErrorCode.UNLOCK;
        }
        View view = views.computeIfAbsent(playerId, k -> new View(playerId, this));
        browseView(request, view);
        return null;
    }

    public void browseView(WorldMapViewC2S request, View view) {
        ViewLevel viewLevel = ViewLevel.findById(request.getViewLevel());
        if (request.getOpenView() || !view.isOpen() || viewLevel != view.getViewLevel()) {
            openView(request, view).forEach(view::sendMessage);
            pushUnionTerritory(view.getPlayerId());
            pushAllMarches(view.getPlayerId());
        } else {
            moveView(request, view).forEach(view::sendMessage);
        }
    }

    public void pushAllMarches(long playerId) {
        scene.submit(() -> {
            Collection<March> marches = scene.getMarchManager().getMarches();
            AddWorldMarchInfoS2C.Builder builder = AddWorldMarchInfoS2C.newBuilder();
            for (March march : marches) {
                builder.addMarchs(march.buildWorldMarchInfo(playerId));
            }
            builder.setSid(GameServerInfo.getServerId());
            AddWorldMarchInfoS2C message = builder.build();
            Optional.ofNullable(views.get(playerId)).ifPresent(v -> v.sendMessage(message));
            Optional.ofNullable(remoteViews.get(playerId)).ifPresent(v -> v.sendMessage(message));
        });
    }

    public void pushUnionTerritory(long playerId) {
        scene.submit(() -> {
            AddUnionTerritoryViewS2C message = AddUnionTerritoryViewS2C.newBuilder().build();
            Optional.ofNullable(views.get(playerId)).ifPresent(v -> v.sendMessage(message));
            Optional.ofNullable(remoteViews.get(playerId)).ifPresent(v -> v.sendMessage(message));
        });
    }

    private List<Message> moveView(WorldMapViewC2S request, View view) {
        List<Message> viewMessages = new ArrayList<>();
        WorldPointStruct center = request.getCenter();
        ViewLevel viewLevel = ViewLevel.findById(request.getViewLevel());
        int xMinOld = view.getMinX();
        int xMaxOld = view.getMaxX();
        int yMinOld = view.getMinY();
        int yMaxOld = view.getMaxY();
        view.update(viewLevel, center.getX(), center.getY(), request.getWidth(), request.getHeight());
        WorldMapViewS2C.Builder worldMapViewBuilder = WorldMapViewS2C.newBuilder();
        worldMapViewBuilder.setViewLevel(viewLevel.getId());
        worldMapViewBuilder.setWidth(request.getWidth());
        worldMapViewBuilder.setHeight(request.getHeight());
        worldMapViewBuilder.setCenter(center.toBuilder());
        int xMin = view.getMinX();
        int xMax = view.getMaxX();
        int yMin = view.getMinY();
        int yMax = view.getMaxY();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                if (!scene.getPointRegulation().isValidCoordinate(x, y)) {
                    continue;
                }
                if (x >= xMinOld && x <= xMaxOld && y >= yMinOld && y <= yMaxOld) {
                    continue;
                }
                WorldPoint point = scene.getPointManager().getPoint(x, y);
                if (point != null && point.isClientVisible()) {
                    worldMapViewBuilder.addWorldPointInfos(point.toWorldPointInfo());
                }
            }
        }
        viewMessages.add(worldMapViewBuilder.build());
        return viewMessages;
    }

    private List<Message> openView(WorldMapViewC2S request, View view) {
        List<Message> viewMessages = new ArrayList<>();
        WorldPointStruct center = request.getCenter();
        ViewLevel viewLevel = ViewLevel.findById(request.getViewLevel());
        view.update(viewLevel, center.getX(), center.getY(), request.getWidth(), request.getHeight());
        WorldMapViewS2C.Builder worldMapViewBuilder = WorldMapViewS2C.newBuilder();
        worldMapViewBuilder.setViewLevel(viewLevel.getId());
        worldMapViewBuilder.setWidth(request.getWidth());
        worldMapViewBuilder.setHeight(request.getHeight());
        worldMapViewBuilder.setCenter(center.toBuilder());
        int xMin = view.getMinX();
        int xMax = view.getMaxX();
        int yMin = view.getMinY();
        int yMax = view.getMaxY();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                if (!scene.getPointRegulation().isValidCoordinate(x, y)) {
                    continue;
                }
                WorldPoint point = scene.getPointManager().getPoint(x, y);
                if (point != null && point.isClientVisible()) {
                    worldMapViewBuilder.addWorldPointInfos(point.toWorldPointInfo());
                }
            }
        }
        viewMessages.add(worldMapViewBuilder.build());
        return viewMessages;
    }
}
