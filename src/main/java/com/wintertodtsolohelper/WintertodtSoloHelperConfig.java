/*
 * Copyright (c) 2018, terminatusx <jbfleischman@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, loldudester <HannahRyanster@gmail.com>
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

import net.runelite.client.config.*;

@ConfigGroup("wintertodt-afk")
public interface WintertodtSoloHelperConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Toggles the status overlay"
    )
    default boolean showOverlay()
    {
        return true;
    }

    //highlight game objects
    @ConfigItem(
            position = 1,
            keyName = "highlightObjects",
            name = "Highlight Objects",
            description = "Highlights objects"
    )
    default boolean highlightObjects()
    {
        return true;
    }

    // What point goal before ending
    @ConfigItem(
            position = 2,
            keyName = "pointGoal",
            name = "Point Goal",
            description = "What point goal before ending"
    )
    @Range(
            min = 0,
            max = 13500
    )
    default int pointGoal()
    {
        return 13500;
    }

    // which brazier you prefer to use
    @ConfigItem(
            position = 2,
            keyName = "brazier",
            name = "Brazier",
            description = "Which brazier you prefer to use"
    )
    default BrazierLocation brazier()
    {
        return BrazierLocation.SouthEast;
    }

    @ConfigSection(
            name = "Percentages",
            description = "All the options for how you solo Wintertodt",
            position = 3,
            closedByDefault = false
    )
    String percentages = "percentages";

    // Minimum Relight Health
    @ConfigItem(
            position = 0,
            keyName = "minRelightHealth",
            name = "Minimum Relight Health",
            description = "Minimum health to relight the main brazier",
            section = percentages
    )
    @Range(
            min = 1,
            max = 100
    )
    @Units(Units.PERCENT)
    default int minRelightHealth()
    {
        return 6;
    }

    // Always Relight Health
    @ConfigItem(
            position = 1,
            keyName = "alwaysRelightHealth",
            name = "Always Relight Health",
            description = "Always relight the fire above this health",
            section = percentages
    )
    @Range(
            min = 2,
            max = 100
    )
    @Units(Units.PERCENT)
    default int alwaysRelightHealth()
    {
        return 11;
    }

    // what percentage to relight multiple fires
    @ConfigItem(
            position = 2,
            keyName = "multiFireRelightPercentage",
            name = "Multi Fire Relight Percentage",
            description = "What percentage to relight multiple fires",
            section = percentages
    )
    @Range(
            min = 2,
            max = 100
    )
    @Units(Units.PERCENT)
    default int multiFireRelightPercentage()
    {
        return 25;
    }

    @ConfigSection(
            name = "Items",
            description = "Should the plugin highlight crates for hammers, knifes, etc",
            position = 4,
            closedByDefault = true
    )
    String items = "items";

    // Should warn for hammer
    @ConfigItem(
            position = 0,
            keyName = "warnForHammer",
            name = "Warn for Hammer",
            description = "Warns you if you don't have a hammer",
            section = items
    )
    default boolean warnForHammer()
    {
        return true;
    }

    // should warn for knife
    @ConfigItem(
            position = 1,
            keyName = "warnForKnife",
            name = "Warn for Knife",
            description = "Warns and highlights you if you don't have a knife",
            section = items
    )
    default boolean warnForKnife()
    {
        return true;
    }

    //should warn for tinderbox
    @ConfigItem(
            position = 2,
            keyName = "warnForTinderbox",
            name = "Warn for Tinderbox",
            description = "Warns and highlights you if you don't have a tinderbox",
            section = items
    )
    default boolean warnForTinderbox()
    {
        return true;
    }

    //min potion dose
    @ConfigItem(
            position = 3,
            keyName = "minPotionDose",
            name = "Minimum Potion Dose",
            description = "Warns and highlights when you have less than this many doses of bruma potion",
            section = items
    )
    @Range(
            min = 0,
            max = 40
    )
    default int minPotionDose()
    {
        return 4;
    }

    @ConfigSection(
            name = "Misc",
            description = "Miscellaneous options",
            position = 5,
            closedByDefault = true
    )
    String misc = "misc";

    //always repair broken
    @ConfigItem(
            position = 0,
            keyName = "alwaysRepairBroken",
            name = "Always Repair Broken",
            description = "Always repair broken brazier",
            section = misc
    )
    default boolean alwaysRepairBroken()
    {
        return true;
    }
}