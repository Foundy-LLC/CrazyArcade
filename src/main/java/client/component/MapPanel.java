package client.component;

import client.service.Api;
import domain.constant.Sizes;
import domain.model.*;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class MapPanel extends JPanel {

    public static final Offset MAP_LEFT_TOP = new Offset(26, 53);

    private Map map;

    private List<Player> players;

    private boolean isDisposed = false;

    public MapPanel() {
        setLayout(null);
        setBounds(MAP_LEFT_TOP.x, MAP_LEFT_TOP.y, Sizes.MAP_WIDTH, Sizes.MAP_HEIGHT);
        setBackground(Color.BLACK);

        new RepaintThread().start();
    }

    public void repaint(Map map, List<Player> players) {
        this.map = map;
        this.players = players;
        repaint();
    }

    private void paintTiles(Graphics g) {
        Tile[][] tile2d = map.getTile2d();
        for (int y = 0; y < tile2d.length; ++y) {
            for (int x = 0; x < tile2d[y].length; ++x) {
                Tile tile = tile2d[y][x];
                if (tile != null) {
                    Offset objectOffset = new Offset(x * Sizes.TILE_SIZE.width, y * Sizes.TILE_SIZE.height);
                    new TileRenderElement(tile).draw(g, objectOffset);
                }
            }
        }
    }

    private void paintPlayerAndItemAndBlock(Graphics g) {
        final String currentUserName = Api.getInstance().getUserName();
        Item[][] item2d = map.getItem2d();
        Block[][] block2d = map.getBlock2d();

        for (int y = 0; y < block2d.length; ++y) {
            for (int x = 0; x < block2d[y].length; ++x) {
                Offset tileOffset = new Offset(x, y);
                Block block = block2d[y][x];
                List<Player> players = this.players.stream()
                        .filter((player) -> player.getCenterTileOffset().equals(tileOffset))
                        .collect(Collectors.toList());

                if (block != null) {
                    Offset objectOffset = new Offset(tileOffset.x * Sizes.TILE_SIZE.width, tileOffset.y * Sizes.TILE_SIZE.height);
                    new BlockRenderElement(block).draw(g, objectOffset);
                }

                if (item2d[y][x] != null) {
                    Item item = item2d[y][x];
                    new ItemRenderElement(item).draw(g, new Offset(x * Sizes.TILE_SIZE.width, y * Sizes.TILE_SIZE.height));
                }

                players.forEach((player) -> {
                    if (!player.shouldBeRemoved()) {
                        Offset playerOffset = player.getOffset();
                        new PlayerRenderElement(player).draw(g, playerOffset);
                        if (player.getName().equals(currentUserName)) {
                            Offset arrowOffset = new Offset(
                                    playerOffset.x,
                                    playerOffset.y - PlayerRenderElement.NORMAL_ONE_FRAME_SIZE.height
                            );
                            new ArrowRenderElement().draw(g, arrowOffset);
                        }
                    }
                });
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
                    new WaterBombRenderElement(waterBomb).draw(g, new Offset(x * tileWidth, y * tileHeight));
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
                    new WaterWaveRenderElement(waterWave).draw(g, new Offset(x * tileWidth, y * tileHeight));
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (map == null) {
            return;
        }

        paintTiles(g);
        paintWaterWaves(g);
        paintWaterBombs(g);
        paintPlayerAndItemAndBlock(g);

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
