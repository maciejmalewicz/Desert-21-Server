package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.EMPTY_FIELD;

public class BoardUtils {

    public static boolean ownsAtLeastOneLocation(Field[][] allFields, List<Location> locations, Player player) {
        return fieldsAtLocations(allFields, locations).stream()
                .filter(f -> player.getId().equals(f.getOwnerId()))
                .anyMatch(p -> true);
    }

    public static Field fieldAtLocation(Field[][] allFields, Location location) throws NotAcceptableException {
        if (!isWithinBoardBounds(allFields, location)){
            throw new NotAcceptableException("Selected field is not within board bounds!");
        }
        return allFields[location.row()][location.col()];
    }

    public static List<Field> fieldsAtLocations(Field[][] allFields, List<Location> locations) {
        return locations.stream()
                .filter(l -> isWithinBoardBounds(allFields, l))
                .map(l -> allFields[l.row()][l.col()])
                .collect(Collectors.toList());
    }

    public static boolean isWithinBoardBounds(Field[][] allFields, Location location) {
        return LocationUtils.isWithinBounds(location, 0, allFields.length - 1, 0, allFields[0].length - 1);
    }

    public static Field[][] generateEmptyPlain(int size) {
        return IntStream.range(0, size)
                .mapToObj(list -> IntStream.range(0, size)
                        .mapToObj(i -> new Field(new Building(EMPTY_FIELD)))
                )
                .map(stream -> stream.toArray(Field[]::new))
                .toArray(Field[][]::new);
    }

    public static List<Field> boardToFieldList(Field[][] fields) {
        return Arrays.stream(fields).flatMap(Arrays::stream).toList();
    }

    public static List<Field> boardToOwnedFieldList(Field[][] fields, String playerId) {
        return boardToFieldList(fields).stream()
                .filter(f -> playerId.equals(f.getOwnerId()))
                .toList();
    }
}
