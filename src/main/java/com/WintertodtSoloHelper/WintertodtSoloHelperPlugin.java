package com.WintertodtSoloHelper;

import com.google.common.collect.Lists;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
//Burnables
import static net.runelite.api.ItemID.*;
import static net.runelite.api.ObjectID.SPROUTING_ROOTS;
//Potions
//Hammer

import javax.inject.Inject;
import java.util.List;

@PluginDescriptor(
		name = "Wintertodt Solo Helper",
		description = "Helpful Highlighting and data for Wintertodt Soloers",
		tags = {"minigame", "firemaking", "boss"}
)
@Slf4j
public class WintertodtSoloHelperPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private WintertodtSoloHelperOverlay overlay;
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WintertodtSoloHelperConfig config;

	@Provides
	WintertodtSoloHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WintertodtSoloHelperConfig.class);
	}

	// Statics
	private static final int WINTERTODT_REGION_ID = 6462;
	public static final int WINTERTODT_WIDGET_GROUP_ID = 396;
	private static final int WINTERTODT_HEALTH_WIDGET_ID = 21;
	private static final int WINTERTODT_POINTS_WIDGET_ID = 7;
	private static final int HAMMER_CRATE = 29316;
	private static final int KNIFE_CRATE = 29317;
	private static final int POTION_CRATE = 29320;
	private static final int TINDERBOX_CRATE = 29319;
	private static final int UNLIT_BRAZIER = 29312;
	private static final int BROKEN_BRAZIER = 29313;
	private static final int LIT_BRAZIER = 29314;
	private static final int BRUMA_ROOTS = 29311;
	private static final int PYROMANCER = 7372;
	private static final int DEAD_PYROMANCER = 7371;

	// World locations
	private WintertodtBrazier SouthEast = new WintertodtBrazier(BrazierLocation.SouthEast);
	private WintertodtBrazier SouthWest = new WintertodtBrazier(BrazierLocation.SouthWest);
	private WintertodtBrazier NorthEast = new WintertodtBrazier(BrazierLocation.NorthEast);
	private WintertodtBrazier NorthWest = new WintertodtBrazier(BrazierLocation.NorthWest);
	@Getter(AccessLevel.PACKAGE)
	private List<WintertodtBrazier> braziers = Lists.newArrayList(SouthEast, SouthWest, NorthEast, NorthWest);

	private Widget healthWidget;
	private Widget pointsWidget;
	private Item[] inventoryItems;
	private Item[] equipmentItems;
	@Getter(AccessLevel.PACKAGE)
	private int brumaLogCount;

	@Getter(AccessLevel.PACKAGE)
	private int brumaKindlingCount;

	@Getter(AccessLevel.PACKAGE)
	private int emptyInventoryCount;

	@Getter(AccessLevel.PACKAGE)
	private int wintertodtHealth;

	@Getter(AccessLevel.PACKAGE)
	private int wintertodtPoints;

	@Getter(AccessLevel.PACKAGE)
	private boolean isInWintertodt;

	@Getter(AccessLevel.PACKAGE)
	private boolean hasHammer;

	@Getter(AccessLevel.PACKAGE)
	private boolean hasKnife;

	@Getter(AccessLevel.PACKAGE)
	private boolean hasTinderbox;

	@Getter(AccessLevel.PACKAGE)
	private String warningText;

	@Getter(AccessLevel.PACKAGE)
	private TileObject hammerCrate;

	@Getter(AccessLevel.PACKAGE)
	private TileObject knifeCrate;

	//list
	@Getter(AccessLevel.PACKAGE)
	private List<TileObject> potionCrates = Lists.newArrayList();

	@Getter(AccessLevel.PACKAGE)
	private List<TileObject> sproutingRoots = Lists.newArrayList();

	@Getter(AccessLevel.PACKAGE)
	private TileObject tinderboxCrate;

	@Getter(AccessLevel.PACKAGE)
	private int potionDoseCount;

	@Getter(AccessLevel.PACKAGE)
	private int unfinishedCount;

	@Getter(AccessLevel.PACKAGE)
	private int herbCount;

	@Getter(AccessLevel.PACKAGE)
	private int litBrazierCount;

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (!isInWintertodtRegion())
		{
			if (isInWintertodt)
			{
				log.debug("Left Wintertodt!");
				reset();
				isInWintertodt = false;
			}
			return;
		}

		if(isInWintertodt)
		{
			healthWidget = client.getWidget(WINTERTODT_WIDGET_GROUP_ID, WINTERTODT_HEALTH_WIDGET_ID);
			pointsWidget = client.getWidget(WINTERTODT_WIDGET_GROUP_ID, WINTERTODT_POINTS_WIDGET_ID);

			if (healthWidget != null) {
				// widget.getText() returns "Wintertodt's Energy: 100%" so we need to get an int
				String text = healthWidget.getText();
				if(text != null && text != "" && text.replaceAll("[^0-9]", "") != "")
				{
					wintertodtHealth = Integer.parseInt(text.replaceAll("[^0-9]", ""));
				}
			}

			if (pointsWidget != null) {
				// widget.getText() returns "Points 122" so we need to get an int
				String text = pointsWidget.getText();
				if(text != null && text != "" && text.replaceAll("[^0-9]", "") != "")
				{
					wintertodtPoints = Integer.parseInt(text.replaceAll("[^0-9]", ""));
				}
			}

			// process widgets
			litBrazierCount = 0;
			for (WintertodtBrazier brazier : braziers)
			{
				brazier.ProcessWidgets(client);
				if(brazier.getStatus() == BrazierStatus.LIT)
				{
					litBrazierCount++;
				}
			}

		}
		else
		{
			reset();
			log.debug("Entered Wintertodt!");
			isInWintertodt = true;
		}


	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event) {
		final ItemContainer itemContainer = event.getItemContainer();

		if(event.getContainerId() == InventoryID.INVENTORY.getId()) {
			inventoryItems = itemContainer.getItems();

			//reset counts
			herbCount = 0;
			unfinishedCount = 0;
			brumaLogCount = 0;
			brumaKindlingCount = 0;
			potionDoseCount = 0;
			emptyInventoryCount = 0;

			if(inventoryItems != null)
			{
				//get bruma count
				for (Item item : inventoryItems) {
					if (item.getId() == BRUMA_ROOT) {
						brumaLogCount++;
					}
					if (item.getId() == BRUMA_KINDLING) {
						brumaKindlingCount++;
					}

					if (item.getId() == REJUVENATION_POTION_1) {
						potionDoseCount++;
					}
					if (item.getId() == REJUVENATION_POTION_2) {
						potionDoseCount = potionDoseCount + 2;
					}
					if (item.getId() == REJUVENATION_POTION_3) {
						potionDoseCount = potionDoseCount + 3;
					}
					if (item.getId() == REJUVENATION_POTION_4) {
						potionDoseCount = potionDoseCount + 4;
					}
					if (item.getId() == REJUVENATION_POTION_UNF) {
						unfinishedCount++;
					}
					if (item.getId() == BRUMA_HERB) {
						herbCount++;
					}
					if(item.getId() == -1) {
						emptyInventoryCount++;
					}
				}
			}
		}

		if(event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
			equipmentItems = itemContainer.getItems();
		}

		if(inventoryItems != null && equipmentItems != null) {
			hasHammer = false;
			hasKnife = false;
			hasTinderbox = false;
			for (Item item : equipmentItems) {
				if (item.getId() == IMCANDO_HAMMER) {
					hasHammer = true;
				}
				if (item.getId() == BRUMA_TORCH) {
					hasTinderbox = true;
				}
			}
			if(!hasHammer) {
				//is inventory hammer
				for (Item item : inventoryItems) {
					if (item.getId() == HAMMER) {
						hasHammer = true;
					}
					if (item.getId() == KNIFE) {
						hasKnife = true;
					}
					if (item.getId() == TINDERBOX) {
						hasTinderbox = true;
					}
				}
			}
		}
	}

	private boolean isInWintertodtRegion()
	{
		if (client.getLocalPlayer() != null)
		{
			return client.getLocalPlayer().getWorldLocation().getRegionID() == WINTERTODT_REGION_ID;
		}

		return false;
	}

	private void reset()
	{
		healthWidget = null;
		pointsWidget = null;
		wintertodtHealth = 0;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		processGameObject(event.getGameObject());
	}

	private void processGameObject(GameObject gameObject) {
		if(gameObject.getId() == HAMMER_CRATE) {
			hammerCrate = gameObject;
		}
		if(gameObject.getId() == KNIFE_CRATE) {
			knifeCrate = gameObject;
		}
		if(gameObject.getId() == POTION_CRATE) {
			//if potionCrate list contains a gameobject on the same location then remove it
			for (TileObject potionCrate : potionCrates) {
				if(potionCrate.getWorldLocation().getX() == gameObject.getWorldLocation().getX() && potionCrate.getWorldLocation().getY() == gameObject.getWorldLocation().getY()) {
					potionCrates.remove(potionCrate);
				}
			}

			potionCrates.add(gameObject);
		}
		if(gameObject.getId() == SPROUTING_ROOTS) {
			//if roots list contains a gameobject on the same location then remove it
			for (TileObject roots : sproutingRoots) {
				if(roots.getWorldLocation().getX() == gameObject.getWorldLocation().getX() && roots.getWorldLocation().getY() == gameObject.getWorldLocation().getY()) {
					sproutingRoots.remove(roots);
				}
			}

			sproutingRoots.add(gameObject);
		}
		if(gameObject.getId() == TINDERBOX_CRATE) {
			tinderboxCrate = gameObject;
		}

		if(gameObject.getId() == UNLIT_BRAZIER || gameObject.getId() == LIT_BRAZIER || gameObject.getId() == BROKEN_BRAZIER) {
			for (WintertodtBrazier brazier : braziers)
			{
				brazier.updateGameObject(gameObject);
			}
		}

		if(gameObject.getId() == BRUMA_ROOTS) {
			for (WintertodtBrazier brazier : braziers)
			{
				brazier.updateRoots(gameObject);
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();

		if (npc.getId() == PYROMANCER || npc.getId() == DEAD_PYROMANCER) {
			for (WintertodtBrazier brazier : braziers) {
				brazier.updatePyromancer(npc);
			}
		}
	}
}
