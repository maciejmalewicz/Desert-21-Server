package com.github.maciejmalewicz.Desert21.models.balance.buildings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RocketLauncherConfig extends BuildingConfig {
    private int firstUseCost;
    private int nextUseCostIncrease;
}
