package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;

import java.util.Arrays;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.HOME_BASE;

public class HomeBaseRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return Arrays.asList(
                new BoardLocationRule(List.of(new Location(boardSize - 2, 1)), HOME_BASE, 1),
                new BoardLocationRule(List.of(new Location(1, boardSize - 2)), HOME_BASE, 1)
        );
    }
}
