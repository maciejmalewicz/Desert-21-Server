package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@Data
@Document("current_games")
public class Game {

    @Id
    private String id;

    private List<Player> players;

    private Field[][] fields;

    public Game(List<Player> players, Field[][] fields) {
        this.players = players;
        this.fields = fields;
    }

    private Field fieldAt(int row, int col) {
        return fields[row][col];
    }
}
