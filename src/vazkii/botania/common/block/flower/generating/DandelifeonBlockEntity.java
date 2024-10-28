package vazkii.botania.common.block.flower.generating;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.CellularBlockEntity;

public class DandelifeonBlockEntity extends GeneratingFlowerBlockEntity {
    public static final int RANGE = 12;

    public static final int SPEED = 10;

    public static final int MAX_MANA_GENERATIONS = 100;

    public static final int MANA_PER_GEN = 60;

    private static final String TAG_RADIUS = "radius";

    private int radius = 12;

    private static final int[][] ADJACENT_BLOCKS = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};

    public DandelifeonBlockEntity(BlockPos pos, BlockState state) {
        super(BotaniaFlowerBlocks.DANDELIFEON, pos, state);
    }

    public int getRange() {
        return this.radius;
    }

    public void tickFlower() {
        super.tickFlower();
        if (!(m_58904_()).f_46443_ &&
                m_58904_().m_46467_() % 10L == 0L && m_58904_().m_276867_(m_58899_()))
            runSimulation();
    }

    private void runSimulation() {
        CellTable table = new CellTable(this.radius, this);
        List<LifeUpdate> changes = new ArrayList<>();
        for (int i = 0; i < table.diameter; i++) {
            for (int j = 0; j < table.diameter; j++) {
                int newLife, oldLife = table.at(i, j);
                int adj = table.getAdjCells(i, j);
                if (adj == 3 && oldLife == -1) {
                    newLife = table.getSpawnCellGeneration(i, j);
                } else if ((adj == 2 || adj == 3) && Cell.isLive(oldLife)) {
                    newLife = oldLife + 1;
                } else {
                    newLife = -1;
                }
                int xdist = Math.abs(i - this.radius);
                int zdist = Math.abs(j - this.radius);
                int allowDist = 1;
                if (xdist <= allowDist && zdist <= allowDist && Cell.isLive(newLife))
                    if (oldLife == 1) {
                        newLife = -1;
                    } else {
                        oldLife = newLife;
                        newLife = -2;
                    }
                if (newLife != oldLife)
                    changes.add(new LifeUpdate(i, j, newLife, oldLife));
            }
        }
        for (LifeUpdate change : changes) {
            BlockPos pos_ = table.center.m_7918_(-this.radius + change.x(), 0, -this.radius + change.z());
            int newLife = change.newLife();
            setBlockForGeneration(pos_, Math.min(newLife, 100), change.oldLife());
        }
    }

    void setBlockForGeneration(BlockPos pos, int cell, int prevCell) {
        Level world = m_58904_();
        BlockState stateAt = world.m_8055_(pos);
        BlockEntity tile = world.m_7702_(pos);
        if (cell == -2) {
            int val = prevCell * 60;
            world.m_7471_(pos, true);
            addMana(val);
            sync();
        } else if (tile instanceof CellularBlockEntity) {
            CellularBlockEntity cellBlock = (CellularBlockEntity) tile;
            cellBlock.setNextGeneration(this, cell);
        } else if (Cell.isLive(cell) && stateAt.m_60795_()) {
            world.m_46597_(pos, BotaniaBlocks.cellBlock.m_49966_());
            tile = world.m_7702_(pos);
            ((CellularBlockEntity) tile).setNextGeneration(this, cell);
            ((CellularBlockEntity) tile).setGeneration(-1);
        }
    }

    public static final class Cell {
        public static final int CONSUME = -2;

        public static final int DEAD = -1;

        public static boolean isLive(int i) {
            return (i >= 0);
        }

        public static int boundaryPunish(int life) {
            return isLive(life) ? (life / 4) : life;
        }
    }

    private static class CellTable {
        public final BlockPos center;

        public final int diameter;

        private int[][] cells;

        public CellTable(int range, DandelifeonBlockEntity dandie) {
            this.center = dandie.getEffectivePos();
            this.diameter = range * 2 + 1;
            this.cells = new int[this.diameter + 2][this.diameter + 2];
            for (int i = -1; i <= this.diameter; i++) {
                for (int j = -1; j <= this.diameter; j++) {
                    BlockPos pos = this.center.m_7918_(-range + i, 0, -range + j);
                    this.cells[i + 1][j + 1] = getCellGeneration(pos, dandie);
                }
            }
        }

        private static int getCellGeneration(BlockPos pos, DandelifeonBlockEntity dandie) {
            BlockEntity tile = dandie.m_58904_().m_7702_(pos);
            if (tile instanceof CellularBlockEntity) {
                CellularBlockEntity cell = (CellularBlockEntity) tile;
                return cell.isSameFlower(dandie) ? cell.getGeneration() : DandelifeonBlockEntity.Cell.boundaryPunish(cell.getGeneration());
            }
            return -1;
        }

        public boolean inBounds(int x, int z) {
            return (x >= 0 && z >= 0 && x < this.diameter && z < this.diameter);
        }

        public int getAdjCells(int x, int z) {
            int count = 0;
            for (int[] shift : DandelifeonBlockEntity.ADJACENT_BLOCKS) {
                if (DandelifeonBlockEntity.Cell.isLive(at(x + shift[0], z + shift[1])))
                    count++;
            }
            return count;
        }

        public int getSpawnCellGeneration(int x, int z) {
            int max = -1;
            for (int[] shift : DandelifeonBlockEntity.ADJACENT_BLOCKS)
                max = Math.max(max, at(x + shift[0], z + shift[1]));
            return (max == -1) ? -1 : (max + 1);
        }

        public int at(int x, int z) {
            return this.cells[x + 1][z + 1];
        }
    }

    private record LifeUpdate(int x, int z, int newLife, int oldLife) {
    }

    public RadiusDescriptor getRadius() {
        return (RadiusDescriptor) RadiusDescriptor.Rectangle.square(getEffectivePos(), this.radius);
    }

    public RadiusDescriptor getSecondaryRadius() {
        return (RadiusDescriptor) RadiusDescriptor.Rectangle.square(getEffectivePos(), 1);
    }

    public int getMaxMana() {
        return 50000;
    }

    public int getColor() {
        return 10226302;
    }

    public void writeToPacketNBT(CompoundTag cmp) {
        super.writeToPacketNBT(cmp);
        if (this.radius != 12)
            cmp.m_128405_("radius", this.radius);
    }

    public void readFromPacketNBT(CompoundTag cmp) {
        super.readFromPacketNBT(cmp);
        this.radius = cmp.m_128441_("radius") ? cmp.m_128451_("radius") : 12;
    }
}
