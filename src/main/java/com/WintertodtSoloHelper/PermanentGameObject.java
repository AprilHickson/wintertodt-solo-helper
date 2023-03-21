package com.WintertodtSoloHelper;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

public class PermanentGameObject {
    @Getter(AccessLevel.PACKAGE)
    private GameObject gameObject;

    private final int objectId;

    private WorldPoint worldPoint;

    public PermanentGameObject(int objectId, WorldPoint worldLocation) {
        this.objectId = objectId;
        this.worldPoint = worldLocation;
    }

    public void setGameObject(GameObject gameObject) {
        if(gameObject.getWorldLocation().getX() == worldPoint.getX() && gameObject.getWorldLocation().getY() == worldPoint.getY()) {
            this.gameObject = gameObject;
        }
    }
}
