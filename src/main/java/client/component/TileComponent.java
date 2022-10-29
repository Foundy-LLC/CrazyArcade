package client.component;

import domain.constant.Sizes;
import domain.model.Offset;
import domain.model.Tile;
import lombok.NonNull;

import javax.swing.*;

public class TileComponent extends GameComponent<Tile> {

    public TileComponent(@NonNull ImageIcon imageIcon, @NonNull Offset tileOffset) {
        super(imageIcon, Sizes.TILE_SIZE);
        int x = tileOffset.x * Sizes.TILE_SIZE.width;
        int y = tileOffset.y * Sizes.TILE_SIZE.height;
        Offset offset = new Offset(x, y);
        setOffset(offset);
    }

    @Override
    public void updateState(Tile tile) {

    }
}
