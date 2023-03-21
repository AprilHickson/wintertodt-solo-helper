/*
 * Copyright (c) 2018, terminatusx <jbfleischman@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.wintertodtafk;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
class WintertodtSoloHelperOverlay extends OverlayPanel
{
	private final WintertodtSoloHelperPlugin plugin;
	private final WintertodtSoloHelperConfig wintertodtAFKConfig;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private WintertodtSoloHelperOverlay(WintertodtSoloHelperPlugin plugin, WintertodtSoloHelperConfig wintertodtAFKConfig, ModelOutlineRenderer modelOutlineRenderer)
	{
		super(plugin);
		this.plugin = plugin;
		this.wintertodtAFKConfig = wintertodtAFKConfig;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.BOTTOM_LEFT);
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Wintertodt Afk overlay");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInWintertodt())
		{
			return null;
		}

		if(wintertodtAFKConfig.showOverlay()) {
			panelComponent.getChildren().add(LineComponent.builder()
					.left("Wintertodt Health: ")
					.leftColor(Color.WHITE)
					.right(String.valueOf(plugin.getWintertodtHealth()))
					.rightColor(Color.WHITE)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Lit Braziers: ")
					.leftColor(Color.WHITE)
					.right(String.valueOf(plugin.getLitBrazierCount()))
					.rightColor(Color.WHITE)
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Potion Doses: ")
					.leftColor(wintertodtAFKConfig.minPotionDose() <= plugin.getPotionDoseCount() ? Color.WHITE : wintertodtAFKConfig.getHighlightColor())
					.right(String.valueOf(plugin.getPotionDoseCount()))
					.rightColor(wintertodtAFKConfig.minPotionDose() <= plugin.getPotionDoseCount() ? Color.WHITE : wintertodtAFKConfig.getHighlightColor())
					.build());

			if(wintertodtAFKConfig.warnForHammer() && !plugin.isHasHammer()) {
				panelComponent.getChildren().add(TitleComponent.builder()
						.text("Missing Hammer")
						.color(wintertodtAFKConfig.getHighlightColor())
						.build());
			}

			if(wintertodtAFKConfig.warnForKnife() && !plugin.isHasKnife()) {
				panelComponent.getChildren().add(TitleComponent.builder()
						.text("Missing Knife")
						.color(wintertodtAFKConfig.getHighlightColor())
						.build());
			}

			if(wintertodtAFKConfig.warnForTinderbox() && !plugin.isHasTinderbox()) {
				panelComponent.getChildren().add(TitleComponent.builder()
						.text("Missing Tinderbox")
						.color(wintertodtAFKConfig.getHighlightColor())
						.build());
			}
		}

		if(wintertodtAFKConfig.highlightObjects())
		{
			if(wintertodtAFKConfig.warnForHammer() && !plugin.isHasHammer()) {
				modelOutlineRenderer.drawOutline(plugin.getHammerCrate(), 6, wintertodtAFKConfig.getHighlightColor(), 6);
			}

			if(wintertodtAFKConfig.warnForKnife() && !plugin.isHasKnife()) {
				modelOutlineRenderer.drawOutline(plugin.getKnifeCrate(), 6, wintertodtAFKConfig.getHighlightColor(), 6);
			}

			if(wintertodtAFKConfig.warnForTinderbox() && !plugin.isHasTinderbox()) {
				modelOutlineRenderer.drawOutline(plugin.getTinderboxCrate(), 6, wintertodtAFKConfig.getHighlightColor(), 6);
			}

			if(plugin.getPotionDoseCount() < wintertodtAFKConfig.minPotionDose()) {
				if(plugin.getUnfinishedCount() == 0) {
					//loop through potioncrates
					for (TileObject crate : plugin.getPotionCrates()) {
						modelOutlineRenderer.drawOutline(crate, 6, wintertodtAFKConfig.getHighlightColor(), 6);
					}
				}
				if(plugin.getHerbCount() == 0) {
					for (TileObject root : plugin.getSproutingRoots()) {
						modelOutlineRenderer.drawOutline(root, 6, wintertodtAFKConfig.getHighlightColor(), 6);
					}
				}
			}

			for (WintertodtBrazier brazier : plugin.getBraziers()) {
				brazier.render(modelOutlineRenderer, wintertodtAFKConfig, plugin);
			}
		}

		return super.render(graphics);
	}
}