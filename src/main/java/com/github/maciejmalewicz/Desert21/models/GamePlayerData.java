package com.github.maciejmalewicz.Desert21.models;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;

public record GamePlayerData(
        Game game,
        Player player
) {
}
