package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.testConfig.TestUtils.findAllWithinCollection;
import static com.github.maciejmalewicz.Desert21.testConfig.TestUtils.findWithinCollection;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BombardActionTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

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
                        BoardUtils.generateEmptyPlain(9),
                        new StateManager(
                                GameState.WAITING_TO_START,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
    }

    @Test
    void getActionValidatablesPathTooShort() {
        var fromLocation = new Location(0, 0);
        var toLocation = new Location(1, 1);
        var path = List.of(new Location(0, 0));

        var action = new BombardAction(fromLocation, toLocation, path, 50);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            action.getActionValidatables(context);
        });
        assertEquals("Path is too short!", exception.getMessage());
    }

    @Test
    void getActionValidatablesToFieldOutOfBounds() {
        var fromLocation = new Location(1, 0);
        var toLocation = new Location(-1, 99);
        var path = List.of(new Location(0, 0), new Location(1, 0), new Location(1, 1));

        var action = new BombardAction(fromLocation, toLocation, path, 50);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            action.getActionValidatables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test
    void getActionValidatablesShouldNotCrashOnPathOutOfBounds() throws NotAcceptableException {
        var fromLocation = new Location(0, 0);
        var toLocation = new Location(1, 1);
        var path = List.of(new Location(0, 0), new Location(-1, 99), new Location(1, 1));

        var action = new BombardAction(fromLocation, toLocation, path, 50);
        var validatables = action.getActionValidatables(context);

        var fieldOwnershipValidatables = findAllWithinCollection(validatables, FieldOwnershipValidatable.class);
        var expectedFieldOwnershipValidatables = List.of(
                new FieldOwnershipValidatable(
                        context.game().getFields()[0][0],
                        context.player()
                ),
                new FieldOwnershipValidatable(
                        null,
                        context.player()
                )
        );
        assertThat(expectedFieldOwnershipValidatables, sameBeanAs(fieldOwnershipValidatables));
    }

    @Test
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        var fromLocation = new Location(0, 0);
        var toLocation = new Location(1, 1);
        var path = List.of(new Location(0, 0), new Location(1, 0), new Location(1, 1));
        var army = new Army(0, 0, 50);

        var action = new BombardAction(fromLocation, toLocation, path, army.getCannons());

        var validatables = action.getActionValidatables(context);
        assertEquals(11, validatables.size());

        var pathLengthValidatable = findWithinCollection(validatables, PathLengthValidatable.class);
        var expectedPathLengthValidatable = new PathLengthValidatable(path, army);
        assertThat(expectedPathLengthValidatable, sameBeanAs(pathLengthValidatable));

        var pathConvergenceValidatable = findWithinCollection(validatables, PathFromAndToConvergenceValidatable.class);
        var expectedPathConvergenceValidatable = new PathFromAndToConvergenceValidatable(path, fromLocation, toLocation);
        assertThat(expectedPathConvergenceValidatable, sameBeanAs(pathConvergenceValidatable));

        var pathContinuityValidatable = findWithinCollection(validatables, PathContinuityValidatable.class);
        var expectedPathContinuityValidatable = new PathContinuityValidatable(path);
        assertThat(expectedPathContinuityValidatable, sameBeanAs(pathContinuityValidatable));

        var locationBoundsValidatables = findAllWithinCollection(validatables, LocationBoundsValidatable.class);
        var expectedLocationBoundsValidatables = path.stream().map(LocationBoundsValidatable::new).toList();
        assertThat(expectedLocationBoundsValidatables, sameBeanAs(locationBoundsValidatables));

        var fieldOwnershipValidatables = findAllWithinCollection(validatables, FieldOwnershipValidatable.class);
        var expectedFieldOwnershipValidatables = List.of(
                new FieldOwnershipValidatable(context.game().getFields()[0][0], context.player()),
                new FieldOwnershipValidatable(context.game().getFields()[0][1], context.player())
        );
        assertThat(expectedFieldOwnershipValidatables, sameBeanAs(fieldOwnershipValidatables));

        var isFieldEnemyValidatable = findWithinCollection(validatables, IsFieldEnemyValidatable.class);
        var expectedIsFieldEnemyValidatable = new IsFieldEnemyValidatable(context.game().getFields()[1][1], context.player());
        assertThat(expectedIsFieldEnemyValidatable, sameBeanAs(isFieldEnemyValidatable));

        var enoughUnitsValidatable = findWithinCollection(validatables, EnoughUnitsValidatable.class);
        var expectedEnoughUnitsValidatable = new EnoughUnitsValidatable(army, new Location(0, 0));
        assertThat(expectedEnoughUnitsValidatable, sameBeanAs(enoughUnitsValidatable));

        var isBombardingUnlockedValidatable = findWithinCollection(validatables, IsBombardingUnlockedValidatable.class);
        var expectedIsBombardingUnlockedValidatable = new IsBombardingUnlockedValidatable();
        assertThat(expectedIsBombardingUnlockedValidatable, sameBeanAs(isBombardingUnlockedValidatable));
    }

    @Test
    void getEventExecutables() throws NotAcceptableException {
        var fromLocation = new Location(0, 0);
        var toLocation = new Location(1, 1);
        var path = List.of(new Location(0, 0), new Location(1, 0), new Location(1, 1));

        var action = new BombardAction(fromLocation, toLocation, path, 50);
        var executables = action.getEventExecutables(context);
        var expectedExecutables = List.of(new BombardingEvent(new Location(1, 1), 50));
        assertThat(executables, sameBeanAs(executables));
    }
}