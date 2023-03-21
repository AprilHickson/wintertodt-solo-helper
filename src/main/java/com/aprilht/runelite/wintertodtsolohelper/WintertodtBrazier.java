package net.runelite.client.plugins.wintertodtafk;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;

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
        if(wizardStatusWidget == null) {
            wizardStatusWidget = client.getWidget(WintertodtSoloHelperPlugin.WINTERTODT_WIDGET_GROUP_ID, wizardStatusWidgetId);
        }
        if(brazierStatusWidget == null) {
            brazierStatusWidget = client.getWidget(WintertodtSoloHelperPlugin.WINTERTODT_WIDGET_GROUP_ID, brazierStatusWidgetId);
        }

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
        var thisIsMainLocation = config.brazier() == brazierLocation;

        if(pyromancerAlive == false) {
            if(pyromancer != null) {
                modelOutlineRenderer.drawOutline(pyromancer, 6, config.getHighlightColor(), 6);
            }
        }

        int burnItemCount = plugin.getBrumaKindlingCount();

        if(config.alwaysRepairBroken()) {
            if(status == BrazierStatus.BROKEN) {
                modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
            }
        }

        if(config.pointGoal() < plugin.getWintertodtPoints()) {
            if(status != BrazierStatus.LIT) {
                modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
            }
        }

        if (thisIsMainLocation) {
            if(status == BrazierStatus.UNLIT) {
                if(plugin.getWintertodtHealth() > config.alwaysRelightHealth())
                {
                    modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
                }
                else if (plugin.getWintertodtHealth() > config.minRelightHealth())
                {
                    if(burnItemCount > 0 && plugin.getEmptyInventoryCount() == 0) {
                        modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
                    }
                    else {
                        modelOutlineRenderer.drawOutline(brumaRootObject, 6, config.getHighlightColor(), 6);
                    }
                }
            }
            else if(status == BrazierStatus.LIT) {
                if(burnItemCount > 0 && plugin.getEmptyInventoryCount() == 0) {
                    modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
                }
                else {
                    modelOutlineRenderer.drawOutline(brumaRootObject, 6, config.getHighlightColor(), 6);
                }
            }
            else if(status == BrazierStatus.BROKEN) {
                modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
            }
        }
        else if(config.multiFireRelightPercentage() < plugin.getWintertodtHealth()) {
            if(status == BrazierStatus.UNLIT) {
                modelOutlineRenderer.drawOutline(brazierObject, 6, config.getHighlightColor(), 6);
            }
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