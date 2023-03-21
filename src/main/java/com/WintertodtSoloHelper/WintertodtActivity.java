package com.WintertodtSoloHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum WintertodtActivity
{
    IDLE("IDLE"),
    WOODCUTTING("Woodcutting"),
    FLETCHING("Fletching"),
    FEEDING_BRAZIER("Feeding"),
    FIXING_BRAZIER("Fixing"),
    LIGHTING_BRAZIER("Lighting"),
    PICKING_HERBS("Picking herbs"),
    MAKING_POTIONS("Making Potions");

    private final String actionString;
}
