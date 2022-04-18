package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.FieldOwnershipValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldOwnershipValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private FieldOwnershipValidator tested;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                new Game(
                        List.of(
                                player,
                                new Player("BB",
                                        "schabina123456",
                                        new ResourceSet(60, 60, 60))),
                        new Field[9][9],
                        new StateManager(
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.METAL_FACTORY), "AA");

        context.game().getFields()[0][3] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");
        context.game().getFields()[0][4] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");
        context.game().getFields()[0][5] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");
    }

    @Test
    void validateOnlyOwnedFields() {
        var validatables = List.of(
                new FieldOwnershipValidatable(context.game().getFields()[0][0], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][1], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][2], context.player())
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateNotOnlyOwnedFields() {
        var validatables = List.of(
                new FieldOwnershipValidatable(context.game().getFields()[0][0], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][1], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][2], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][3], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][4], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][5], context.player())
        );
        assertFalse(tested.validate(validatables, context));
    }
}