package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;

public record BuildingSufficientForUnitsTrainingValidatable(Building building, UnitType unitType) implements ActionValidatable {
}
