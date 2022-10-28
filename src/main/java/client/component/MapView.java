package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Block;
import domain.model.Map;
import domain.model.Offset;
import domain.model.WaterBomb;

import javax.swing.*;
import java.awt.*;

public class MapView extends JPanel {

    public static final Offset MAP_LEFT_TOP = new Offset(26, 53);

    private Map map;

    private boolean isDisposed = false;

    public MapView() {
        setLayout(null);
        setBounds(MAP_LEFT_TOP.x, MAP_LEFT_TOP.y, Sizes.MAP_WIDTH, Sizes.MAP_HEIGHT);
        setBackground(Color.BLACK);

        new RepaintThread().start();
    }

    public void updateMap(Map map) {
        this.map = map;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (map == null) {
            return;
        }

        Block[][] block2d = map.getBlock2D();
        for (int y = 0; y < block2d.length; ++y) {
            for (int x = 0; x < block2d[y].length; ++x) {
                if (block2d[y][x] != null) {
                    BlockComponent blockComponent = new BlockComponent(new Offset(x, y));
                    Offset renderOffset = blockComponent.getRenderOffset();
                    g.drawImage(blockComponent.getImage(), renderOffset.x, renderOffset.y, null);
                }
            }
        }

        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        final int tileWidth = Sizes.TILE_SIZE.width;
        final int tileHeight = Sizes.TILE_SIZE.height;

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                if (waterBomb2d[y][x] != null) {
                    WaterBomb waterBombState = waterBomb2d[y][x];
                    int frame = waterBombState.getFrame();
                    WaterBombComponent blockComponent = new WaterBombComponent(new Offset(x, y));
                    Offset renderOffset = blockComponent.getRenderOffset();
                    g.drawImage(
                            blockComponent.getImage(),
                            renderOffset.x,
                            renderOffset.y,
                            renderOffset.x + tileWidth,
                            renderOffset.y + tileHeight,
                            frame * WaterBombComponent.SIZE.width,
                            0,
                            (frame + 1) * WaterBombComponent.SIZE.width,
                            WaterBombComponent.SIZE.height,
                            null);
                }
            }
        }

        setOpaque(false);
    }

    public void dispose() {
        isDisposed = true;
    }

    private class RepaintThread extends Thread {
        @Override
        public void run() {
            while (!isDisposed) {
                try {
                    sleep(200);
                    repaint();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
