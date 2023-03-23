package com.WintertodtSoloHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Slf4j
public class WintertodtBrazier {
    public GameObject brazierObject;
    private GameObject brumaRootObject;
    private NPC pyromancer;

    @Getter(AccessLevel.PACKAGE)
    private BrazierStatus status;

    private static final int UNLIT_SPRITE_ID = 1398;
    private static final int LIT_SPRITE_ID = 1399;
    private static final int BROKEN_SPRITE_ID = 1397;

    private static final int DEAD_PYROMANCER_SPRITE_ID = 1400;

    private WorldPoint worldLocation;

    private int brazierStatusWidgetId;

    private Widget brazierStatusWidget;

    private int wizardStatusWidgetId;

    private Widget wizardStatusWidget;

    public BrazierLocation brazierLocation;

    @Getter(AccessLevel.PACKAGE)
    private boolean pyromancerAlive;

    public WintertodtBrazier(BrazierLocation brazierLocation) {
        this.brazierLocation = brazierLocation;

        switch (brazierLocation) {
            case SouthWest:
                worldLocation = new WorldPoint(1621, 3998, 0);
                brazierStatusWidgetId = 12;
                wizardStatusWidgetId = 8;
                break;
            case NorthWest:
                worldLocation = new WorldPoint(1621, 4016, 0);
                brazierStatusWidgetId = 13;
                wizardStatusWidgetId = 9;
                break;
            case NorthEast:
                worldLocation = new WorldPoint(1639, 4016, 0);
                brazierStatusWidgetId = 14;
                wizardStatusWidgetId = 10;
                break;
            case SouthEast:
                worldLocation = new WorldPoint(1639, 3998, 0);
                brazierStatusWidgetId = 15;
                wizardStatusWidgetId = 11;
                break;
        }
    }

    public void ProcessWidgets(Client client) {
        wizardStatusWidget = client.getWidget(WintertodtSoloHelperPlugin.WINTERTODT_WIDGET_GROUP_ID, wizardStatusWidgetId);
        brazierStatusWidget = client.getWidget(WintertodtSoloHelperPlugin.WINTERTODT_WIDGET_GROUP_ID, brazierStatusWidgetId);

        if(brazierStatusWidget != null) {
            if(brazierStatusWidget.getSpriteId() == UNLIT_SPRITE_ID) {
                status = BrazierStatus.UNLIT;
            }
            if(brazierStatusWidget.getSpriteId() == LIT_SPRITE_ID) {
                status = BrazierStatus.LIT;
            }
            if(brazierStatusWidget.getSpriteId() == BROKEN_SPRITE_ID)
            {
                status = BrazierStatus.BROKEN;
            }
        }

        if(wizardStatusWidget != null) {
            if(wizardStatusWidget.getSpriteId() == DEAD_PYROMANCER_SPRITE_ID) {
                pyromancerAlive = false;
            }
            else {
                pyromancerAlive = true;
            }
        }
    }

    public void updateGameObject(GameObject gameObject) {
        if(gameObject.getWorldLocation().getX() == worldLocation.getX() && gameObject.getWorldLocation().getY() == worldLocation.getY()) {
            brazierObject = gameObject;
        }
    }

    public void render(ModelOutlineRenderer modelOutlineRenderer, WintertodtSoloHelperConfig config, WintertodtSoloHelperPlugin plugin) {
        boolean thisIsMainLocation = config.brazier() == brazierLocation;

        int distanceFromPlayer = worldLocation.distanceTo(plugin.getPlayerLocation());

        boolean isWithinDistance = distanceFromPlayer <= 15;

        boolean shouldCurrentActivityStopRender = plugin.getCurrentActivity() == WintertodtActivity.LIGHTING_BRAZIER
                                        || plugin.getCurrentActivity() == WintertodtActivity.FEEDING_BRAZIER;

        boolean shouldDraw = !isWithinDistance || !shouldCurrentActivityStopRender;

        boolean shouldDrawRoot = !isWithinDistance || plugin.getCurrentActivity() != WintertodtActivity.WOODCUTTING;

        if(!pyromancerAlive) {
            if(pyromancer != null) {
                modelOutlineRenderer.drawOutline(pyromancer, 6, config.getHighlightColor(), 6);
            }
        }

        int brumaKindlingCount = plugin.getBrumaKindlingCount();
        int brumaRootCount = plugin.getBrumaLogCount();

        // Broken Brazier
        if(config.alwaysRepairBroken()) {
            if(status == BrazierStatus.BROKEN) {
                drawOutline(modelOutlineRenderer, brazierObject, config, true);
            }
        }

        // Ending Game
        if(config.pointGoal() < plugin.getWintertodtPoints()) {
            if(status != BrazierStatus.LIT) {
                drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
            }
        }

        // Main gameplay loop
        if (thisIsMainLocation) {
            if(status == BrazierStatus.UNLIT) {
                if(plugin.getWintertodtHealth() > config.alwaysRelightHealth())
                {
                    drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
                }
                else if (plugin.getWintertodtHealth() > config.minRelightHealth())
                {
                    if(brumaKindlingCount > 0)
                    {
                        if(brumaRootCount == 0) {
                            drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
                        }
                    }
                    else if(plugin.getEmptyInventoryCount() != 0) {
                        drawOutline(modelOutlineRenderer, brumaRootObject, config, shouldDrawRoot);
                    }
                }
            }
            else if(status == BrazierStatus.LIT) {
                if(brumaKindlingCount > 0)
                {
                    if(brumaRootCount == 0) {
                        drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
                    }
                }
                else if(plugin.getEmptyInventoryCount() != 0) {
                    drawOutline(modelOutlineRenderer, brumaRootObject, config, shouldDrawRoot);
                }
            }
            else if(status == BrazierStatus.BROKEN) {
                drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
                if (plugin.getEmptyInventoryCount() != 0 && brumaKindlingCount == 0) {
                    drawOutline(modelOutlineRenderer, brumaRootObject, config, shouldDrawRoot);
                }
            }
        }
        else if(config.multiFireRelightPercentage() < plugin.getWintertodtHealth()) {
            if(status == BrazierStatus.UNLIT) {
                drawOutline(modelOutlineRenderer, brazierObject, config, shouldDraw);
            }
        }
    }

    private void drawOutline(ModelOutlineRenderer modelOutlineRenderer, GameObject gameObject, WintertodtSoloHelperConfig config, boolean shouldDraw) {
        if(shouldDraw) {
            modelOutlineRenderer.drawOutline(gameObject, 6, config.getHighlightColor(), 6);
        }
    }

    public void updateRoots(GameObject gameObject) {
        int distance = gameObject.getWorldLocation().distanceTo(worldLocation);

        if(distance < 15)
        {
            brumaRootObject = gameObject;
        }
    }

    public void updatePyromancer(NPC npc) {
        int distance = npc.getWorldLocation().distanceTo(worldLocation);

        if(distance < 15)
        {
            pyromancer = npc;
        }
    }
}