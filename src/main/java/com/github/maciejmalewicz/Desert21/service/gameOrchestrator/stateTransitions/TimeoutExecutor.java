package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TimeoutExecutor {

    private final TimeoutExecutablePicker timeoutExecutablePicker;
    private final GameRepository gameRepository;
    private final PlayersNotifier playersNotifier;

    public TimeoutExecutor(TimeoutExecutablePicker timeoutExecutablePicker,
                           GameRepository gameRepository,
                           PlayersNotifier playersNotifier) {
        this.timeoutExecutablePicker = timeoutExecutablePicker;
        this.gameRepository = gameRepository;
        this.playersNotifier = playersNotifier;

        //critical line when the app reloads
        wakeUpStateTransitions();
    }

    public void executeTimeoutOnGame(Game gameBeforeExecution) {
        var thread = new Thread(() -> {
            var executable = timeoutExecutablePicker.pickTimeoutExecutable(gameBeforeExecution);
            var timeoutId = gameBeforeExecution.getStateManager().getCurrentStateTimeoutId();
            var msToTimeout = DateUtils.millisecondsTo(gameBeforeExecution.getStateManager().getTimeout());
            var msToTimeoutWithOffset = msToTimeout + executable.getExecutionOffset();
            msToTimeoutWithOffset = Math.max(msToTimeoutWithOffset, 0);
            try {
                Thread.sleep(msToTimeoutWithOffset);
            } catch (InterruptedException exc) {
                //ignore
            }
            var gameOptional = gameRepository.findById(gameBeforeExecution.getId());
            if (gameOptional.isEmpty()) {
                return;
            }
            var game = gameOptional.get();
            var currentGameTimeoutId = game.getStateManager().getCurrentStateTimeoutId();
            if (!timeoutId.equals(currentGameTimeoutId)) {
                return;
            }

            game.getStateManager().setCurrentlyTimedOut(true); //important flag for further logic

            var transitionService = executable.getStateTransitionService(game);
            transitionService.stateTransition(game);

            var timeoutNotifiable = executable.getNotifications(game);
            timeoutNotifiable.ifPresent(notification -> playersNotifier.notifyPlayers(game, notification));
        });
        thread.start();
    }

    private void wakeUpStateTransitions() {
        gameRepository
                .findAll()
                .forEach(this::executeTimeoutOnGame);
    }
}
