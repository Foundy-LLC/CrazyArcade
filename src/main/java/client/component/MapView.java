package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.*;

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

    private void paintBlocks(Graphics g) {
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
    }

    private void paintWaterBombs(Graphics g) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        final int tileWidth = Sizes.TILE_SIZE.width;
        final int tileHeight = Sizes.TILE_SIZE.height;

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                if (waterBomb2d[y][x] != null) {
                    WaterBomb waterBomb = waterBomb2d[y][x];
                    int frame = waterBomb.getFrame();
                    WaterBombComponent waterBombComponent = new WaterBombComponent(new Offset(x, y));
                    Offset renderOffset = waterBombComponent.getRenderOffset();
                    Dimension renderSize = waterBombComponent.getImageSize();

                    g.drawImage(
                            waterBombComponent.getImage(),
                            renderOffset.x,
                            renderOffset.y,
                            renderOffset.x + tileWidth,
                            renderOffset.y + tileHeight,
                            frame * renderSize.width,
                            0,
                            (frame + 1) * renderSize.width,
                            renderSize.height,
                            null);
                }
            }
        }
    }

    private void paintWaterWaves(Graphics g) {
        WaterWave[][] waterWave2d = map.getWaterWave2d();
        final int tileWidth = Sizes.TILE_SIZE.width;
        final int tileHeight = Sizes.TILE_SIZE.height;

        for (int y = 0; y < waterWave2d.length; ++y) {
            for (int x = 0; x < waterWave2d[y].length; ++x) {
                if (waterWave2d[y][x] != null) {
                    WaterWave waterWave = waterWave2d[y][x];
                    int frame = waterWave.getFrame();
                    ImageIcon imageIcon = getWaterWaveImageIcon(waterWave);
                    WaterWaveComponent waterWaveComponent = new WaterWaveComponent(
                            imageIcon,
                            new Dimension(52, 52),
                            new Offset(x, y));
                    Offset renderOffset = waterWaveComponent.getRenderOffset();
                    Dimension renderSize = waterWaveComponent.getImageSize();

                    g.drawImage(
                            waterWaveComponent.getImage(),
                            renderOffset.x,
                            renderOffset.y,
                            renderOffset.x + tileWidth,
                            renderOffset.y + tileHeight,
                            frame * renderSize.width,
                            0,
                            (frame + 1) * renderSize.width,
                            renderSize.height,
                            null);
                }
            }
        }
    }

    private ImageIcon getWaterWaveImageIcon(WaterWave waterWave) {
        Direction direction = waterWave.getDirection();
        if (direction == null) {
            return ImageIcons.WATER_BOMB_POP;
        }
        return switch (waterWave.getDirection()) {
            case UP -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_UP_END : ImageIcons.WATER_WAVE_UP;
            case DOWN -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_DOWN_END : ImageIcons.WATER_WAVE_DOWN;
            case LEFT -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_LEFT_END : ImageIcons.WATER_WAVE_LEFT;
            case RIGHT -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_RIGHT_END : ImageIcons.WATER_WAVE_RIGHT;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (map == null) {
            return;
        }

        paintBlocks(g);
        paintWaterWaves(g);
        paintWaterBombs(g);

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
