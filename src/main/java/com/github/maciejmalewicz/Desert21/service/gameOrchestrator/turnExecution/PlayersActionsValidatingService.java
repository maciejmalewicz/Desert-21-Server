package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeNotRepeatedValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.mongodb.internal.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PlayersActionsValidatingService {

    private final List<ActionValidator<?>> actionValidators;

    @Autowired
    public PlayersActionsValidatingService(
            BuildingUpgradableValidator buildingUpgradableValidator,
            CostValidator costValidator,
            FieldOwnershipValidator fieldOwnershipValidator,
            FieldNonOwnershipValidator fieldNonOwnershipValidator,
            LocationBoundsValidator locationBoundsValidator,
            SingleUpgradePerLocationValidator singleUpgradePerLocationValidator,
            BuildingSufficientForUnitsTrainingValidator buildingSufficientForUnitsTrainingValidator,
            NoPendingTrainingsValidator noPendingTrainingsValidator,
            ProductionTypeUpgradesOwnershipValidator productionTypeUpgradesOwnershipValidator,
            SingleTrainingPerLocationValidator singleTrainingPerLocationValidator,
            EnoughUnitsValidator enoughUnitsValidator,
            PathFromAndToConvergenceValidator pathFromAndToConvergenceValidator,
            PathContinuityValidator pathContinuityValidator,
            PathLengthValidator pathLengthValidator,
            LabUpgradeHierarchyValidator labUpgradeHierarchyValidator,
            SingleUpgradePerBranchValidator singleUpgradePerBranchValidator,
            LabUpgradeNotRepeatedValidator labUpgradeNotRepeatedValidator,
            SingleRocketStrikePerTurnValidator singleRocketStrikePerTurnValidator,
            RocketLauncherOwnershipValidator rocketLauncherOwnershipValidator,
            IsFieldTargetableByRocketValidator isFieldTargetableByRocketValidator,
            SuperSonicUpgradeValidator superSonicUpgradeValidator,
            RocketStrikeValidRocketStrikeTargetValidator rocketStrikeValidRocketStrikeTargetValidator,
            IsBuildingBuildableValidator isBuildingBuildableValidator,
            IsFieldEmptyValidator isFieldEmptyValidator,
            HasUpgradeRequiredToBuildValidator hasUpgradeRequiredToBuildValidator,
            SingleBuildPerLocationValidator singleBuildPerLocationValidator,
            IsBombardingUnlockedValidator isBombardingUnlockedValidator,
            IsFieldEnemyValidator isFieldEnemyValidator,
            MaxTowersCapValidator maxTowersCapValidator,
            MaxFactoriesCapValidator maxFactoriesCapValidator,
            HasSufficientLabForBuildingUpgradeValidator hasSufficientLabForBuildingUpgradeValidator
    ) {
        actionValidators = List.of(
                locationBoundsValidator,
                fieldOwnershipValidator,
                fieldNonOwnershipValidator,
                singleUpgradePerLocationValidator,
                buildingUpgradableValidator,
                costValidator,
                singleTrainingPerLocationValidator,
                buildingSufficientForUnitsTrainingValidator,
                productionTypeUpgradesOwnershipValidator,
                noPendingTrainingsValidator,
                enoughUnitsValidator,
                pathFromAndToConvergenceValidator,
                pathContinuityValidator,
                pathLengthValidator,
                labUpgradeHierarchyValidator,
                singleUpgradePerBranchValidator,
                labUpgradeNotRepeatedValidator,
                singleRocketStrikePerTurnValidator,
                rocketLauncherOwnershipValidator,
                isFieldTargetableByRocketValidator,
                superSonicUpgradeValidator,
                rocketStrikeValidRocketStrikeTargetValidator,
                isBuildingBuildableValidator,
                isFieldEmptyValidator,
                hasUpgradeRequiredToBuildValidator,
                singleBuildPerLocationValidator,
                isBombardingUnlockedValidator,
                isFieldEnemyValidator,
                maxTowersCapValidator,
                maxFactoriesCapValidator,
                hasSufficientLabForBuildingUpgradeValidator
        );
    }

    public boolean validatePlayersActions(Collection<Action> actions, TurnExecutionContext context) {
        var validatables = new ArrayList<ActionValidatable>();
        try {
            for (Action action: actions) {
                var validatablesFromAction = action.getActionValidatables(context);
                validatables.addAll(validatablesFromAction);
            }
        } catch (NotAcceptableException exc) {
            return false;
        }
        return actionValidators.stream()
                .allMatch(validator -> executeSingleValidation(validator, validatables, context));
    }

    private <T extends ActionValidatable> boolean executeSingleValidation(
            ActionValidator<T> validator,
            List<ActionValidatable> validatables,
            TurnExecutionContext context
    ) {
        var selectedValidatables = validatables.stream()
                .filter(v -> v.getClass().equals(validator.getValidatableClass()))
                .map(validator.getValidatableClass()::cast)
                .toList();
        return validator.validate(selectedValidatables, context);
    }
}
